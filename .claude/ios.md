# iOS — ClosetMixer

## Cible
- iOS 16+ (SwiftUI)
- Architectures : `iosX64` (simulateur Intel), `iosArm64` (device), `iosSimulatorArm64` (simulateur Apple Silicon)

## Structure
```
iosApp/
├── iosApp.xcworkspace   ← Ouvrir avec Xcode (pas .xcodeproj)
└── iosApp/
    ├── iOSApp.swift     ← Point d'entrée SwiftUI (@main)
    ├── ContentView.swift
    └── ...              ← Vues SwiftUI à implémenter
```

## Framework KMP partagé
Le module `shared` est compilé en framework XCFramework via Gradle.

### Build simulateur (Apple Silicon)
```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

### Build device
```bash
./gradlew :shared:linkDebugFrameworkIosArm64
```

### Ouvrir dans Xcode
```bash
open iosApp/iosApp.xcworkspace
```

## DI Koin (iOS)
```kotlin
// shared/src/iosMain/kotlin/com/closetmixer/ios/IosModule.kt
val iosModule = module {
    single<SqlDriver> { DatabaseDriverFactory().createDriver() }
}
```
- Initialiser Koin depuis Swift : `KoinHelper` ou `doInitKoin()` appelé dans `iOSApp.swift`
- Ordre : `modules(iosModule, sharedModule)`

## DatabaseDriverFactory (iOS actual)
```kotlin
// shared/src/iosMain/kotlin/com/closetmixer/ios/DatabaseDriverFactory.kt
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(ClosetDatabase.Schema, "closetmixer.db")
}
```

## Utiliser les ViewModels KMP depuis Swift
Les ViewModels KMP sont des classes Kotlin ordinaires (pas des `ObservableObject` Swift).
Options :
1. **Wrapper ObservableObject** : créer une classe Swift qui enveloppe le VM KMP et republie les `StateFlow` via `Combine`
2. **SKIE** (Touchlab) : génère automatiquement des wrappers Swift pour les `StateFlow`

## Images (iOS)
- `FileManager.default.urls(for: .documentDirectory)` pour stocker les photos
- `UIImagePickerController` ou `PhotosUI.PHPickerViewController` pour la sélection

## Règles
- Ne pas dupliquer la logique métier en Swift — tout est dans `shared`
- Les ViewModels, UseCases, Repositories → `shared/commonMain`
- Swift ne gère que l'UI et le cycle de vie

## Status actuel
Les vues SwiftUI ne sont pas encore implémentées (squelette initial uniquement).
Priorité : Android d'abord, iOS après que la logique `shared` soit stable.
