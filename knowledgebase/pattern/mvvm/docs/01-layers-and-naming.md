# 1. Layers and naming

```
 ┌──────────────┐  state (read-only)   ┌──────────────┐  state + lambdas  ┌──────────────┐
 │  Repository  │ ───────────────────▶ │  ViewModel   │ ────────────────▶ │ XRoute       │
 │  / UseCase   │ ◀─────────────────── │              │ ◀──────────────── │  └ XScreen   │
 └──────────────┘    suspend / Flow    └──────────────┘   intent calls    │     └ parts  │
                                                                          └──────────────┘
```

## MVVM-L1 — Unidirectional data flow
State flows **down** as read-only values. Events flow **up** as lambdas. A composable never
writes into state it received as a parameter.

## MVVM-L2 — Route / Screen split (MUST)
Every navigation destination has exactly two top-level composables:

- `XRoute` — stateful. Obtains the ViewModel, collects state, wires lambdas, performs navigation.
- `XScreen` — stateless. Takes `XUiState` + lambdas. Previewable and testable without a VM.

```kotlin
@Composable
fun TaskListRoute(
    onOpenTask: (TaskId) -> Unit,                       // navigation is passed IN to the Route
    viewModel: TaskListViewModel = koinViewModel(),      // Koin (koin-androidx-compose)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TaskListScreen(
        state = state,
        onTaskClick = onOpenTask,                       // UI-only event (E1) – no VM involved
        onToggleDone = viewModel::toggleDone,           // intent verb on VM (L8)
        onRetryClick = viewModel::retry,
    )
}

@Composable
fun TaskListScreen(
    state: TaskListUiState,
    onTaskClick: (TaskId) -> Unit,
    onToggleDone: (TaskId) -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) { /* no ViewModel, no Flow, no repository here */ }
```

## MVVM-L3 — The ViewModel stops at the Route (MUST NOT)
```kotlin
// ❌ Child reaches into the VM – untestable, unpreviewable, recomposes on everything
@Composable fun TaskRow(task: TaskUi, viewModel: TaskListViewModel) { ... }

// ✅ Child gets data + lambdas
@Composable fun TaskRow(task: TaskUi, onToggleDone: () -> Unit, modifier: Modifier = Modifier) { ... }
```

## MVVM-L4 — No framework objects in a ViewModel (MUST NOT)
Forbidden in a VM: `Context`, `Activity`, `Fragment`, `View`, `Resources`, the nav back stack,
`LazyListState`, `ScrollState`, `SnackbarHostState`, `FocusRequester`, `Painter`, `Color` derived
from theme, any `@Composable`.

**Only exception:** `TextFieldState` (see MVVM-I1) — it is a plain snapshot-state object with no
Android references, and keeping it in the VM is the officially recommended way to own text input.

If you need an Android service (clipboard, location, connectivity), wrap it in a data-layer class
injected with `@ApplicationContext` and depend on its interface.

## MVVM-L5 — Read-only exposure (MUST)
```kotlin
// StateFlow style
private val _uiState = MutableStateFlow(GameUiState())
val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

// Snapshot style – see 02 (MVVM-C4a)
private val _uiState = MutableGameUiState()
val uiState: GameUiState = _uiState
```

## MVVM-L6 — Lifecycle-aware collection (MUST)
Always `collectAsStateWithLifecycle()` (stops collecting below STARTED). Never `collectAsState()`
for ViewModel flows on Android.

## MVVM-L7 — No VM ↔ VM references (MUST NOT)
A ViewModel never receives another ViewModel (constructor, function arg, or lambda). Shared
data goes through the data layer — see MVVM-V4.

## MVVM-L8 — Naming (MUST)

| Thing | Name | Example |
|---|---|---|
| ViewModel | `XViewModel` | `TaskListViewModel` |
| UI state | `XUiState` | `TaskListUiState` |
| UI model for one item | `XUi` | `TaskUi` |
| Stateful entry | `XRoute` | `TaskListRoute` |
| Stateless screen | `XScreen` | `TaskListScreen` |
| Navigation key | `XKey` | `TaskDetailKey` (see 10 – MVVM-N2) |
| UI state holder (non-VM) | `XState` + `rememberXState()` | `FeedListState` |
| VM delegate | `XController` | `PromoCodeController` |
| VM public functions | intent verb, no `on` prefix | `retry()`, `toggleDone(id)`, `save()` |
| UI lambdas | `onNounVerb` | `onRetryClick`, `onToggleDone` |
| "Consumed" callbacks | `xHandled()` | `navigationHandled()`, `messageShown()` |

## MVVM-L9 — ViewModels are scoped to their Navigation 3 entry (MUST)
Navigation 3 does **not** scope ViewModels by default. Without the ViewModel decorator,
`koinViewModel()` inside an entry falls back to the Activity's store: the VM is never cleared when
the entry is popped, and two entries of the same type share one instance. Every `NavDisplay` that
hosts Routes with ViewModels MUST install both decorators, in this order:

```kotlin
NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),   // rememberSaveable per entry
        rememberViewModelStoreNavEntryDecorator(),        // ViewModelStore per entry  <- required
        rememberResultEventBusNavEntryDecorator(),        // only if entries exchange results (N4)
    ),
    entryProvider = entryProvider {
        entry<TaskListKey> {
            TaskListRoute(onOpenTask = { id -> backStack.add(TaskDetailKey(id.value)) })
        }
        entry<TaskDetailKey> { key ->
            TaskDetailRoute(
                viewModel = koinViewModel { parametersOf(key.taskId) },   // nav arg -> constructor (P5)
                onBack = { backStack.removeLastOrNull() },
            )
        }
    },
)
```

Libraries this module needs for the rules in this knowledgebase (add to `libs.versions.toml`):

| Artifact | For |
|---|---|
| `androidx.lifecycle:lifecycle-viewmodel-compose` | `ViewModel` in Compose, `SavedStateHandle.saveable` |
| `androidx.lifecycle:lifecycle-runtime-compose` | `collectAsStateWithLifecycle()` (L6) |
| `androidx.lifecycle:lifecycle-viewmodel-navigation3` | `rememberViewModelStoreNavEntryDecorator()` (L9) |
| `androidx.navigation3:navigation3-runtime` / `-ui` ≥ 1.2 | `NavDisplay`, `NavKey`, Result API `ResultEventBus` (N4) (already in catalog) |
| `io.insert-koin:koin-androidx-compose` | `koinViewModel()` (already in catalog) |
| `org.jetbrains.kotlinx:kotlinx-coroutines-test` | VM tests (already in catalog) |

## Folder layout (per feature)
```
feature/tasks/
├── list/
│   ├── TaskListViewModel.kt
│   ├── TaskListUiState.kt        ← UiState + UI models (TaskUi)
│   ├── TaskListRoute.kt          ← Route + Screen
│   └── components/               ← stateless pieces (TaskRow, EmptyState…)
└── detail/ ...
```
