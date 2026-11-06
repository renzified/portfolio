# 5. Modeling UI state

## MVVM-M1 — Sealed for exclusive phases, flags for overlays (MUST)
If two states can't exist at the same time, make that impossible to express.

```kotlin
// ❌ 8 combinations, most of them nonsense (isLoading && error != null && tasks.isNotEmpty()?)
data class TaskListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val tasks: List<TaskUi> = emptyList(),
)

// ✅ Exclusive phases are sealed; things that overlay content are flags inside Content
sealed interface TaskListUiState {
    data object Loading : TaskListUiState
    data class Error(val message: UiText) : TaskListUiState
    data class Content(
        val tasks: List<TaskUi>,
        val filter: TaskFilter,
        val isRefreshing: Boolean = false,      // overlay: shown together with tasks
        val userMessage: UiText? = null,        // overlay: snackbar (see 07)
    ) : TaskListUiState {
        val isEmpty: Boolean get() = tasks.isEmpty()     // M2: derived, not stored
    }
}
```

```kotlin
@Composable
fun TaskListScreen(state: TaskListUiState, /* lambdas */) {
    when (state) {
        TaskListUiState.Loading -> FullScreenLoading()
        is TaskListUiState.Error -> ErrorState(state.message, onRetryClick)
        is TaskListUiState.Content ->
            if (state.isEmpty) EmptyState() else TaskList(state.tasks, state.isRefreshing, /*…*/)
    }
}
```

**Forms / editors** usually have no exclusive phases after load → a flat data class is correct:
```kotlin
data class EditProfileUiState(
    val isSaving: Boolean = false,
    val canSave: Boolean = false,
    val saved: Boolean = false,           // one-off outcome, see 07
    val userMessage: UiText? = null,
)
```

## MVVM-M2 — Don't store what you can derive (MUST)
Stored derived values go out of sync. Derive them.

| Where to derive | How |
|---|---|
| Cheap, from fields of the same UiState | computed property on the data class (`val isEmpty get() = …`) |
| From several sources | `combine(...)` in the VM |
| Snapshot style (Style B) | `val canSubmit by derivedStateOf { … }` in the mutable impl |
| From UI-only state, where output changes less than input | `derivedStateOf` in the composable (`showFab` from scroll index) |

```kotlin
// ❌
data class CartUiState(val items: List<CartItemUi>, val total: Money)   // total can drift from items
// ✅
data class CartUiState(val items: List<CartItemUi>) {
    val total: Money get() = items.sumOf { it.price * it.quantity }
}
```

## MVVM-M3 — Read-only, never-mutated collections (MUST)
- UiState uses `List`, `Set`, `Map` — never `MutableList`, `ArrayList`, `HashMap` as the declared type.
- A list placed in UiState is never mutated afterwards; always build a new one (`it.tasks + task`,
  `it.tasks.map { … }`).
- With strong skipping (Kotlin 2.0.20+, default) unstable params are compared by instance, so
  re-using the same list instance when nothing changed keeps children skipping. `kotlinx.collections.immutable`
  is optional, not required.

```kotlin
_uiState.update { s -> s.copy(tasks = s.tasks.map { if (it.id == id) it.copy(done = !it.done) else it }) }
```

## MVVM-M4 — Stable keys and item models in lists (MUST)
```kotlin
LazyColumn {
    items(items = state.tasks, key = { it.id.value }, contentType = { "task" }) { task ->
        TaskRow(task = task, onToggleDone = { onToggleDone(task.id) })
    }
}
```
- Every `items(…)` call has `key`. Without it, inserts/removes recompose and lose per-item state.
- Item UI models (`TaskUi`) are `data class`es with only what the row renders.
- Per-item UI-only state (row swipe offset, expanded) lives in the row via `rememberSaveable` — keyed
  items keep it attached to the right row.

## MVVM-M5 — ViewModels don't resolve strings (MUST)
ViewModels have no `Context` (L4) and must survive locale changes. Expose resource references or
raw data; resolve in the UI.

```kotlin
sealed interface UiText {
    data class Raw(val value: String) : UiText
    data class Res(@StringRes val id: Int, val args: List<Any> = emptyList()) : UiText  // data class → testable equality
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Raw -> value
    is UiText.Res -> stringResource(id, *args.toTypedArray())
}
```

- Plural / formatted numbers / dates: expose the raw value (`Int`, `Instant`, `Money`) and format in
  the UI or in a formatter passed from the UI layer.
- Server-provided messages: `UiText.Raw`.

## MVVM-M6 — UI models vs domain models (SHOULD)
- If the screen needs a different shape (formatted parts, flattened relations, selection flag), map
  domain → `XUi` in the VM (`fun Task.toUi(selected: Boolean) = TaskUi(…)`).
- If the domain model is immutable and already the right shape, using it directly is fine.
- Never put DTOs / entities (Retrofit, Room) into UiState.

## MVVM-M7 — Always a real initial value (MUST)
`StateFlow<XUiState>` with a meaningful default (`Loading`, empty form). Never `StateFlow<XUiState?>`
and never `lateinit` UI state — `null` hides whether you're loading, empty or broken.

## MVVM-M8 — No UI types in UiState (MUST NOT)
No `Color`, `Dp`, `ImageVector`, `Painter`, `Modifier`, `@Composable` lambdas in UiState. Express
the *meaning* (`priority = High`, `status = Overdue`) and let the UI pick the color/icon.
