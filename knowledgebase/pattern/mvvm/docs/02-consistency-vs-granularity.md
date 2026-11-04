# 2. Consistency vs granularity

Source: *Android Dev Summit — State in Compose* (the `GameUiState` slides).

| | Meaning |
|---|---|
| **Consistency: isolation** | Writes stay local until they're committed. Nobody sees a half-done update. |
| **Consistency: atomicity** | All writes from one update become visible at the same time. |
| **Granularity: separate properties** | State is split by concern, so you can track reads per field. |
| **Granularity: invalidation** | Only the composables that read the changed field recompose. |

## The two styles side by side

### Style A — `StateFlow<data class>`: consistency ✅, granularity ❌

```kotlin
data class GameUiState(
    val levelName: String = "Level 1 - Stay Alive!",
    val timeSurvived: Int = 0,
    val score: Double = 0.0,
)

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun tick() = _uiState.update {
        val time = it.timeSurvived + 1                 // MVVM-C7: compute first
        it.copy(timeSurvived = time, score = it.score + 10 + .01 * time)
    }
}

@Composable
fun GameRoute(viewModel: GameViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()   // read happens HERE
    Column {
        Header(levelName = state.levelName)
        ScoreBadge(timeSurvived = state.timeSurvived, score = state.score)
    }
}
```

What the snapshot observer records:

```
Readers:  state  → [ GameRoute ]           Writes: tick → { state }
⇒ every tick invalidates GameRoute (the whole scope reruns)
```

- **Consistent:** one immutable object, one `update {}` → atomic by construction.
- **Coarse:** the whole Route body reruns on every tick. `Header` is then *skipped* because its
  parameter `levelName` is equal (strong skipping), so the real cost is "rerun the root + compare
  params", not "redraw everything". For most screens that cost is negligible — which is why this is
  the default.

### Style B — snapshot-state class: consistency ✅, granularity ✅

```kotlin
@Stable
interface GameUiState {                                   // MVVM-C4a: read-only face
    val levelName: String
    val timeSurvived: Int
    val score: Double
}

private class MutableGameUiState : GameUiState {          // MVVM-C4a: private mutable impl
    override var levelName by mutableStateOf("Level 1 - Stay Alive!")
    override var timeSurvived by mutableIntStateOf(0)     // MVVM-C4c: primitive specialisation
    override var score by mutableDoubleStateOf(0.0)
}

class GameViewModel : ViewModel() {
    private val _uiState = MutableGameUiState()
    val uiState: GameUiState = _uiState

    fun tick() = Snapshot.withMutableSnapshot {           // MVVM-C4b: atomic multi-field write
        val time = _uiState.timeSurvived + 1              // MVVM-C7
        _uiState.timeSurvived = time
        _uiState.score += 10 + .01 * time
    }
}

@Composable
fun GameRoute(viewModel: GameViewModel = koinViewModel()) {
    val state = viewModel.uiState                          // NO read here – just a reference
    Column {
        Header(state = state)                              // reads levelName inside
        ScoreBadge(state = state)                          // reads timeSurvived + score inside
    }
}

@Composable private fun Header(state: GameUiState) = Text(state.levelName)
@Composable private fun ScoreBadge(state: GameUiState) =
    Text("${state.timeSurvived}s · ${"%.2f".format(state.score)}")
```

```
Readers:  levelName    → [ Header ]          Writes: tick → { timeSurvived, score }
          timeSurvived → [ ScoreBadge ]
          score        → [ ScoreBadge ]
⇒ every tick invalidates ScoreBadge only. GameRoute and Header never rerun.
```

> Note: in Style B, passing the **state object** (not its fields) is what defers the read into the
> child. Passing `state.score` would read it in the parent and lose the granularity.

## Rules

### MVVM-C1 — Default is Style A (MUST)
Every ViewModel starts with one immutable `XUiState` data class in a `StateFlow`. Reasons: trivially
atomic, easy to test (`assertEquals(expected, state)`), works with `stateIn/combine`, survives
non-Compose consumers.

### MVVM-C2 — Write with `update {}` (MUST)
```kotlin
_uiState.update { it.copy(isSaving = true) }          // ✅ atomic compare-and-set, safe from any thread
_uiState.value = _uiState.value.copy(isSaving = true) // ❌ read-modify-write race: concurrent writes get lost
```

### MVVM-C3 — Same event ⇒ same state unit (MUST)
If one event writes fields `a` and `b`, they must live in the same `StateFlow` (Style A) or be written
in the same `withMutableSnapshot` (Style B). Otherwise the UI can render a frame where `a` is new and
`b` is old.

```kotlin
// ❌ Two flows written by one event → torn frame possible (items updated, isLoading still true)
private val _items = MutableStateFlow(emptyList<Item>())
private val _isLoading = MutableStateFlow(false)

// ✅ One unit
data class ListUiState(val items: List<Item> = emptyList(), val isLoading: Boolean = false)
```

### MVVM-C4 — Style B only when Tree 2 says so (MAY)
Use the snapshot-state class when a field updates continuously **and** recomposing the root per update
is measurable (Layout Inspector recomposition counts / macrobenchmark) or there are many independent
readers. When you use it:

- **C4a (MUST)** Read-only `@Stable interface` public, `private class Mutable…` implementation.
  Never expose `var` with public setters (the slide's `var` is for brevity only).
- **C4b (MUST)** Any write touching more than one property goes inside
  `Snapshot.withMutableSnapshot { }`. This gives isolation (nobody sees partial writes, even from a
  background thread) and atomicity (all applied together on exit).
  Single-property writes may be bare.
- **C4c (SHOULD)** Use `mutableIntStateOf`, `mutableLongStateOf`, `mutableFloatStateOf`,
  `mutableDoubleStateOf` for primitives (no boxing).
- **C4d (SHOULD)** Use `mutableStateListOf` / `mutableStateMapOf` for collections that change
  element-by-element, so readers of one entry don't depend on the whole collection reference.
  Expose them as `List`/`Map`.

```kotlin
private class MutableBoardState : BoardState {
    override val cells = mutableStateListOf<Cell>()           // exposed as List<Cell>
}
```

### MVVM-C5 — One style per ViewModel (MUST)
A ViewModel exposes its UI state **either** as `StateFlow<XUiState>` **or** as a snapshot-state class.
Never "some fields as flow, some as `mutableStateOf`".
**Only exception:** `TextFieldState` for text input (MVVM-I1) may sit next to a `StateFlow` UiState.

### MVVM-C6 — Don't fake granularity with per-field flows (MUST NOT)
```kotlin
// ❌ Breaks C3, triples the boilerplate, and the reads all still happen in the Route.
val score = uiState.map { it.score }.distinctUntilChanged().stateIn(...)
val time  = uiState.map { it.timeSurvived }.distinctUntilChanged().stateIn(...)
```
If you need granularity, switch the ViewModel to Style B.

### MVVM-C7 — Compute new values into locals, then write (MUST)
The two styles read differently during an update:

```kotlin
// Style A — `it` is the OLD state for the whole lambda
it.copy(timeSurvived = it.timeSurvived + 1,
        score = it.score + 10 + .01 * it.timeSurvived)       // uses OLD time (0) → 10.0

// Style B — inside a snapshot you see your OWN earlier writes
uiState.timeSurvived += 1
uiState.score += 10 + .01 * uiState.timeSurvived             // uses NEW time (1) → 10.01
```
The slides' two versions therefore produce **different scores**. Always compute into locals first
so the result is the same in both styles and obvious to the reader:

```kotlin
val time = current.timeSurvived + 1
val score = current.score + 10 + .01 * time
```

### MVVM-C8 — Pass what's read (SHOULD)
- **Style A:** pass children the **individual fields** they use (`Header(levelName = state.levelName)`),
  never the whole `XUiState`. Unchanged params → child skips.
- **Style B:** pass the **state object** and read inside the child, to defer the read.
- Both styles: values that change every frame are read in layout/draw phase (MVVM-I3).

## Quick comparison

| | Style A: `StateFlow<data class>` | Style B: snapshot-state class |
|---|---|---|
| Atomic multi-field update | `update { copy() }` – automatic | `withMutableSnapshot {}` – you must remember (C4b) |
| Invalidation | Every reader of `state` (usually the Route) | Only readers of the changed field |
| Equality / testing | `assertEquals(data class)` | Assert each property |
| Operators (`combine`, `stateIn`, `debounce`) | Native | Need `snapshotFlow {}` bridge |
| Non-Compose consumers | Yes | Compose runtime required |
| When | **Default** | High-frequency updates, measured cost |
