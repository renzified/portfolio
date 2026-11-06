# 11. Review checklist & anti-patterns

Copy into the PR description for any screen/ViewModel change.

## PR checklist

**Structure**
- [ ] Destination has `XRoute` (stateful) + `XScreen` (stateless) — L2
- [ ] ViewModel is only referenced in the Route — L3
- [ ] `NavDisplay` has the ViewModel entry decorator — L9
- [ ] No `Context`/back stack/`LazyListState`/`SnackbarHostState`/UI types in the VM — L4, M8
- [ ] Mutable state is private; public is read-only — L5, C4a
- [ ] `collectAsStateWithLifecycle()` — L6
- [ ] No VM references another VM — L7

**State shape**
- [ ] One `XUiState` in one `StateFlow` (or a justified snapshot-state class) — C1, C4
- [ ] Not mixing StateFlow and `mutableStateOf` UI state in one VM (except `TextFieldState`) — C5
- [ ] Multi-field writes are atomic: `update {}` / `withMutableSnapshot {}` — C2, C3, C4b
- [ ] New values computed into locals before writing — C7
- [ ] Exclusive phases are sealed; no impossible flag combos — M1
- [ ] No stored derived values — M2
- [ ] Lazy lists use `key` — M4
- [ ] No resolved strings in the VM — M5
- [ ] Non-null initial state — M7

**Ownership**
- [ ] Pure UI state isn't in the VM; business state isn't in `remember` — S1, S2, S6
- [ ] State hoisted no higher than needed — S4
- [ ] Rotation/process death behaviour is intentional — S5, P5
- [ ] Shared data goes through a repository — V4

**Producing & events**
- [ ] Streams use `stateIn(viewModelScope, WhileSubscribed(5_000), …)` — P1
- [ ] No work in `init {}` — P3
- [ ] Errors caught and turned into state — P7
- [ ] VM → UI outcomes are state + `xHandled()`; no SharedFlow/Channel — E3, E4
- [ ] Navigation executed in the Route — E5
- [ ] Text input via `TextFieldState` — I1
- [ ] Per-frame values stay in the UI — I2

**Between screens**
- [ ] Nav keys hold IDs / small parameters only; VM gets them via constructor — N1, N2
- [ ] No static holders / shared VMs / CompositionLocals used to hand data forward — N3
- [ ] Results use `ResultEventBus` with explicit keys, only in entry blocks, forwarded to a VM intent, then `removeResult` — N4
- [ ] Other screens learn about data changes by observing the repository, not by refresh calls — N5
- [ ] Messages that outlive the screen go to the app message queue — N6
- [ ] Session/app-wide changes handled by the back-stack owner from state — N7
- [ ] No `SharedFlow`/`Channel`/event bus between screens or VMs — N8

**Tests**
- [ ] VM test with fakes covering each UiState phase + each outcome — T1–T3
- [ ] Screen previews/tests per phase — T4

## Anti-pattern gallery

| ❌ Smell | Why it's wrong | ✅ Instead | Rule |
|---|---|---|---|
| `fun Child(vm: XViewModel)` | Untestable, couples UI to VM | Pass state + lambdas | L3 |
| `_state.value = _state.value.copy(…)` | Lost updates under concurrency | `_state.update { it.copy(…) }` | C2 |
| `_items` + `_isLoading` as separate flows written together | Torn frames | One UiState | C3 |
| Public `var x by mutableStateOf()` in VM | Anyone can write | Read-only interface + private impl | C4a |
| Two writes to snapshot state from a background coroutine without `withMutableSnapshot` | Partial state visible | Wrap in `withMutableSnapshot` | C4b |
| `uiState.map { it.score }.stateIn()` per field | Fake granularity, breaks consistency | Style B | C6 |
| `val isLoading; val error; val data` all in one flat class | Impossible states | Sealed phases | M1 |
| `val total` stored next to `items` | Drifts | Computed property | M2 |
| `context.getString()` in VM | Leaks, ignores locale change | `UiText` | M5 |
| `StateFlow<XUiState?>` | Hides loading/empty/error | Real initial value | M7 |
| `init { viewModelScope.launch { load() } }` | Eager, untestable, no retry | Cold flow in `stateIn` + retry trigger | P3 |
| `SharingStarted.Eagerly` for UI state | Upstream runs in background | `WhileSubscribed(5_000)` | P1 |
| `suspend fun save()` called from UI | Work dies with composition | Non-suspend, launch in VM | P4 |
| `PagingData` inside UiState | Not a value | Separate flow + `cachedIn` | P6 |
| `MutableSharedFlow<Event>()` for navigation | Events lost | State + `navigationHandled()` | E3, E4 |
| Back stack in VM / `vm.load(id)` from `LaunchedEffect` | Navigation in VM, args after creation | Lambda in Route, args via constructor | E5, P5 |
| `NavDisplay` without `rememberViewModelStoreNavEntryDecorator()` | VMs leak to Activity scope | Add decorator | L9 |
| `TextField(value = vm.query.collectAsState().value, …)` | Async → cursor bugs | `TextFieldState` | I1 |
| `onValueChange = vm::setDragOffset` | VM round-trip per frame | Local state, commit at end | I2 |
| `Modifier.offset(y = state.value.dp)` for scroll-driven UI | Recompose per frame | `Modifier.offset { }` | I3 |
| `expanded` set in VM for an FAQ row | Plumbing for pure UI state | `rememberSaveable` | S2 |
| `var name by remember { mutableStateOf(state.name) }` | Stale duplicate | VM-owned draft | S7 |
| `activityViewModels()` as app store | Global coupling | Repository | V6 |
| `TaskDetailKey(task: TaskUi)` | Stale copy in saved state, size limits | `TaskDetailKey(taskId)` | N1 |
| `SelectedTask.current = task` before navigating | Lost on process death, hidden dependency | ID in the key | N3 |
| Picker result read in `XScreen` or kept as state on the opener | Re-applied, duplicated | Forward once to VM intent, then clear | N4 |
| `LaunchedEffect(Unit) { vm.refresh() }` after returning | Polling for changes | Observe the repository | N5 |
| `object AppEvents { val bus = MutableSharedFlow<Event>() }` | Dropped/duplicated events, hidden coupling | Repository state / message queue / result | N8 |
| Each screen collecting a "logged out" event | Duplicated, racy navigation | Session state; root rewrites back stack | N7 |
