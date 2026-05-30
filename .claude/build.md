# Build — ClosetMixer

## Versions clés
| Outil | Version |
|-------|---------|
| Kotlin Multiplatform | 2.0.21 |
| Android Gradle Plugin | 8.2.2 |
| Gradle | 8.9 |
| Java / JVM target | 17 |
| compileSdk / targetSdk | 35 |
| minSdk | 26 (Android 8.0) |
| iOS cible | iOS 16+ |

## Catalogue de versions
Toutes les dépendances sont centralisées dans `gradle/libs.versions.toml`.
Ne jamais déclarer une version en dur dans un `build.gradle.kts`.

## Commandes
```bash
# Shared KMP
./gradlew :shared:build

# Android debug APK
./gradlew :androidApp:assembleDebug

# Android install sur émulateur/device
./gradlew :androidApp:installDebug

# iOS framework (simulateur)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Ouvrir Xcode
open iosApp/iosApp.xcworkspace
```

## Tâche stub nécessaire
Le module `shared` déclare une tâche vide `testClasses` car Android Studio la cherche sur tout module :
```kotlin
// shared/build.gradle.kts
tasks.register("testClasses")
```

## SQLDelight
Le schéma est dans `shared/src/commonMain/sqldelight/com/closetmixer/db/ClosetDatabase.sq`.
SQLDelight génère le code Kotlin au build. Ne jamais modifier les fichiers générés dans `build/`.
Package généré : `com.closetmixer.db`

## Moko Resources
Les fichiers de traduction sont dans `shared/src/commonMain/resources/MR/`.
Package généré : `com.closetmixer`
8 langues : `base`(FR), `en`, `ar`, `ko`, `ja`, `tr`, `id`, `es`

## Problèmes connus résolus
- `testClasses` introuvable → tâche stub dans `shared/build.gradle.kts`
- `menuAnchor()` sans paramètre → Material3 1.3.x requiert `menuAnchor(MenuAnchorType.PrimaryNotEditable)`
- `Color.luminance()` inexistant → calculer manuellement : `red * 0.299f + green * 0.587f + blue * 0.114f`
- `kotlinx.datetime` dans `androidApp` → utiliser `java.time` (API 26+)
