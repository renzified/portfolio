# 10. Between screens — passing data and notifying events

Chapters 1–9 are about one destination. This chapter is about what crosses the boundary between
destinations: the input a screen opens with, the answer it gives back, and things that happen on
one screen that other screens need to know about.

## Tree 4 — How does this reach the other screen?

Run this whenever a value or an "it happened" has to leave the screen that produced it.

```
Is it data the app keeps (persisted, cached, or shown on more than one screen)?
├── YES → Write it to the repository; other screens observe it. Send nothing.   (MVVM-N5, V4)
└── NO  → Is it what the next screen needs in order to open?
          ├── YES → Put the ID / small value in the NavKey                       (MVVM-N1, N2)
          └── NO  → Is it an answer for the screen that opened this one
                    (picker choice, confirm result, "deleted, offer undo")?
                    ├── YES → Navigation result, consumed once by the opener     (MVVM-N4)
                    └── NO  → Must it outlive the current screen, or does it come from
                              outside any screen (sync, worker, session)?
                              ├── Message for the user → App-level message queue (MVVM-N6)
                              └── App-wide condition   → Session/app state in a repository (MVVM-N7)

Shared, not-yet-saved draft across the steps of one flow? → Flow ViewModel (MVVM-V5), not this chapter.
```

There is **no branch that ends in an event bus** (MVVM-N8).

---

## Passing data forward

### MVVM-N1 — Keys carry IDs and small parameters, never data (MUST)
A `NavKey` is serialized into the saved back stack. Whatever you put in it is stored in the saved
state, frozen at the moment you navigated, and becomes a second copy of the data (S7).

| Allowed in a key | Not allowed in a key |
|---|---|
| IDs (`taskId: Long`, `@JvmInline value class TaskId`) | Domain objects (`Task`), UI models (`TaskUi`), entities/DTOs |
| Small parameters that configure the screen: mode enum, pre-selected filter, initial query, `selectedLabelId` for a picker | Lists, bitmaps, byte arrays, anything unbounded |
| Not-yet-saved values that are **small** and **are the input** (a search query typed on the previous screen) | Data the destination can load by ID |

```kotlin
// ❌ Stale snapshot of the task; bloats saved state; a second source of truth
@Serializable data class TaskDetailKey(val task: TaskUi) : NavKey

// ✅ The destination loads the current task by ID and observes it
@Serializable data class TaskDetailKey(val taskId: Long) : NavKey
```

Creating something and then opening it: the repository saves and returns the new ID, the VM
exposes it as an outcome (`createdTaskId = id`, E3), the Route navigates with that ID.

> Why not "just pass the object to skip a DB read": the detail screen must observe the repository
> anyway (the task can change while it's open, V4). The read is not saved, only duplicated.

### MVVM-N2 — Key → entry → VM constructor; nothing below the Route sees the key (MUST)
- Keys are `@Serializable` `data class`/`data object` implementing `NavKey`, named `XKey`, with only
  `val`s. One key type per destination.
- The `entryProvider` unpacks the key and passes its values to the VM constructor with
  `parametersOf` (P5) and to the Route as plain parameters when the UI needs them directly.
- `XScreen` and its children never receive a `NavKey`.
- The VM never calls `vm.load(id)` after creation (E5).

### MVVM-N3 — No side channels for forward data (MUST NOT)
All of these skip the key, so the data is gone after process death and the destination
depends on whatever the previous screen did before it:

```kotlin
// ❌ Static/singleton "holder" set right before navigating
SelectedTask.current = task; backStack.add(TaskDetailKey)
// ❌ Shared Activity-scoped VM used as a handoff (V6)
sharedVm.selected = task
// ❌ CompositionLocal / remember at the NavDisplay level carrying screen data
// ❌ Writing to the next screen's SavedStateHandle
```

If the data is too big for a key, it belongs in the data layer: save it (or put it in an in-memory
repository) and pass the ID.

---

## Returning a result to the opener

### MVVM-N4 — Results go through the back-stack owner and are consumed once (MUST)
Use this for answers that are **not data the app keeps**: the item chosen in a picker, the answer
to a confirm dialog destination, "task 42 was deleted, offer undo".

1. The **result screen's Route** exposes a lambda (`onLabelPicked: (LabelId) -> Unit`). It does not
   know who opened it.
2. The **`entryProvider`** (owner of the back stack, E5) implements that lambda: store the result,
   then pop.
3. The **opener's entry** receives the result, forwards it to its VM through an intent function
   (`viewModel.selectLabel(id)`), then clears it.
4. The VM treats it as normal input: writes it into its state, or into `SavedStateHandle` if the
   user would miss it after process death (S5).

Payloads follow N1: IDs and small values only. The VM never sees the result mechanism. Two VMs
never talk to each other (L7).

We use Navigation 3's **Result API** (`androidx.navigation3.runtime.result`, Navigation 3 ≥ 1.2,
in `navigation3-runtime`):

| API | Role |
|---|---|
| `rememberResultEventBusNavEntryDecorator()` | Gives every entry a `LocalResultEventBus` |
| `LocalResultEventBus.current.sendResult(resultKey, result)` | Step 2: send from the result screen's entry block |
| `ResultEffect<T>(resultKey) { … }` | Step 3: receive in the opener's entry block (queued, in order) |
| `resultBus.removeResult(resultKey)` | Step 3: clear after forwarding, so it isn't re-delivered when the entry re-enters composition |
| `resultBus.conflateAsState<T>(resultKey, default)` | Only for results that are pure UI state (E1/S2); never instead of forwarding to a VM |
| `rememberResultEventBus()` | Hoist the bus when results must cross `NavDisplay`s or back stacks (see notes) |

**Result keys are always explicit strings** (MUST), declared once next to the result screen's
`NavKey`. Payloads are IDs/small values (N1), which are exactly the shared types that type-derived
keys would mix up, and a sender using an explicit key is never received by a type-derived
`ResultEffect` (or the reverse).

```kotlin
// label/picker/LabelPickerKey.kt
@Serializable data class LabelPickerKey(val selectedLabelId: Long?) : NavKey
const val LABEL_PICKED_RESULT = "label_picked"         // payload: Long (label id)
```

```kotlin
val backStack = rememberNavBackStack(TaskListKey)

NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
        rememberResultEventBusNavEntryDecorator(),
    ),
    entryProvider = entryProvider {
        entry<EditTaskKey> { key ->
            val viewModel = koinViewModel<EditTaskViewModel> { parametersOf(key.taskId) }
            val resultBus = LocalResultEventBus.current
            ResultEffect<Long>(resultKey = LABEL_PICKED_RESULT) { labelId ->           // 3
                viewModel.selectLabel(labelId)
                resultBus.removeResult(resultKey = LABEL_PICKED_RESULT)
            }
            EditTaskRoute(
                viewModel = viewModel,
                onPickLabel = { current -> backStack.add(LabelPickerKey(selectedLabelId = current)) },
                onBack = { backStack.removeLastOrNull() },
            )
        }
        entry<LabelPickerKey> { key ->
            val resultBus = LocalResultEventBus.current
            LabelPickerRoute(
                selectedLabelId = key.selectedLabelId,                                 // N1: small parameter in
                onLabelPicked = { id ->                                                // 1
                    resultBus.sendResult(resultKey = LABEL_PICKED_RESULT, result = id)  // 2
                    backStack.removeLastOrNull()
                },
                onBack = { backStack.removeLastOrNull() },
            )
        }
    },
)
```

- `LocalResultEventBus` is read **only in entry blocks**, never in a Route, Screen, or VM.
- `ResultEffect`'s body runs in a coroutine, but keep it to "forward + remove". Snackbars and
  other reactions come from the VM's state (E3), not from the effect.

Notes:
- The bus is in memory. A result pending at the exact moment of process death is lost. That is
  acceptable for picker/confirm answers; anything that must not be lost goes to the repository (N5).
- One result key per kind of answer. Two pickers that both return a `Long` get two keys.
- If the result screen can be opened from several places, every opener installs its own
  `ResultEffect` on the same key; whichever is composed receives it and removes it.
- The decorator creates one bus per decorator instance. When an app keeps several back stacks
  with their own decorators (e.g. one per bottom-nav tab, as in `:roadmap:android`), or a flow
  hosts a nested `NavDisplay` (V5), and a result must cross between them, hoist one bus with
  `rememberResultEventBus()` and pass it to every `rememberResultEventBusNavEntryDecorator(resultEventBus = …)`.

---

## Notifying other screens

### MVVM-N5 — A data change is not an event (MUST)
When screen B edits, creates or deletes something that screen A shows, A finds out by **observing
the repository** (V4), never by being told.

```kotlin
// ❌ Telling the list to refresh
results.send(TaskChanged, taskId)
LaunchedEffect(Unit) { viewModel.refresh() }   // "refresh on resume"
// ❌ Repository emitting "something changed" pings that VMs react to by reloading
val changes = MutableSharedFlow<Unit>()

// ✅ The list VM already observes repository.observeTasks(); B's write makes it re-emit
```

If the data layer can't be observed (network-only API), make it observable: cache in the
repository (`MutableStateFlow` or Room) and have writes update the cache. Don't move the problem
into the UI.

### MVVM-N6 — App-level messages are state in one queue (MUST)
Use this for user-visible messages that must **outlive the screen that caused them**, or that come
from **outside any screen**: "Saved" right before the editor closes, "Sync finished",
"Upload failed".

```kotlin
data class UserMessage(val id: Long, val text: UiText, val action: MessageAction? = null)

class UserMessages {                               // Koin `single`, data/ui-shell layer
    private val _messages = MutableStateFlow<List<UserMessage>>(emptyList())
    val messages: StateFlow<List<UserMessage>> = _messages.asStateFlow()
    private val nextId = AtomicLong()

    fun post(text: UiText, action: MessageAction? = null) =
        _messages.update { it + UserMessage(nextId.incrementAndGet(), text, action) }

    fun shown(id: Long) = _messages.update { list -> list.filterNot { it.id == id } }
}
```

- Screen VMs and workers **post**. One host **shows**: the app shell around the `NavDisplay`. Its
  VM is Activity-scoped, which is the "truly about the Activity" case allowed by V6. It exposes the
  head of the queue and `messageShown(id)`, exactly like E3.
- This is E3 lifted to app scope: messages wait in state until shown, then are removed by id. No
  `SharedFlow`/`Channel` (E4).
- Undo and other actions are carried as data (`MessageAction.UndoDelete(taskId)`). The shell VM
  handles them by calling the repository.

**Screen message (E3) or app message (N6)?**

| The message… | Use |
|---|---|
| is about this screen and only matters while it is visible | `userMessage` in the screen's UiState (E3) |
| is caused by this screen, but the screen closes right after | App queue (N6) |
| answers the screen that opened this one, which then decides what to show | Result (N4); the opener's VM sets its own `userMessage` |
| comes from a worker, sync, or another non-screen source | App queue (N6) |

### MVVM-N7 — App-wide conditions are state; the back-stack owner reacts (MUST)
Logout, session expired, account switched, forced update, onboarding not done: these are **state**
in a repository (`SessionRepository.session: StateFlow<Session>`), not broadcasts.

- Only the code that owns the back stack observes it and rewrites the stack (E5), for example
  clearing to `SignInKey` when the session becomes `SignedOut`.
- Screens don't each listen for "logged out". Their VMs are cleared because their entries leave the
  back stack (L9).
- Repositories holding user data reset themselves from the same session state, not from a screen.

```kotlin
@Composable
fun AppRoot(viewModel: AppViewModel = koinViewModel()) {
    val session by viewModel.session.collectAsStateWithLifecycle()
    val backStack = rememberNavBackStack(SplashKey)

    LaunchedEffect(session) {
        when (session) {
            Session.SignedOut -> { backStack.clear(); backStack.add(SignInKey) }
            is Session.SignedIn -> if (backStack.lastOrNull() in setOf(SplashKey, SignInKey)) {
                backStack.clear(); backStack.add(HomeKey)
            }
            Session.Unknown -> Unit
        }
    }
    AppNavDisplay(backStack)
}
```

### MVVM-N8 — No event bus between screens or ViewModels (MUST NOT)
No app-wide `MutableSharedFlow`, `Channel`, `EventBus`, `LiveData` "event" singleton,
`LocalBroadcastManager`, or static listener lists for screen-to-screen communication. They drop
events when nobody is collecting, deliver them twice on re-subscription, hide who depends on whom,
and can't be asserted as state in tests. This is E4 applied across screens.

**Only exception:** Navigation 3's `ResultEventBus`, used exactly as N4 describes.

### MVVM-N9 — Two panes on screen: selection is the back stack (MUST)
In an adaptive list–detail layout, both entries are composed at once. They still don't talk:

- Selecting an item adds/replaces `TaskDetailKey(id)` on the back stack. The detail VM gets the ID
  from its key (N2).
- The list learns which item is selected from the back stack, passed in by the entry block:
  `selectedTaskId = (backStack.lastOrNull() as? TaskDetailKey)?.taskId`.
- Edits in the detail pane reach the list through the repository (N5).
- No ViewModel shared by both panes, no selection mirrored in two VMs.

---

## Testing these

- **N1/N2:** VM tests construct the VM with the ID directly; no navigation needed (T1).
- **N4:** The VM side is a plain intent (`selectLabel(id)`): test it directly. The bus lives only in
  entry blocks, so Screens and VMs never see it.
- **N5:** Write through the fake repository, assert the *other* VM's state updates.
- **N6:** Fake `UserMessages`. Assert the VM posted the expected `UiText`; test the shell VM's
  `messageShown(id)` separately.
- **N7:** Test the session → back stack mapping as a pure function
  (`fun stackFor(session: Session, current: List<NavKey>): List<NavKey>?`) if it grows beyond a few lines.

## Summary

| Situation | Do | Rule |
|---|---|---|
| Open detail for an item | `TaskDetailKey(taskId)`; VM loads by ID | N1, N2 |
| "I'll pass the whole object to save a read" | Pass the ID | N1 |
| Data too big for a key | Save it to a repository, pass the ID | N1, N3 |
| Picker returns a choice | Result via entry block, consumed once into opener's VM | N4 |
| Detail edited a task the list shows | Nothing to send; list observes repository | N5 |
| "Saved" after the editor closes | App message queue | N6 |
| Background sync finished | App message queue | N6 |
| Session expired | Session state; root rewrites back stack | N7 |
| Two panes need the same selection | Selection = detail key on the back stack | N9 |
| Global `SharedFlow<AppEvent>` | Never | N8 |
