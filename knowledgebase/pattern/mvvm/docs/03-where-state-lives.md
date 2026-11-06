# 3. Where state lives

Not all state belongs in a ViewModel. Putting pure UI state there adds plumbing, a round-trip per
interaction, and (for scroll/animation) jank.

| Kind of state | Example | Owner |
|---|---|---|
| **Business / screen state** | tasks list, cart total, "is saving", error from repository, selected filter that changes the query | ViewModel (S1) |
| **UI element state, trivial** | row expanded, dropdown open, password visible, tab index that only switches visuals | `remember` / `rememberSaveable` (S2) |
| **UI element state, with UI logic** | scroll + "show scroll-to-top FAB", snackbar queue, multi-select mode with gestures | Plain state holder class (S3) |

## MVVM-S1 — Business-relevant state → ViewModel (MUST)
If a repository/use case produces it, consumes it, or business rules depend on it, the ViewModel owns
it.

## MVVM-S2 — Trivial UI element state → in the composable (MUST)
No business logic, no data layer → don't route through the VM.

```kotlin
@Composable
fun FaqItem(question: String, answer: String, modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }     // S2 + S5
    Column(modifier.clickable { expanded = !expanded }) {
        Text(question)
        AnimatedVisibility(expanded) { Text(answer) }
    }
}

@Composable
fun PasswordField(state: TextFieldState, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }              // nobody else cares
    OutlinedSecureTextField(
        state = state,
        textObfuscationMode = if (visible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped,
        trailingIcon = { IconButton(onClick = { visible = !visible }) { /* eye icon */ } },
        modifier = modifier,
    )
}
```

❌ The anti-pattern:
```kotlin
class FaqViewModel : ViewModel() {
    private val _expandedIds = MutableStateFlow(setOf<Int>())     // pure UI concern in the VM
    fun toggle(id: Int) = _expandedIds.update { if (id in it) it - id else it + id }
}
```

## MVVM-S3 — Non-trivial UI logic → plain state holder class (SHOULD)
When UI logic grows past a couple of lines, or several composables share it, extract a class.
It **may** hold UI objects (`LazyListState`, `SnackbarHostState`, a composition `CoroutineScope`) —
that's exactly what a ViewModel can't.

```kotlin
@Stable
class FeedListState(
    val listState: LazyListState,
    private val scope: CoroutineScope,
) {
    val showScrollToTop: Boolean by derivedStateOf { listState.firstVisibleItemIndex > 2 }

    fun scrollToTop() {
        scope.launch { listState.animateScrollToItem(0) }
    }
}

@Composable
fun rememberFeedListState(
    listState: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope(),
): FeedListState = remember(listState, scope) { FeedListState(listState, scope) }

@Composable
fun FeedScreen(state: FeedUiState, modifier: Modifier = Modifier) {
    val listState = rememberFeedListState()
    Box(modifier) {
        LazyColumn(state = listState.listState) { /* items(key = …) */ }
        if (listState.showScrollToTop) {
            FloatingActionButton(onClick = listState::scrollToTop) { /* ↑ */ }
        }
    }
}
```

A screen may use **both** a ViewModel (business) and a plain state holder (UI). The plain holder may
call VM functions through lambdas it receives, never by holding the VM.

## MVVM-S4 — Hoist to the lowest common ancestor (MUST)
Hoist state only as high as the highest composable that reads or writes it. If only `FaqItem` uses
`expanded`, it stays in `FaqItem`. If "expand all" exists in the toolbar, hoist to the screen.

```kotlin
@Composable
fun FaqScreen(items: List<FaqUi>) {
    var expandedIds by rememberSaveable { mutableStateOf(setOf<Int>()) }   // hoisted: toolbar needs it
    Column {
        TextButton(onClick = { expandedIds = items.map { it.id }.toSet() }) { Text("Expand all") }
        items.forEach { item ->
            FaqItem(                                  // stateless variant: expanded is now a parameter
                item = item,
                expanded = item.id in expandedIds,
                onToggle = { expandedIds = if (item.id in expandedIds) expandedIds - item.id else expandedIds + item.id },
            )
        }
    }
}
```

## MVVM-S5 — Survive what the user would miss (MUST)

| Survives | `remember` | `rememberSaveable` | ViewModel field | `SavedStateHandle` |
|---|---|---|---|---|
| Recomposition | ✅ | ✅ | ✅ | ✅ |
| Rotation / config change | ❌ | ✅ | ✅ | ✅ |
| Leaving & returning via back stack | ❌ | ✅ | ✅ | ✅ |
| Process death | ❌ | ✅ | ❌ | ✅ |

Use `rememberSaveable` for user choices in the UI (selected tab, expanded section, entered draft).
Use `SavedStateHandle` for user input owned by the VM (MVVM-P5). Don't save things you can reload.

## MVVM-S6 — Business logic needs it ⇒ move it to the VM (MUST)
The tab index starts as UI state. The day the selected tab changes what's loaded, it moves to the VM.

```kotlin
// Before – pure visual
var tab by rememberSaveable { mutableIntStateOf(0) }

// After – tab drives the query, so the VM owns it
class OrdersViewModel(private val repo: OrdersRepository, private val handle: SavedStateHandle) : ViewModel() {
    private val tab = handle.getStateFlow(KEY_TAB, OrderTab.Active)
    val uiState = tab.flatMapLatest { repo.observeOrders(it) }.map { … }.stateIn(…)
    fun selectTab(t: OrderTab) { handle[KEY_TAB] = t }
}
```

## MVVM-S7 — Single source of truth (MUST NOT duplicate)
Never keep a copy of VM state in `remember` "for convenience", and never seed `remember` from VM
state without a key. If you need a locally-editable copy (e.g. an edit form), that's a draft — it
lives in the VM (`TextFieldState`) or is explicitly keyed:

```kotlin
// ❌ Goes stale when state.name changes
var name by remember { mutableStateOf(state.name) }

// ✅ Draft owned by the VM (see 08 – MVVM-I1)
TextField(state = viewModel.nameField)
```
