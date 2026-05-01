# AGENTS.md

## Scope
- This repo is a single-module Android app: `:app` only (`settings.gradle.kts`). Package root is `com.quetoquenana.and`.
- Tech stack in active use: Jetpack Compose, Hilt, Room, Retrofit + Moshi + OkHttp, Firebase Auth/Analytics, Google Sign-In, Timber (`app/build.gradle.kts`, `gradle/libs.versions.toml`).

## App shape / entry flow
- Bootstrap path is `AndroidManifest.xml` → `PedalPalApp` → `MainActivity`.
- `PedalPalApp` enables Timber and, in debug builds, points Firebase Auth to the local emulator **only when running on an Android emulator** (`PedalPalApp.kt`). Physical devices intentionally skip `10.0.2.2:9099`.
- `MainActivity` owns the top-level `Scaffold`; `AppNavGraph` starts at `Startup.route`, not `Home.route`.
- Session restore decides the first screen: `StartupViewModel` calls `RestoreSessionUseCase`, then routes to `Home`, `Authentication`, or `CompleteProfile`.

## Architecture patterns that matter here
- Code is feature-sliced under `app/src/main/java/com/quetoquenana/and/features/<feature>/...` with `domain`, `data`, and `ui` packages when the slice is fleshed out.
- UI pattern: route composable + Hilt VM + `StateFlow` state + flow-based one-off events. Most screens use `MutableSharedFlow`/`SharedFlow`; `StartupViewModel` is the current exception and uses a buffered `Channel` exposed via `receiveAsFlow()`. See `features/authentication/ui/AuthenticationScreen.kt`, `features/bikes/ui/BikesScreen.kt`, and `StartupScreen.kt`.
- Navigation metadata lives in `core/ui/navigation/Screen.kt`; top/bottom bar visibility is derived from that metadata in `NavDestinations.kt`. If you add a screen, update both route definitions and visibility behavior. For parameterized/query routes, keep the `createRoute(...)` helpers in `Screen.kt` aligned with `routeMatches(...)` in `NavDestinations.kt`.
- Cross-composable navigation is exposed through `LocalNavigator` / `ProvideNavigator`, but some routes still pass callbacks directly from `AppNavGraph`. Follow the style already used in that area instead of forcing one pattern everywhere.

## Data flow / service boundaries
- `GetHomeContentUseCase` is the main aggregator: it pulls appointments, suggestions, announcements, and bikes from separate repositories to build `HomeUiState`.
- Bikes are a real remote + local slice: `BikeApi` on `pedalPalServiceRetrofit` backs bike lists/details/history/create plus Strava import endpoints, while `BikeRepositoryImpl` caches list responses into Room and reads detail/history/Strava data from the backend.
- Auth is the most complete vertical slice:
  - Firebase sign-in/sign-up lives in `FirebaseAuthDataSourceImpl`.
  - Backend registration lives in `AuthRemoteDataSourceRetrofit` / `AuthApi`.
  - Local session/user cache lives in Room entities `AuthSessionEntity` and `AuthUserEntity`.
  - `AuthRepositoryImpl` stitches Firebase + backend + Room together.
- Network auth is centralized:
  - `AuthInterceptor` injects `Authorization` unless request header `No-Auth: true` is present.
  - `TokenAuthenticator` refreshes via `AuthRefreshApi` on 401s.
  - Retrofit instances are named; use the correct one: `userServiceRetrofit`, `pedalPalServiceRetrofit`, `refreshRetrofit` (`RetrofitModule.kt`).
- API responses are expected to be wrapped in `ApiResponse<T>`.

## Project-specific gotchas
- `TokenStorageImpl` is currently in-memory only. Tokens survive neither process death nor app restart.
- Room uses `fallbackToDestructiveMigration()` in `AppDatabase`; schema changes will wipe local data in dev builds.
- `BikeRepositoryImpl.getBikes(refresh = false)` reads Room only unless callers explicitly request refresh. `Home` uses the default, so empty bike data can simply mean the local cache was never seeded.
- Suggestions are still backed by a deterministic fake remote data source (`SuggestionsRemoteDataSourceImpl`). Appointments and announcements now go through `AppointmentApi` / `AnnouncementApi` on `pedalPalServiceRetrofit`.
- Some app shell pieces are placeholders/TODOs (for example `MainViewModel` badge count, `ProfileScreen`, and `BikeComponentOptionsScreen` action wiring). Check implementation depth before extending a feature.

## Build / test / debug workflows
- Required local config: `GOOGLE_WEB_CLIENT_ID` must exist in `local.properties`; build config injects it into `BuildConfig`, and `GoogleAuthModule` uses it to configure `GoogleSignInClient` (`app/build.gradle.kts`, `GoogleAuthModule.kt`). `app/google-services.json` is already committed.
- Common verified Gradle tasks:
  - `./gradlew :app:assembleDebug`
  - `./gradlew :app:installDebug`
  - `./gradlew :app:testDebugUnitTest`
  - `./gradlew :app:connectedDebugAndroidTest`
- Firebase Auth emulator workflow comes from `README.md`:
  - `firebase login --reauth`
  - `firebase use --add`
  - `firebase init emulators` (choose Auth)
  - `firebase emulators:start`
- For local Google auth setup, `README.md` also documents how to print the debug keystore SHA-1.

## Testing conventions in this repo
- Unit tests mostly instantiate real use cases with manual fakes rather than using DI test replacements; see `app/src/test/java/com/quetoquenana/and/auth/...`.
- ViewModel tests commonly swap `Dispatchers.Main` for `StandardTestDispatcher` and collect one-off event flows with `CompletableDeferred`/`withTimeoutOrNull`; see `app/src/test/java/com/quetoquenana/and/auth/ui/AuthenticationViewModelTest.kt` and `app/src/test/java/com/quetoquenana/and/bikes/ui/AddBikeViewModelTest.kt`.
- Compose instrumentation tests under `app/src/androidTest` assert by visible text and click rendered text nodes (`AppointmentsRowTest.kt`, `SuggestionsRowTest.kt`, `BikesScreenTest.kt`).

## When adding or changing code
- Prefer adding Hilt bindings in the feature’s own `di` package (`features/authentication/di`, `features/bikes/data/.../di`) rather than creating catch-all modules.
- Keep DTO ↔ domain ↔ Room mapping functions close to their model types, as this repo already does (`toDto`, `toDomain`, `toEntity`).
- For navigation arguments, prefer adding/updating `createRoute(...)` helpers on the `Screen` object (`AddBike`, `BikeDetail`, `BikeHistory`, `BikeComponentOptions`) instead of hand-building routes inline.
- Preserve the current route/event/state naming style (`<Feature>Route`, `<Feature>ViewModel`, `UiState`, sealed event/result types).

