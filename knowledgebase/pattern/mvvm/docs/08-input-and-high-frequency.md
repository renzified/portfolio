# 8. Text input and high-frequency state

## MVVM-I1 — Text owned by the VM uses `TextFieldState` (MUST)
Text input must update **synchronously**. Routing every keystroke through
`StateFlow → collectAsState → TextField(value=…)` is asynchronous, so the IME and the field can
disagree → cursor jumps, lost characters, broken composition (e.g. Japanese/Korean input).

| Need | Do |
|---|---|
| Text only the UI cares about | `rememberTextFieldState()` in the composable (S2) |
| Text the VM validates / submits / searches | `val field = TextFieldState()` in the VM |
| Must survive process death | `savedStateHandle.saveable(key, TextFieldState.Saver) { TextFieldState() }` (P5) |

```kotlin
class SignUpViewModel(private val repository: AuthRepository) : ViewModel() {
    val email = TextFieldState()                         // allowed exception to L4 / C5
    val password = TextFieldState()

    private val isSubmitting = MutableStateFlow(false)

    // Derived from text: bridge snapshot state → Flow with snapshotFlow
    val uiState: StateFlow<SignUpUiState> = combine(
        snapshotFlow { email.text.toString() to password.text.toString() },
        isSubmitting,
    ) { (e, p), submitting ->
        SignUpUiState(
            emailError = if (e.isNotEmpty() && !e.isValidEmail()) UiText.Res(R.string.invalid_email) else null,
            canSubmit = e.isValidEmail() && p.length >= 8 && !submitting,
            isSubmitting = submitting,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SignUpUiState())

    fun submit() {
        viewModelScope.launch {
            isSubmitting.value = true
            repository.signUp(email.text.toString(), password.text.toString())
            isSubmitting.value = false
        }
    }
}

@Composable
fun SignUpRoute(viewModel: SignUpViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SignUpScreen(
        state = state,
        email = viewModel.email,            // TextFieldState passed down as-is
        password = viewModel.password,
        onSubmitClick = viewModel::submit,
    )
}

@Composable
fun SignUpScreen(state: SignUpUiState, email: TextFieldState, password: TextFieldState, onSubmitClick: () -> Unit) {
    Column {
        OutlinedTextField(state = email, isError = state.emailError != null,
            supportingText = state.emailError?.let { { Text(it.asString()) } })
        OutlinedSecureTextField(state = password)
        Button(onClick = onSubmitClick, enabled = state.canSubmit) { Text("Sign up") }
    }
}
```

- Transform input (uppercase, max length, digits-only) with `InputTransformation`, not by rewriting
  the text from the VM.
- Programmatic changes (clear, prefill) use `field.setTextAndPlaceCursorAtEnd(…)` / `field.clearText()`
  from the VM.

❌ Never:
```kotlin
private val _query = MutableStateFlow("")
TextField(value = query, onValueChange = viewModel::setQuery)   // async round-trip
```

## MVVM-I4 — Debounce in the VM (MUST)
```kotlin
class SearchViewModel(private val repository: SearchRepository) : ViewModel() {
    val query = TextFieldState()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SearchUiState> = snapshotFlow { query.text.toString().trim() }
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { q ->
            if (q.isEmpty()) flowOf(SearchUiState.Idle)
            else flow {
                emit(SearchUiState.Searching)
                emit(repository.search(q).fold(
                    onSuccess = { SearchUiState.Results(it.map { r -> r.toUi() }) },
                    onFailure = { SearchUiState.Error(UiText.Res(R.string.search_failed)) },
                ))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchUiState.Idle)
}
```
`flatMapLatest` cancels the previous search when the query changes.

## MVVM-I2 — Per-frame values never go to the VM (MUST)
Drag offsets, scroll offsets, animation progress, pinch scale: these change ~60–120×/s. A VM
round-trip per frame is wasted work and causes jank. Keep them in the UI; commit only the **result**.

```kotlin
@Composable
fun VolumeSlider(volume: Float, onVolumeCommit: (Float) -> Unit) {
    var dragging by remember { mutableStateOf<Float?>(null) }         // local while dragging
    Slider(
        value = dragging ?: volume,
        onValueChange = { dragging = it },                           // per frame → local only
        onValueChangeFinished = {
            dragging?.let(onVolumeCommit)                             // once → VM
            dragging = null
        },
    )
}
```

Swipe-to-dismiss: offset is local (`Animatable`/`AnchoredDraggableState`), the VM only gets
`delete(id)` when the swipe settles.

## MVVM-I3 — Defer fast reads to layout/draw phase (SHOULD)
When a value that changes often must reach the UI (Style B ticker, scroll-driven header), read it
in a **lambda** modifier so only layout/draw reruns, not composition.

```kotlin
// ❌ Reads in composition → recomposes every frame
Box(Modifier.offset(y = -scrollState.value.toDp()))

// ✅ Reads in layout phase
Box(Modifier.offset { IntOffset(0, -scrollState.value) })

// ✅ Progress from a Style-B VM: pass a provider, read in draw
@Composable
fun UploadBar(progress: () -> Float, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(4.dp).drawBehind {
        drawRect(Color.Blue, size = size.copy(width = size.width * progress()))
    })
}
UploadBar(progress = { viewModel.uiState.progress })
```

Lambda-modifier equivalents: `offset { }`, `graphicsLayer { }`, `drawBehind { }`,
`layout { }`, `Modifier.alpha` → `graphicsLayer { alpha = … }`.

## Where high-frequency state goes — summary

| Source | Rate | Owner | Style |
|---|---|---|---|
| Gesture / animation | per frame | UI (`Animatable`, local state) | — (I2) |
| Scroll position | per frame | UI (`LazyListState`) | `derivedStateOf` for thresholds |
| Business ticker (game loop, countdown, upload %) | ~1–60/s | VM | Style B if measured cost (C4), read via provider (I3) |
| Sensor / location stream | ~1–50/s | Repository → VM | sample/throttle in VM (`sample(100)`) then Style A |
| Text input | per keystroke | `TextFieldState` | I1 |
