# 7. Events

Two directions, different rules.

```
UI ──(user action)──▶ VM          E1, E2
VM ──(outcome)──────▶ UI          E3, E4, E5
```

## MVVM-E1 — UI-only events stay in the UI (MUST)
If handling the event needs no business logic and no data, don't send it to the VM.

| Event | Handled in |
|---|---|
| Click a row → open detail | Route calls the `onOpenTask` navigation lambda |
| Expand / collapse, show tooltip, open a menu | Composable state (S2) |
| Scroll to top | Plain state holder (S3) |
| Back button | Navigation |

## MVVM-E2 — Actions needing logic → one VM function per intent (MUST)
- Function names are intents (`save()`, `toggleDone(id)`, `retry()`), not UI gestures
  (`onSaveButtonClicked()`) — the VM doesn't know there's a button. (L8)
- The Route maps UI lambdas to VM intents with method references:
  `onSaveClick = viewModel::save`.
- Many actions on one screen (> 6)? Group them in a stable holder rather than inventing an
  MVI-style `Action` sealed class:

```kotlin
@Immutable
data class EditorActions(
    val onTitleChange: (String) -> Unit,
    val onBold: () -> Unit,
    val onItalic: () -> Unit,
    val onUndo: () -> Unit,
    val onRedo: () -> Unit,
    val onSave: () -> Unit,
    val onDiscard: () -> Unit,
)

@Composable
fun EditorRoute(viewModel: EditorViewModel = koinViewModel()) {
    val actions = remember(viewModel) {
        EditorActions(
            onTitleChange = viewModel::setTitle,
            onBold = viewModel::bold, /* … */
        )
    }
    …
}
```

## MVVM-E3 — VM → UI one-off outcomes are state (MUST)
"Navigate after save", "show snackbar", "open the camera" are outcomes of business logic. Model them
as **state**, let the UI act on it, then tell the VM it was handled.

```kotlin
data class EditTaskUiState(
    val isSaving: Boolean = false,
    val navigateBack: Boolean = false,       // outcome
    val userMessage: UiText? = null,         // outcome
)

class EditTaskViewModel(private val repository: TaskRepository, …) : ViewModel() {
    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState = _uiState.asStateFlow()

    fun save() {
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            repository.save(…)
                .onSuccess { _uiState.update { it.copy(isSaving = false, navigateBack = true) } }
                .onFailure { _uiState.update { it.copy(isSaving = false, userMessage = UiText.Res(R.string.save_failed)) } }
        }
    }
    fun navigationHandled() = _uiState.update { it.copy(navigateBack = false) }
    fun messageShown() = _uiState.update { it.copy(userMessage = null) }
}

@Composable
fun EditTaskRoute(
    onBack: () -> Unit,
    viewModel: EditTaskViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentOnBack by rememberUpdatedState(onBack)

    // Navigation outcome
    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            viewModel.navigationHandled()
            currentOnBack()                                     // E5: navigation happens here
        }
    }

    // Message outcome
    state.userMessage?.let { message ->
        val text = message.asString()
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(text)
            viewModel.messageShown()
        }
    }

    EditTaskScreen(state = state, snackbarHostState = snackbarHostState, onSaveClick = viewModel::save)
}
```

Why state and not a stream:
- It **can't be lost** — if the UI is in the background when save finishes, the flag waits.
- It survives rotation and is visible in tests as plain data.
- "Handled" is explicit; the VM decides what happens next.

Messages that may arrive faster than they're shown → keep a `List<UserMessage>` (with ids) and
`messageShown(id)` removes the head.

Outcomes that must reach **another** screen (a result for the opener, a message that outlives this
screen, a session change) are covered in chapter 10 (MVVM-N4 – N7).

## MVVM-E4 — No SharedFlow / Channel for VM → UI events (MUST NOT)
```kotlin
// ❌ Emissions with no active collector are dropped (rotation, background, before first collect)
private val _events = MutableSharedFlow<Event>()
// ❌ Channel can deliver while the UI is between STOPPED and collection cancel → lost or doubled
private val _events = Channel<Event>()
```
One mechanism for the whole codebase = E3. Consistency beats the small convenience.
The same applies between screens: no app-wide event bus (MVVM-N8).

## MVVM-E5 — Navigation executes in the Route (MUST)
- The VM never holds the back stack (`NavBackStack`, `SnapshotStateList<NavKey>`) or any navigator
  (L4), and never decides *where* to go — only *that* the outcome happened
  (`navigateBack = true`, `openedTaskId = id`).
- The Route receives navigation lambdas (`onBack`, `onOpenTask`) from the `entryProvider` and calls
  them. Only the code that owns the back stack calls `backStack.add(…)` / `removeLastOrNull()`.
- Nav arguments flow into the VM through its constructor (P5), never as a function call after
  creation (`vm.load(id)` from a `LaunchedEffect` is forbidden).

## MVVM-E6 — Effects triggered by state go in the Route, keyed correctly (MUST)
- `LaunchedEffect(key)` keyed on the **outcome value**, so a new outcome restarts it.
- Lambdas captured by long-lived effects go through `rememberUpdatedState`.
- Permissions, activity results, haptics, opening external apps are UI-layer effects: the VM sets
  the outcome (`requestCameraPermission = true`), the Route launches the launcher and reports the
  result back through a VM function (`cameraPermissionResult(granted)`).
