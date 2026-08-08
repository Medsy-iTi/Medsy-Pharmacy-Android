# AGENTS.md — Medsy Pharmacy Android

Read this file fully before writing or editing code. It is the source of truth for how the Medsy
Pharmacy Android project is structured. Do not introduce a new pattern, dependency, module,
result wrapper, shared abstraction, or product behavior without following the deviation protocol.

These rules apply to all new and modified code. Existing generated scaffold code is grandfathered
until it is replaced.

## 0. Golden rules

1. Stay inside the assigned feature. Shared changes must be minimal, necessary, and disclosed.
2. Respect module boundaries. Presentation depends on domain and design system, never data. Data
   depends on domain, never presentation. Domain stays pure Kotlin.
3. Ask before broad refactors, cross-feature changes, shared API redesigns, dependency changes,
   package moves, or migrations unless the user's request explicitly includes that work.
4. Use the design system for colors, typography, shapes, fonts, and reusable visual primitives.
   Hex colors are allowed only in design-token files.
5. Never hardcode app-authored user-facing text. Add English and Arabic string resources together.
6. Never commit, print, or log secrets. Developer-local configuration belongs in gitignored
   `local.properties` and only the minimum required value may be exposed through `BuildConfig`.
7. Never create or run tests, compile, build, sync, install, launch, or start an emulator unless
   the user's current request explicitly asks for that exact verification.
8. Use `MedsyResult`, `EmptyMedsyResult`, and `MedsyError` for cross-layer fallible operations.
9. Never log passwords, OTPs, tokens, prescriptions, addresses, request bodies, phone numbers, or
   pharmacy licensing and verification data.
10. Medsy Pharmacy is request-based. Do not invent a pharmacy inventory feed or storefront.
11. Do not perform unrelated cleanup, dependency upgrades, renames, or formatting sweeps.

## 1. Project overview

- Product: Medsy Pharmacy Android application.
- Application ID/package root: `com.medsy.pharmacy` / `com.medsy.*`.
- UI: Kotlin, Jetpack Compose, single `AppCompatActivity`.
- Architecture: Clean Architecture split by layer, with feature packages inside each layer.
- Presentation: lean MVI using `State`, `UIIntent`, and optional `UIEffect`.
- Navigation: AndroidX Navigation 3, owned by `:app`, using serializable `NavKey` routes.
- Dependency injection: Hilt with KSP.
- Networking: Retrofit, Moshi, and OkHttp in `:data`.
- Theme: Medsy light/dark schemes and tokens from `:designsystem`.
- Localization: English and Arabic; all UI must be RTL-safe.

Current foundation routes and screens may be placeholders. Their existence does not authorize
inventing backend contracts or production behavior.

## 2. Modules and dependency direction

| Module | Responsibility | May depend on | Must not contain |
|---|---|---|---|
| `:app` | Application, activity, Navigation 3, manifest, top-level composition | presentation, data, domain, designsystem | Feature business logic, DTO mapping |
| `:presentation` | Screens, ViewModels, MVI contracts, feature-local UI | domain, designsystem | Retrofit types, repositories, app routes |
| `:domain` | Models, repository interfaces, use cases, business rules | Kotlin/JDK only | Android, Compose, Retrofit, Moshi, resources |
| `:data` | APIs, DTOs, data sources, repository implementations, mappers, DI | domain | Composables, ViewModels, navigation |
| `:designsystem` | Theme, tokens, fonts, feature-agnostic reusable UI | Compose/Android UI | Feature state, use cases, data types |

Required runtime direction:

```text
Composable → ViewModel → UseCase → Repository interface → Repository implementation → DataSource → ApiService
```

- ViewModels depend on use cases, not repositories.
- Use cases depend on domain repository interfaces.
- Only data sources call Retrofit or storage frameworks.
- DTOs and Retrofit responses never leave `:data`.
- Do not create a `core` module or feature Gradle modules without approval.

## 3. Feature structure

Use the same lowercase feature name across modules:

```text
presentation/<feature>/
  XScreen.kt
  XViewModel.kt
  XState.kt
  XUIIntent.kt
  XUIEffect.kt        # only when needed
  components/

domain/<feature>/
  model/
  repository/
  usecase/

data/<feature>/
  remote/
  mapper/
  repository/
  di/
```

Keep small features flat. Do not create empty classes or directories just to match a template.
Feature-specific components stay with the feature. Only feature-agnostic components with genuine
reuse belong in `:designsystem`. Presentation features must not import one another.

## 4. Change isolation and approval

A feature task may touch its feature packages, the minimum app navigation registration, its string
resources, and a necessary shared integration point. Before an unrequested large or shared change:

1. Stop.
2. Name the affected features/modules and the reason.
3. Explain blast radius and compatibility.
4. Offer the smallest compliant alternative.
5. Wait for approval.

When a requested change explicitly includes multiple modules or shared setup, keep the work limited
to that approved scope. Prefer additive shared APIs and inspect all consumers before changing one.

## 5. MVI contract

- Use `XRoot`, `XScreen`, `XViewModel`, `XState`, `XUIIntent`, and optional `XUIEffect`.
- `XRoot` integrates the ViewModel and navigation callbacks.
- `XScreen` renders state, emits intents, and remains previewable without Hilt.
- ViewModels use `@HiltViewModel`, constructor injection, private mutable state, and one exhaustive
  `onIntent(intent)` entry point.
- Use buffered `Channel` plus `receiveAsFlow()` only for one-off effects.
- Never store `Context`, composables, navigation stacks, DTOs, Retrofit types, or resolved strings
  in a ViewModel.
- Child composables receive data and callbacks, never a ViewModel.
- Real data screens explicitly model loading, success, empty, recoverable error, and retry.

## 6. Navigation 3

- `:app` owns routes, root/nested back stacks, entry providers, transitions, and bottom navigation.
- Route keys implement `NavKey` and are `@Serializable`.
- Pass stable identifiers and small arguments, never full domain objects.
- `:presentation` exposes callbacks and must not import `com.medsy.pharmacy.nav`.
- Use the shared navigation-duration constant and make the smallest route registration edit.

## 7. Networking and results

- Application-wide clients live in `:data`; ordinary features do not create Retrofit singletons.
- Base URLs and environment values come from generated configuration, never feature source.
- HTTP logging is debug-only and authentication/cookie headers remain redacted.
- Every Medsy endpoint uses the shared `ApiResponse<T>` envelope.
- Use the shared safe REST helpers for generic HTTP, timeout, I/O, serialization, empty-body, and
  unknown failures. Always rethrow `CancellationException`.
- Do not add `ApiResult`, `DomainResult`, Kotlin `Result`, exception-based normal flow, or
  feature-specific copies of the shared envelope.
- Repositories map DTO success values to domain models and propagate typed errors unchanged.
- Raw backend messages, exceptions, and `Throwable` never enter presentation state or UI.
- DTO names end in `Dto`; explicit mappers use `toDomain()` and `toDto()`.

## 8. Design system and accessibility

- Use `MedsyTheme`, `MaterialTheme.colorScheme`, `MaterialTheme.typography`, shared shapes, and
  `MaterialTheme.extendedColors`.
- Dynamic Material You colors remain disabled unless a brand change is approved.
- Feature code must not use color literals, framework colors, or local palettes.
- Use start/end rather than left/right and verify directional icons in RTL.
- Meaningful images/icons require localized content descriptions; decorative visuals use `null`.
- Respect touch targets, scalable text, contrast, and never communicate status by color alone.
- Search existing components before creating another. Shared components accept neutral UI data and
  callbacks, never ViewModels or repositories.

## 9. Strings, Arabic, and RTL

- Every label, title, message, dialog, snackbar, validation message, accessibility description, and
  representative preview string comes from the resource set of the module that renders it.
- Add English and Arabic translations in the same change.
- Prefix names by feature, use placeholders/plurals, and do not concatenate translated sentences.
- Use Western digits and Egyptian Pound formatting where required.
- Server content may remain raw; app-authored labels may not.

## 10. Pharmacy product and safety invariants

- Only approved pharmacies are eligible to receive new nearby requests.
- Paused pharmacies do not receive new requests; existing orders remain manageable.
- Requested quantity and Medsy fixed unit price are read-only.
- Pharmacists enter actual available quantity; partial offers are valid.
- An alternative may be proposed only when the patient allowed alternatives for that item and some
  requested quantity is unavailable. AI never proposes alternatives.
- One optional offer-wide percentage discount applies uniformly to supplied original and alternative
  lines and must stay within the backend-configured maximum.
- Preparation time and delivery fee are explicit; backend calculations are authoritative.
- Sent offers are immutable. Request/phase validity and idempotency are server-enforced.
- Pharmacies do not manage combined-offer abstractions and see only their own child order.
- Approval, receiving, request, offer, and order state transitions are backend-authoritative.
- Prescriptions, delivery addresses, contact data, location, licenses, and verification documents
  are sensitive and visible only when authorized.
- API contracts must be frozen before implementing real pharmacy endpoints or DTOs.
- AI never diagnoses, prescribes, authors dosage, bypasses review, or creates an order.

## 11. Hilt and configuration

- Keep `@HiltAndroidApp` on the application and `@AndroidEntryPoint` on `MainActivity`.
- ViewModels use `@HiltViewModel`; repositories bind with `@Binds`; framework builders use
  `@Provides`.
- Feature bindings live under that feature's data DI package.
- Shared clients may be `@Singleton`; feature UI state may not.
- Dependencies and versions belong in `gradle/libs.versions.toml`.
- Developer-local values belong in gitignored `local.properties`.
- Expose only the minimum required generated field. Never provide a real secret fallback.
- Privileged secrets remain on the backend because client applications cannot protect them.

## 12. Naming

- Packages lowercase; types/composables PascalCase; functions/properties camelCase; resources
  lower_snake_case.
- Repositories use `XRepository` / `XRepositoryImpl`.
- Use cases use `VerbNounUseCase` with `operator fun invoke`.
- Keep files focused and avoid generic base ViewModels, reducers, or MVI frameworks.

## 13. Deviation protocol

If a requirement cannot be implemented within these rules:

1. Stop before deviating.
2. Cite the blocking rule.
3. Explain the affected modules/features.
4. Propose the smallest deviation and a compliant alternative.
5. Wait for explicit approval.
6. Update this file if approval establishes a reusable convention.

Security, privacy, medical safety, feature isolation, and fixed-price rules are hard stops.

## 14. Verification

- Do not create, edit, generate, or run tests unless explicitly requested.
- Do not compile, assemble, build, sync, install, launch, or run an emulator unless explicitly
  requested.
- Verify unrequested work only by static inspection: imports, types, dependency direction, resource
  names, exhaustive branches, manifests, configuration, and obvious syntax.
- State at handoff that tests/build/runtime verification were not executed because this file
  requires explicit permission.

## 15. Definition of Done

- Work stayed within approved features and shared integration points.
- Module direction and UI-to-data call chain are respected.
- No DTO, Retrofit, Android, or Compose types leaked across boundaries.
- Fallible contracts use `MedsyResult` and `MedsyError`.
- MVI contracts are lean and exhaustive; no new `TODO()` remains.
- UI uses only design-system tokens and supports light/dark themes.
- English and Arabic resources exist for every app-authored string and layouts are RTL-safe.
- No credentials or sensitive data were committed or logged.
- Fixed-price, discount, alternatives, approval, privacy, and AI invariants are preserved.
- No unrelated cleanup was included.
- No tests or Gradle/runtime verification ran without explicit user permission.
