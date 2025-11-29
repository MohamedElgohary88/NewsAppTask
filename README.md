# NewsAppTask

A modern Android news reader sample built with Jetpack Compose, Paging 3, Retrofit, Room, Hilt, and Material 3.

This README documents the project structure, setup, build & run instructions, architecture decisions, and developer notes to help you maintain and extend the app.

---

## Table of Contents

- About
- Features
- Architecture & Patterns
- Project structure
- Requirements
- Setup
- Run
- Tests
- Code style & lint
- Key implementation details
- Troubleshooting
- Contribution
- License

---

## About

NewsAppTask is a sample Android application that fetches top headlines from a news API and presents them in a performant, modern UI using Jetpack Compose. The app demonstrates:

- Compose-first UI with Material 3 styling
- Pagination using Paging 3
- Networking with Retrofit + OkHttp
- Local caching and favorites with Room
- Dependency Injection using Hilt
- Image loading with Coil
- In-app browser using AndroidX Browser / Custom Tabs
- A strict UI-state ViewModel pattern (single StateFlow per screen)

This project has been refactored to centralize UI state into single `UiState` classes, move strings to Android resources, and consolidate design tokens (colors/dimens/typography).

## Features

- News list with infinite scrolling (Paging 3)
- Offline-aware UI with connectivity observer
- Save / remove favorites (Room)
- Details screen with expandable content and save button
- Share / open article in browser
- Swipe to delete in favorites
- Theming using Material 3 and a small design system (colors, dimens, type)

## Architecture & Patterns

- Clean-ish layered approach:
  - `data` layer: Retrofit service, DTOs, Room database, PagingSource
  - `domain` layer: Use cases and models (Article)
  - `presentation` layer: Compose UI, Screens, ViewModels (Hilt injection)
- ViewModel -> UI state:
  - Each screen exposes a single immutable `StateFlow<ScreenUiState>` representing the entire dynamic UI state (loading, errors, lists, booleans)
  - Paging is exposed as a Flow<PagingData<Article>> and collected in the composables using `LazyPagingItems`.
- One-off UI events (navigation, snackbars) are emitted by the ViewModel via a `SharedFlow` and collected by the `Route` composable which performs navigation or shows transient UI.

## Project structure (high-level)

- `app/` - Android app module
  - `src/main/java/com/elgohary/newsapptask/` - app packages
    - `data/` - data sources (network, db, paging)
    - `domain/` - models and use cases
    - `presentation/` - compose UI, viewmodels, navigation, design system
    - `MainActivity.kt` - app entrypoint (Compose host)
  - `src/main/res/` - resources (strings, layouts, icons)

## Requirements

- JDK 17
- Android SDK (compile/target SDK 36)
- Gradle 8.x (wrapper included)
- An API key for the News API (store in `local.properties`)

## Setup

1. Clone the repo

2. Add your News API key to `local.properties` in the project root:

```properties
NEWS_API_KEY=your_api_key_here
```

3. Open the project in Android Studio (Arctic Fox or newer recommended). The project uses Gradle catalog (libs.versions.toml) for dependency management.

## Build & Run

To build and install on a connected device/emulator:

```bash
# from project root
./gradlew assembleDebug
# or
./gradlew installDebug
```

From Android Studio: run the `app` configuration as usual.

## Tests

This project includes a baseline of unit/Android tests dependencies. To run tests:

```bash
./gradlew test        # run local unit tests
./gradlew connectedAndroidTest  # run instrumentation tests on device/emulator
```

(There are currently no heavy test suites committed; adding ViewModel unit tests is recommended.)

## Code style & lint

- Kotlin + Compose style is followed.
- Use `ktlint` / Android Studio lint rules (not enforced in the repo by default).
- Hilt is used for DI, and KSP is enabled for code generation (Room, Hilt).

## Key implementation details & developer notes

- Strings have been moved into `res/values/strings.xml` for localization readiness.
- A shared network helper `executeSafely` was added in `data/util/NetworkUtils.kt` which wraps API calls into a `Result<T>` for consistent error handling.
- `NewsPagingSource` implements robust paging behavior:
  - Uses `params.loadSize` (caps to a configured default page size)
  - Maps DTOs to domain safely with `mapNotNull` so bad DTOs don't crash a page
  - Uses `totalResults` (when provided by the API) to compute `nextKey`, otherwise falls back to returned count heuristic
  - Applies optional client-side filtering and deduplication by article URL

- Presentation layer implemented a strict UI-state rule:
  - Each screen exposes a single `StateFlow<UiState>` (e.g., `NewsListUiState`, `DetailsUiState`, `FavoritesUiState`) containing all the dynamic data for the screen.
  - Paging flows (`Flow<PagingData<Article>>`) remain cold and are collected in composables with `collectAsLazyPagingItems()`.
  - One-off UI events (navigation, snackbars) are emitted via `MutableSharedFlow<Event>` by ViewModels and collected in *Route* composables which perform navigation or transient UI effects.

- Design system files added/updated:
  - `presentation/designsystem/Colors.kt` — centralized colors
  - `presentation/designsystem/Dimens.kt` — spacing and UI defaults (gate delay, shimmer duration)
  - `presentation/designsystem/Type.kt` — typography tokens
  - `presentation/designsystem/Theme.kt` — Material 3 theme wiring

## Troubleshooting

- AAPT / `@string/app_name` errors: ensure `app/src/main/res/values/strings.xml` contains `<string name="app_name">NewsAppTask</string>`.
- Missing API key: make sure `local.properties` contains `NEWS_API_KEY` before building.
- Hilt codegen or Room KSP errors: run `./gradlew clean assembleDebug` to force regeneration.

## Contributing

Contributions are welcome. Please follow these guidelines:

1. Create a branch for your feature/fix.
2. Keep changes small and focused.
3. Write unit tests for new ViewModel logic.
4. Open a PR and include a short description and screenshots if UI changes are involved.

## License

This project is provided as-is for learning and demonstration purposes.

---

If you want, I can also:

- Run a full `./gradlew assembleDebug` and share the build output here.
- Add example unit tests for `DetailsViewModel` / `FavoritesViewModel`.
- Add a `CONTRIBUTING.md` with a PR checklist and developer setup steps.

Which do you want next?
