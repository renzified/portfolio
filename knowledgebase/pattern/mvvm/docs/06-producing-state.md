# 6. Producing state

## MVVM-P1 — Streams become state with `stateIn` (MUST)
```kotlin
val uiState: StateFlow<XUiState> = source
    .map { … }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),   // survives rotation, stops 5 s after UI leaves
        initialValue = XUiState.Loading,
    )
```
- `WhileSubscribed(5_000)` is the only allowed `started` for UI state. `Eagerly`/`Lazily` keep
  upstream (DB, sockets, location) alive while the app is in the background.
- Define a constant once: `private val WhileUiSubscribed = SharingStarted.WhileSubscribed(5_000)`.

## MVVM-P2 — User inputs are private flows, combined with data (MUST)
The canonical screen: a data stream from the repository + user-controlled inputs + transient UI
outcomes.

```kotlin
class TaskListViewModel(
    private val repository: TaskRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val filter = savedStateHandle.getStateFlow(KEY_FILTER, TaskFilter.All)  // P5: user input
    private val isRefreshing = MutableStateFlow(false)                               // transient
    private val userMessage = MutableStateFlow<UiText?>(null)                       // transient (07)

    val uiState: StateFlow<TaskListUiState> =
        combine(
            filter.flatMapLatest { repository.observeTasks(it) },
            filter, isRefreshing, userMessage,
        ) { tasks, filter, refreshing, message ->
            TaskListUiState.Content(
                tasks = tasks.map { it.toUi() },
                filter = filter,
                isRefreshing = refreshing,
                userMessage = message,
            ) as TaskListUiState
        }
            .catch { emit(TaskListUiState.Error(UiText.Res(R.string.error_generic))) }   // P7
            .stateIn(viewModelScope, WhileUiSubscribed, TaskListUiState.Loading)

    fun selectFilter(f: TaskFilter) { savedStateHandle[KEY_FILTER] = f }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            repository.refresh()                                       // writes DB → stream re-emits
                .onFailure { userMessage.value = UiText.Res(R.string.refresh_failed) }
            isRefreshing.value = false
        }
    }

    fun toggleDone(id: TaskId) { viewModelScope.launch { repository.toggleDone(id) } }
    fun messageShown() { userMessage.value = null }

    private companion object { const val KEY_FILTER = "filter" }
}
```

`flatMapLatest` needs `@OptIn(ExperimentalCoroutinesApi::class)`.

### When to use `MutableStateFlow<XUiState>` + `update {}` instead
When the screen is driven mostly by user actions rather than data streams (forms, editors, games),
the "private `_uiState` + `update {}`" form from chapter 2 is fine. Pick per VM:

| VM mostly… | Shape |
|---|---|
| displays repository streams | `combine(...).stateIn(...)` (P1/P2) |
| reacts to user actions / local logic | `private val _uiState = MutableStateFlow(…)` + `update {}` (C2) |

Don't do both for the same fields.

## MVVM-P3 — No work in `init {}` (MUST NOT)
```kotlin
// ❌ Runs on construction even if the UI never subscribes; hard to test; loads again
//    on nothing; can't be retried cleanly.
init { viewModelScope.launch { _uiState.value = Content(repository.load()) } }
```

One-shot loads become a cold flow inside the `stateIn` chain, with a retry trigger:

```kotlin
private val retries = MutableStateFlow(0)

@OptIn(ExperimentalCoroutinesApi::class)
val uiState: StateFlow<ProfileUiState> = retries
    .flatMapLatest {
        flow {
            emit(ProfileUiState.Loading)
            emit(
                repository.loadProfile().fold(
                    onSuccess = { ProfileUiState.Content(it.toUi()) },
                    onFailure = { ProfileUiState.Error(UiText.Res(R.string.error_generic)) },
                )
            )
        }
    }
    .stateIn(viewModelScope, WhileUiSubscribed, ProfileUiState.Loading)

fun retry() = retries.update { it + 1 }
```

Note: with `WhileSubscribed(5_000)`, if the user is away > 5 s the upstream restarts and the load
runs again on return. That's usually desired (fresh data). If not, cache in the repository.

## MVVM-P4 — Public VM functions are non-suspend and main-safe (MUST)
```kotlin
fun save() { viewModelScope.launch { … } }          // ✅ UI calls it from a click
suspend fun save() { … }                             // ❌ UI would need a scope; work dies with composition
```
Repositories/use cases are main-safe (they switch dispatchers themselves), so the VM never wraps
calls in `withContext(Dispatchers.IO)` for repository calls. Heavy CPU work in the VM uses an injected
dispatcher (P8).

## MVVM-P5 — Nav args via constructor; SavedStateHandle for user input only (MUST)
With Navigation 3 the nav key **is** the argument. Pass what the VM needs from the key into its
constructor via Koin `parametersOf`. The key is already saved by the back stack, so it survives
process death without going through `SavedStateHandle`.

```kotlin
@Serializable data class TaskDetailKey(val taskId: Long) : NavKey

// Koin module
viewModel { (taskId: Long) -> TaskDetailViewModel(taskId, get(), get()) }

// Entry provider
entry<TaskDetailKey> { key ->
    TaskDetailRoute(viewModel = koinViewModel { parametersOf(key.taskId) }, onBack = onBack)
}

class TaskDetailViewModel(
    private val taskId: Long,                           // nav arg – immutable for the VM's lifetime
    private val repository: TaskRepository,
    private val savedStateHandle: SavedStateHandle,     // Koin injects it for koinViewModel()
) : ViewModel()
```

| Put in SavedStateHandle | Don't |
|---|---|
| Selected filter / tab / sort | Nav arguments (constructor, above) |
| Draft text the user typed | Lists/data you can reload |
| | Loading flags, errors |
| | Large objects (Bundle limit ~1 MB for the whole app) |

```kotlin
private val sort = savedStateHandle.getStateFlow("sort", SortOrder.Newest)        // StateFlow style

@OptIn(SavedStateHandleSaveableApi::class)
val title: TextFieldState = savedStateHandle.saveable(
    key = "title", saver = TextFieldState.Saver,
) { TextFieldState() }                                                            // draft text (I1)
```

## MVVM-P6 — Paging is a separate flow (MUST — explicit exception to C1)
`PagingData` is a stream, not a value; it can't sit inside a `data class` UiState.

```kotlin
class FeedViewModel(repository: FeedRepository) : ViewModel() {
    val posts: Flow<PagingData<PostUi>> = repository.pagedPosts()
        .map { page -> page.map { it.toUi() } }
        .cachedIn(viewModelScope)                       // MUST: survives rotation, single upstream

    val uiState: StateFlow<FeedUiState> = …            // everything else (header, filters)
}

@Composable
fun FeedRoute(viewModel: FeedViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val posts = viewModel.posts.collectAsLazyPagingItems()
    FeedScreen(state = state, posts = posts)
}
```
Loading/error of the list itself comes from `posts.loadState`, not from UiState.

## MVVM-P7 — Errors are state (MUST)
- Every flow that reaches `stateIn` has a `.catch { emit(Error…) }` (or the repository returns
  `Result`).
- Every `viewModelScope.launch` that can fail handles the failure and writes it into state
  (`userMessage`, `Error` phase). Never let an exception escape `viewModelScope`.
- `CancellationException` is never swallowed — `runCatching` around a suspend call must rethrow it
  (or use a `suspendRunCatching` helper).

## MVVM-P8 — Injected dispatchers (MUST)
```kotlin
class ReportViewModel(
    private val repository: ReportRepository,
    @DefaultDispatcher private val default: CoroutineDispatcher,
) : ViewModel() {
    fun export() {
        viewModelScope.launch {
            val csv = withContext(default) { buildCsv(repository.rows()) }   // CPU work
            …
        }
    }
}
```
Tests pass a `TestDispatcher`.
