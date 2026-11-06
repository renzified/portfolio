# MVVM — House Rules (Jetpack Compose · Koin · Navigation 3)

A rulebook, not a tutorial. Every rule has an ID so it can be cited in code review
(`"breaks MVVM-C2"`). Words are used strictly:

| Word | Meaning |
|---|---|
| **MUST / MUST NOT** | No exceptions unless the rule itself lists one. |
| **SHOULD** | Default. Deviating requires a comment in code explaining why. |
| **MAY** | Allowed when the linked decision tree says so. |

## Chapters

| # | File | Covers |
|---|---|---|
| 1 | [01-layers-and-naming.md](docs/01-layers-and-naming.md) | Route/Screen split, who may touch the ViewModel, Nav3 scoping, naming, folder layout |
| 2 | [02-consistency-vs-granularity.md](docs/02-consistency-vs-granularity.md) | `StateFlow<UiState>` vs snapshot state, atomic writes, invalidation scope |
| 3 | [03-where-state-lives.md](docs/03-where-state-lives.md) | ViewModel vs `remember` vs plain state holder class |
| 4 | [04-splitting-viewmodels.md](docs/04-splitting-viewmodels.md) | One vs many ViewModels, delegates, multi-step flows, sharing data |
| 5 | [05-modeling-ui-state.md](docs/05-modeling-ui-state.md) | Sealed vs flags, derived values, lists, strings, UI models |
| 6 | [06-producing-state.md](docs/06-producing-state.md) | `stateIn`, `combine`, no `init {}`, retry/refresh, SavedStateHandle, Paging, errors |
| 7 | [07-events.md](docs/07-events.md) | UI → VM actions, VM → UI one-off outcomes (navigation, snackbar) |
| 8 | [08-input-and-high-frequency.md](docs/08-input-and-high-frequency.md) | Text fields, search debounce, gestures, animations, phase deferral |
| 9 | [09-testing.md](docs/09-testing.md) | Testing both state styles and stateless screens |
| 10 | [10-between-screens.md](docs/10-between-screens.md) | Nav key contents, returning results, notifying other screens, app-level messages, session changes |
| 11 | [11-review-checklist.md](docs/11-review-checklist.md) | Anti-patterns + PR checklist |

---

## The four decision trees

Run trees 1–3 in order whenever you add a new piece of state. Run tree 4 whenever a value or an
"it happened" has to leave the screen that produced it.

### Tree 1 — Where does this state live? (chapter 3)

```
Is it read or changed by business logic, or does it come from / go to the data layer?
├── YES → ViewModel                                                    (MVVM-S1)
└── NO  → It is UI element state.
          Is the logic around it more than trivial, or shared by several composables?
          ├── YES → Plain state holder class + rememberXState()        (MVVM-S3)
          └── NO  → remember { } / rememberSaveable { } in the composable (MVVM-S2)

Would the user be annoyed to lose it on rotation / process death?  → rememberSaveable (MVVM-S5)
Does it change every frame (gesture, animation)?                    → never in the VM  (MVVM-I2)
```

### Tree 2 — How does the ViewModel expose it? (chapter 2)

```
Does any field update continuously (≥ several times per second for a while:
ticker, game loop, upload progress, playback position, sensor)?
├── NO  → StateFlow<XUiState> (one immutable data class)                (MVVM-C1)  ← DEFAULT
└── YES → Is recomposing the screen root on each tick measurably expensive,
          or are there many independent readers of different fields?
          ├── NO  → StateFlow<XUiState> (stay on the default)
          └── YES → Snapshot-state class (read-only interface + private mutable impl) (MVVM-C4)
```

### Tree 3 — Which ViewModel owns it? (chapter 4)

```
Is the state for a section that is self-contained (own data source) AND reused on several screens?
├── YES → That section's own ViewModel, obtained in the Route              (MVVM-V3)
└── NO  → Is it shared by several destinations of one multi-step flow?
          ├── YES → Flow entry owns a shared ViewModel           (MVVM-V5)
          └── NO  → Is it shared by unrelated screens?
                    ├── YES → Repository is the source of truth; each VM observes it (MVVM-V4)
                    └── NO  → The destination's ViewModel                    (MVVM-V1)
                              Getting big? Split logic into delegates inside it (MVVM-V2)
```

### Tree 4 — How does this reach the other screen? (chapter 10)

```
Is it data the app keeps (persisted, cached, or shown on more than one screen)?
├── YES → Write it to the repository; other screens observe it. Send nothing.   (MVVM-N5)
└── NO  → Is it what the next screen needs in order to open?
          ├── YES → ID / small value in the NavKey                                (MVVM-N1, N2)
          └── NO  → Is it an answer for the screen that opened this one?
                    ├── YES → Navigation result, consumed once by the opener      (MVVM-N4)
                    └── NO  → Message for the user → app-level message queue      (MVVM-N6)
                              App-wide condition   → session/app state in a repo  (MVVM-N7)
Never an event bus.                                                              (MVVM-N8)
```

---

## Rule index

### Layers & naming — ch. 1
| ID | Rule |
|---|---|
| L1 | State flows down, events flow up (UDF). |
| L2 | Every destination = `XRoute` (stateful, owns VM) + `XScreen` (stateless). |
| L3 | MUST NOT pass a ViewModel below the Route. |
| L4 | MUST NOT hold Android/UI framework objects in a ViewModel (one listed exception). |
| L5 | Expose read-only state; keep mutable state `private`. |
| L6 | Collect flows with `collectAsStateWithLifecycle()`. |
| L7 | ViewModels never reference other ViewModels. |
| L8 | Naming: `XViewModel`, `XUiState`, `XRoute`, `XScreen`; VM functions are intent verbs, UI lambdas are `onXxx`. |
| L9 | Every `NavDisplay` installs `rememberViewModelStoreNavEntryDecorator()` so VMs are scoped per entry. |

### Consistency vs granularity — ch. 2
| ID | Rule |
|---|---|
| C1 | Default: one immutable `XUiState` data class in a `StateFlow`. |
| C2 | Write with `_uiState.update { it.copy(...) }`, never `value = value.copy(...)`. |
| C3 | Fields written by the same event live in the same state unit. |
| C4 | Snapshot-state class only via Tree 2; then C4a–C4d apply. |
| C5 | One ViewModel = one style. Don't mix `StateFlow` UI state and `mutableStateOf` UI state (one listed exception). |
| C6 | MUST NOT split a UiState into per-field flows to "gain granularity". |
| C7 | Compute new values into locals first, then write. |
| C8 | StateFlow style: pass children only the fields they read. Snapshot style: pass the state object to defer reads. |

### Where state lives — ch. 3
| ID | Rule |
|---|---|
| S1 | Business-relevant state → ViewModel. |
| S2 | Pure UI element state → `remember`/`rememberSaveable`. |
| S3 | Non-trivial UI logic → plain state holder class + `rememberXState()`. |
| S4 | Hoist to the lowest common ancestor, no higher. |
| S5 | User-visible choices that must survive rotation/process death → `rememberSaveable` or `SavedStateHandle`. |
| S6 | The moment business logic needs a UI value, move it to the VM. |
| S7 | Single source of truth — never mirror the same state in VM and UI. |

### Splitting ViewModels — ch. 4
| ID | Rule |
|---|---|
| V1 | Default: one ViewModel per navigation destination. |
| V2 | Large VM → split into plain delegate classes owned by the VM, not more VMs. |
| V3 | Extra VM on one screen only for a self-contained, reused section. |
| V4 | Cross-screen shared data → repository as source of truth, never VM-to-VM. |
| V5 | Multi-step flow with in-progress data → one flow entry owning the shared VM + inner back stack. |
| V6 | MUST NOT use an Activity-scoped VM as a global store. |
| V7 | Dialog/sheet: own destination with own logic → own VM; otherwise part of parent UiState. |

### Modeling UI state — ch. 5
| ID | Rule |
|---|---|
| M1 | Mutually exclusive phases → sealed interface. Overlays → flags inside the content state. |
| M2 | Don't store derived values; compute them. |
| M3 | Collections in UiState are read-only `List`/`Map`, never mutated after creation. |
| M4 | Lazy list items MUST have a stable `key`. |
| M5 | ViewModels don't resolve strings; expose `UiText`/`@StringRes`/raw data. |
| M6 | Map domain → UI models in the VM when the shape differs. |
| M7 | UiState always has a meaningful initial value; never `StateFlow<XUiState?>`. |
| M8 | No UI types (`Color`, `Dp`, `Painter`, composables) in UiState. |

### Producing state — ch. 6
| ID | Rule |
|---|---|
| P1 | Streams → `.stateIn(viewModelScope, WhileSubscribed(5_000), initial)`. |
| P2 | User-driven inputs → private `MutableStateFlow`s combined with streams. |
| P3 | No work in `init {}`. |
| P4 | Public VM functions are non-suspend, main-safe, fire-and-forget. |
| P5 | Nav args via constructor (`parametersOf(key…)`); `SavedStateHandle` only for user input. |
| P6 | Paging: separate `Flow<PagingData>` with `cachedIn(viewModelScope)` (explicit exception to C1). |
| P7 | Errors are state; catch them in the flow. |
| P8 | Inject dispatchers; never hardcode `Dispatchers.IO`. |

### Events — ch. 7
| ID | Rule |
|---|---|
| E1 | UI-only events (no business logic) are handled in the UI. |
| E2 | Actions needing logic → VM function. |
| E3 | VM → UI one-off outcomes are state + a "consumed" callback. |
| E4 | MUST NOT use `SharedFlow`/`Channel` for VM → UI events. |
| E5 | Navigation executes in the Route, never in the VM. |
| E6 | State-triggered effects live in the Route, keyed on the outcome; permissions/launchers are UI effects. |

### Input & high frequency — ch. 8
| ID | Rule |
|---|---|
| I1 | Text input owned by the VM uses `TextFieldState`, never a `StateFlow` round-trip. |
| I2 | Per-frame gesture/animation values never go to the VM; commit the final value only. |
| I3 | Read fast-changing values in layout/draw phase via lambda modifiers. |
| I4 | Debounce/throttle in the VM, not in the UI. |

### Testing — ch. 9
| ID | Rule |
|---|---|
| T1 | VM tests use fakes + `MainDispatcherRule`. |
| T2 | `WhileSubscribed` flows need a collector in `backgroundScope`. |
| T3 | Snapshot-style VMs are asserted by reading properties directly. |
| T4 | UI tests target `XScreen` with fake state, no ViewModel. |

### Between screens — ch. 10
| ID | Rule |
|---|---|
| N1 | Nav keys carry IDs and small parameters, never domain/UI objects or lists. |
| N2 | Key → entry → VM constructor (`parametersOf`); nothing below the Route sees the key. |
| N3 | No side channels for forward data (static holders, shared VMs, CompositionLocals). |
| N4 | Results go through `ResultEventBus` in entry blocks (explicit keys), are forwarded to an opener VM intent, then removed. |
| N5 | A data change is not an event: other screens observe the repository; no "refresh" pings. |
| N6 | Messages that outlive a screen go into one app-level queue (state + `shown(id)`). |
| N7 | App-wide conditions (logout, session expired) are repository state; only the back-stack owner reacts. |
| N8 | MUST NOT use an event bus between screens or VMs (only exception: Nav3 `ResultEventBus` per N4). |
| N9 | Two panes: selection is the back stack, not shared VM state. |
