# 4. Splitting ViewModels

Splitting is about **ownership of logic**, not about recomposition performance (that's chapter 2).

## MVVM-V1 — One ViewModel per destination (default, MUST)
Each navigation destination gets one ViewModel, created in its Route and scoped to its back-stack
entry. It is cleared when the destination is popped.

## MVVM-V2 — Big ViewModel? Split into delegates, not more ViewModels (SHOULD)
When a VM grows (>~300 lines, or clearly separable concerns), extract plain classes that own one
concern. The VM composes them and remains the single state owner for the screen.

```kotlin
// A delegate: plain class, no ViewModel, gets the VM's scope.
class PromoCodeController(
    private val promoRepository: PromoRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(PromoState())
    val state: StateFlow<PromoState> = _state.asStateFlow()

    fun apply(code: String) {
        _state.update { it.copy(isApplying = true, error = null) }
        scope.launch {
            promoRepository.validate(code)
                .onSuccess { discount -> _state.update { PromoState(applied = discount) } }
                .onFailure { _state.update { it.copy(isApplying = false, error = UiText.Res(R.string.promo_invalid)) } }
        }
    }
}

data class PromoState(
    val applied: Discount? = null,
    val isApplying: Boolean = false,
    val error: UiText? = null,
)

class CheckoutViewModel(
    cartRepository: CartRepository,
    promoRepository: PromoRepository,
) : ViewModel() {
    private val promo = PromoCodeController(promoRepository, viewModelScope)

    val uiState: StateFlow<CheckoutUiState> =
        combine(cartRepository.observeCart(), promo.state) { cart, promo ->
            CheckoutUiState(
                items = cart.items.map { it.toUi() },
                promo = promo,
                total = cart.total - (promo.applied?.amount ?: Money.ZERO),
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CheckoutUiState())

    fun applyPromo(code: String) = promo.apply(code)
}
```

Why delegates over sibling VMs: one `uiState` for the screen (consistency, C3), the delegate can be
unit-tested alone with a `TestScope`, and it can be reused by another VM.

## MVVM-V3 — Extra ViewModel on a screen: only for self-contained, reused sections (MAY)
Allowed when **all** are true:
1. The section has its own data source and logic (it doesn't need the host screen's state).
2. It appears on several screens.
3. The host screen doesn't need to read its state.

Example: a cart badge in the top bar of several screens.

```kotlin
@Composable
fun HomeRoute(
    onOpenCart: () -> Unit,
    homeViewModel: HomeViewModel = koinViewModel(),
    cartBadgeViewModel: CartBadgeViewModel = koinViewModel(),   // both obtained in the Route (L3)
) {
    val home by homeViewModel.uiState.collectAsStateWithLifecycle()
    val cartCount by cartBadgeViewModel.count.collectAsStateWithLifecycle()
    HomeScreen(
        state = home,
        cartCount = cartCount,
        onCartClick = onOpenCart,
    )
}
```

If the host needs the section's state, it's not self-contained → move the logic into the host VM
(as a delegate, V2) or into the data layer (V4).

## MVVM-V4 — Shared across unrelated screens ⇒ repository is the source of truth (MUST)
Two screens that show/modify the same data each have their own VM observing the same repository.
They stay in sync because they observe the same `Flow`, not because they talk to each other.

```kotlin
class CartRepository(private val dao: CartDao) {
    fun observeCart(): Flow<Cart> = dao.observe()            // single source of truth
    suspend fun add(item: ProductId) = dao.insert(item)
}

class ProductViewModel(private val cart: CartRepository) : ViewModel() {
    fun addToCart(id: ProductId) { viewModelScope.launch { cart.add(id) } }
}
class CartBadgeViewModel(cart: CartRepository) : ViewModel() {
    val count = cart.observeCart().map { it.items.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}
```

❌ Never:
```kotlin
class ProductViewModel(private val cartVm: CartBadgeViewModel) : ViewModel()   // L7
fun addToCart(id: ProductId, cartVm: CartBadgeViewModel)                       // L7
```

If data is in-memory only (no DB), the repository holds a `MutableStateFlow` and is scoped as a
singleton (or to the feature's DI component).

## MVVM-V5 — Multi-step flows ⇒ one flow entry owning the shared ViewModel (MUST)
A wizard (checkout: address → payment → review) shares in-progress, not-yet-persisted data.
Navigation 3 has no nested graphs to scope a ViewModel to, so the rule is:

- The whole flow is **one** entry on the app back stack (`CheckoutKey`).
- Its Route owns the flow ViewModel (scoped to that entry by the ViewModel decorator, L9) and
  hosts its **own inner back stack** of steps in a nested `NavDisplay`.
- The flow VM is cleared when `CheckoutKey` is popped, i.e. when the user leaves the whole flow.

```kotlin
@Serializable data object CheckoutKey : NavKey
@Serializable data object AddressStep : NavKey
@Serializable data object PaymentStep : NavKey
@Serializable data object ReviewStep : NavKey

@Composable
fun CheckoutRoute(
    onFinished: () -> Unit,
    flowViewModel: CheckoutFlowViewModel = koinViewModel(),        // scoped to CheckoutKey
) {
    val steps = rememberNavBackStack(AddressStep)                    // inner back stack
    NavDisplay(
        backStack = steps,
        onBack = { steps.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),               // step VMs scoped per step
        ),
        entryProvider = entryProvider {
            entry<AddressStep> {
                AddressRoute(flowViewModel = flowViewModel, onNext = { steps.add(PaymentStep) })
            }
            entry<PaymentStep> {
                PaymentRoute(
                    flowViewModel = flowViewModel,
                    stepViewModel = koinViewModel<PaymentViewModel>(),   // optional step-only logic
                    onNext = { steps.add(ReviewStep) },
                )
            }
            entry<ReviewStep> {
                ReviewRoute(flowViewModel = flowViewModel, onDone = onFinished)
            }
        },
    )
}
```

The flow VM holds the draft (`CheckoutDraft`) and is the only writer of it. Step VMs, if any, own
step-local logic (e.g. card validation) and hand results to their Route, which calls the flow VM —
the two VMs still never reference each other (L7). Passing the flow VM into a step **Route** is
allowed (it's a Route, L3); passing it below the Route is not.

## MVVM-V6 — No Activity-scoped "god" ViewModel (MUST NOT)
An Activity-scoped VM used as an app-wide store lives forever, hides dependencies, and couples every
screen. Use repositories (V4) for app-wide data. Activity scope is acceptable only for state truly
about the Activity (e.g. theme / splash-screen "keep on screen" condition).

## MVVM-V7 — Dialogs and bottom sheets (MUST)
| Case | Owner |
|---|---|
| Dialog is a nav destination (`dialog<…>`) with its own data/logic (e.g. "Rename list" that saves) | Its own ViewModel (V1 applies) |
| Dialog is part of the screen (confirm delete, pick sort order) | Visibility + selection in the **parent** UiState if logic reacts to it; in `remember` if purely visual (S2) |
| Confirm dialog whose result triggers business logic | Visibility in UI, result goes to parent VM via lambda |

```kotlin
// Confirm delete – visibility is UI state, the delete is a VM action
var confirmDelete by remember { mutableStateOf<TaskId?>(null) }
confirmDelete?.let { id ->
    AlertDialog(
        onDismissRequest = { confirmDelete = null },
        confirmButton = { TextButton(onClick = { onDeleteTask(id); confirmDelete = null }) { Text("Delete") } },
        text = { Text("Delete this task?") },
    )
}
```

## Summary

| Situation | Do |
|---|---|
| New screen | One VM (V1) |
| Screen VM getting big | Delegates inside it (V2) |
| Self-contained widget reused on many screens | Its own VM, obtained in each Route (V3) |
| Same data on unrelated screens | Repository + one VM per screen (V4) |
| Wizard sharing a draft | Flow entry owns shared VM (V5) |
| "Global app state" | Repository, never Activity VM (V6) |
