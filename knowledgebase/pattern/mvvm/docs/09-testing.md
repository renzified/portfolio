# 9. Testing

The rules in chapters 1–8 exist partly so that each layer can be tested alone:
the VM without UI, the Screen without a VM.

## MVVM-T1 — Fakes + MainDispatcherRule (MUST)
- Use hand-written **fakes** of repository interfaces (not mocks) so tests drive real flows.
- `viewModelScope` uses `Dispatchers.Main`; replace it in every VM test.

```kotlin
class MainDispatcherRule(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

class FakeTaskRepository : TaskRepository {
    private val tasks = MutableStateFlow<List<Task>>(emptyList())
    var refreshResult: Result<Unit> = Result.success(Unit)
    fun emit(list: List<Task>) { tasks.value = list }
    override fun observeTasks(filter: TaskFilter) = tasks.map { it.filter(filter::matches) }
    override suspend fun refresh() = refreshResult
    override suspend fun toggleDone(id: TaskId) =
        tasks.update { l -> l.map { if (it.id == id) it.copy(done = !it.done) else it } }
}
```

## MVVM-T2 — `WhileSubscribed` flows need a collector (MUST)
`stateIn(WhileSubscribed)` does nothing until someone collects. Start a collector in
`backgroundScope` (auto-cancelled at the end of the test).

```kotlin
class TaskListViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()
    private val repo = FakeTaskRepository()
    private val vm = TaskListViewModel(repo, SavedStateHandle())

    @Test
    fun `shows tasks from repository`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        repo.emit(listOf(task(id = 1, title = "Buy milk")))

        val state = vm.uiState.value as TaskListUiState.Content
        assertEquals(listOf("Buy milk"), state.tasks.map { it.title })
    }

    @Test
    fun `refresh failure shows message and clears after shown`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }
        repo.refreshResult = Result.failure(IOException())

        vm.refresh()
        assertEquals(UiText.Res(R.string.refresh_failed),
            (vm.uiState.value as TaskListUiState.Content).userMessage)   // E3 – outcome is plain data

        vm.messageShown()
        assertNull((vm.uiState.value as TaskListUiState.Content).userMessage)
    }
}
```
(Turbine's `vm.uiState.test { awaitItem() … }` is an equally valid way to collect; pick one per
project and stick to it.)

## MVVM-T3 — Snapshot-style VMs: read properties directly (MUST)
No collector needed; `withMutableSnapshot` applies to the global snapshot when it returns.

```kotlin
class GameViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun `tick increments time and score atomically`() {
        val vm = GameViewModel()

        vm.tick()

        assertEquals(1, vm.uiState.timeSurvived)
        assertEquals(10.01, vm.uiState.score, 1e-9)       // C7: score uses the NEW time
    }
}
```
To test reactions to snapshot state (e.g. `snapshotFlow { field.text }` in I1/I4), change the state
then advance: `vm.query.setTextAndPlaceCursorAtEnd("kot"); advanceTimeBy(301)`. If changes made
outside composition don't propagate, call `Snapshot.sendApplyNotifications()` after the write.

## MVVM-T4 — UI tests hit the stateless Screen (MUST)
Because of L2, screens take plain state — no VM, no Hilt, no fake repository in UI tests.

```kotlin
class TaskListScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun errorState_retryCallsLambda() {
        var retried = false
        compose.setContent {
            TaskListScreen(
                state = TaskListUiState.Error(UiText.Raw("Boom")),
                onTaskClick = {}, onToggleDone = {}, onRetryClick = { retried = true },
            )
        }
        compose.onNodeWithText("Retry").performClick()
        assertTrue(retried)
    }
}
```

Previews follow the same pattern: one `@Preview` per sealed phase (Loading / Error / Content /
Empty).
