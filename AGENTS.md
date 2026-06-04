# AGENTS.md

## Scope
- This repo is a single-module Android app: `:app` only (`settings.gradle.kts`). Package root is `com.quetoquenana.and`.
- Tech stack in active use: Jetpack Compose, Hilt, Room, Retrofit + Moshi + OkHttp, Coil 3, Firebase Auth/Analytics, Google Sign-In, Timber (`app/build.gradle.kts`, `gradle/libs.versions.toml`).

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
- Appointments are now a cached remote + local slice: `AppointmentApi` backs list/detail/create, `AppointmentRepositoryImpl` persists `AppointmentEntity` + `AppointmentServiceEntity` in Room, and it enriches remote payloads with cached bike/store names before returning UI models.
- Bikes are a real remote + local slice: `BikeApi` on `pedalPalServiceRetrofit` backs bike lists/details/history/create plus Strava import endpoints, while `BikeRepositoryImpl` caches list responses into Room and reads detail/history/Strava data from the backend.
- Stores + services are now first-class dependencies of appointment booking: `StoreApi` caches stores/locations in Room, `ServiceCatalogApi` caches packages/products per `storeLocationId`, and `AddAppointmentViewModel` surfaces `ServiceCatalog.lastUpdated`, `isFromCache`, and `fetchErrorMessage` directly in the UI.
- Auth is the most complete vertical slice:
  - Firebase sign-in/sign-up lives in `FirebaseAuthDataSourceImpl`.
  - Backend registration lives in `AuthRemoteDataSourceRetrofit` / `AuthApi`.
  - Local session/user cache lives in Room entities `AuthSessionEntity` and `AuthUserEntity`.
  - `AuthRepositoryImpl` stitches Firebase + backend + Room together.
- Media is a shared cross-feature slice under `core/media`: `MediaRepositoryImpl` caches `MediaEntity` rows in Room, uses `MediaApi` to create/confirm media records, and uploads bytes through `MediaUploadRemoteDataSourceOkHttp` with backend-provided URLs. Bike images/profile photos and appointment payment proofs all go through this layer.
- Network auth is centralized:
  - `AuthInterceptor` injects `Authorization` unless request header `No-Auth: true` is present.
  - `TokenAuthenticator` refreshes via `AuthRefreshApi` on 401s.
  - `AcceptLanguageInterceptor` adds `Accept-Language` from `AcceptLanguageProvider` on both main and refresh clients; the provider currently only emits `es` or `en`.
  - Retrofit instances are named; use the correct one: `userServiceRetrofit`, `pedalPalServiceRetrofit`, `refreshRetrofit` (`RetrofitModule.kt`).
- API responses are expected to be wrapped in `ApiResponse<T>`.

## Project-specific gotchas
- `TokenStorageImpl` is currently in-memory only. Tokens survive neither process death nor app restart.
- Room uses `fallbackToDestructiveMigration()` in `AppDatabase`; schema changes will wipe local data in dev builds.
- `BikeRepositoryImpl.getBikes(refresh = false)` reads Room only unless callers explicitly request refresh. `Home` uses the default, so empty bike data can simply mean the local cache was never seeded.
- Suggestions are still backed by a deterministic fake remote data source (`SuggestionsRemoteDataSourceImpl`). Appointments and announcements now go through `AppointmentApi` / `AnnouncementApi` on `pedalPalServiceRetrofit`.
- `ProfileViewModel.onProfilePhotoSelected(...)` and the profile media pipeline exist, but `ProfileRoute` currently passes an empty `onEditPhotoClick`; profile photo picking/upload is not wired into the screen yet.
- Store refresh intentionally preserves each location's cached `serviceCatalogLastUpdatedAt` (`StoreRepositoryImpl`) so the appointment flow can keep showing service catalog freshness after stores are refreshed.
- Some app shell pieces are still placeholders/TODOs (for example `HomeScreen` suggestion click navigation). Check implementation depth before extending a feature.

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
- Repository/cache tests often use hand-rolled local/remote datasource fakes to verify Room scoping and fallback behavior rather than mocking every dependency; see `app/src/test/java/com/quetoquenana/and/services/data/repository/ServiceCatalogRepositoryImplTest.kt` and `app/src/test/java/com/quetoquenana/and/core/media/data/repository/MediaRepositoryImplTest.kt`.
- Core/network utilities are covered with plain JVM tests (`AcceptLanguageProviderTest.kt`, `UuidJsonAdapterTest.kt`, `BigDecimalJsonAdapterTest.kt`), so locale/JSON behavior is usually exercised outside Android-specific tests.
- Compose instrumentation tests under `app/src/androidTest` assert by visible text and click rendered text nodes (`AppointmentsRowTest.kt`, `SuggestionsRowTest.kt`, `BikesScreenTest.kt`).

## When adding or changing code
- Prefer adding Hilt bindings in the feature’s own `di` package (`features/authentication/di`, `features/bikes/data/.../di`) rather than creating catch-all modules.
- Keep DTO ↔ domain ↔ Room mapping functions close to their model types, as this repo already does (`toDto`, `toDomain`, `toEntity`).
- For media picked from Android `Uri`s, use `Context.toImageMediaUploadRequest(...)` / `toImageMediaUploadRequests(...)` from `core/media/domain/model/MediaInputResolver.kt` instead of hand-rolling content resolver logic; `BikeDetailScreen.kt` is the current example.
- For navigation arguments, prefer adding/updating `createRoute(...)` helpers on the `Screen` object (`AddBike`, `BikeDetail`, `BikeHistory`, `BikeComponent`) instead of hand-building routes inline.
- If you touch appointment booking, preserve the current store-location-scoped service catalog contract (`ServiceCatalog.lastUpdated`, `isFromCache`, `fetchErrorMessage`) because `AddAppointmentViewModel` renders those fields directly.
- Preserve the current route/event/state naming style (`<Feature>Route`, `<Feature>ViewModel`, `UiState`, sealed event/result types).

