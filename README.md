# ClosetMixer

Application Android & iOS de gestion de garde-robe et de génération de tenues.  
Construite avec **Kotlin Multiplatform (KMP)**.

## Stack technique

| Composant | Technologie |
|-----------|-------------|
| Langage partagé | Kotlin Multiplatform 2.0.x |
| UI Android | Jetpack Compose + Material 3 |
| UI iOS | SwiftUI (iOS 16+) |
| Base de données | SQLDelight 2.x |
| Réseau | Ktor Client 2.x |
| DI | Koin 3.x |
| Réactivité | Coroutines + StateFlow |
| Météo | Open-Meteo (gratuit, sans clé API) |
| i18n | Moko-resources (8 langues) |

## Fonctionnalités

- Garde-robe : catalogue de vêtements par catégorie
- Génération de tenues : algorithme basé sur la météo et le style culturel
- Calendrier : planification des tenues par jour
- Voyage : liste de packing par voyage
- Statistiques : articles les plus/moins portés
- 8 langues : FR, EN, AR, KO, JA, TR, ID, ES
- Styles culturels : Modest, K-Fashion, J-Fashion, Traditionnel, Neutre

## Instructions de build

### Prérequis

- Android Studio Hedgehog ou supérieur
- JDK 17
- Xcode 15+ (pour iOS)

### Android

```bash
./gradlew :shared:build
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug
```

### iOS

```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
open iosApp/iosApp.xcworkspace
```

## API Météo

Ce projet utilise **Open-Meteo** — API météo entièrement gratuite, sans inscription ni clé API.  
Documentation : https://open-meteo.com

## Structure du projet

```
ClosetMixer/
├── shared/          ← Code KMP partagé (data, domain, presentation)
├── androidApp/      ← Application Android (Jetpack Compose)
└── iosApp/          ← Application iOS (SwiftUI)
```

## Screenshots

<!-- TODO: ajouter screenshots -->

---

*Développé avec Kotlin Multiplatform*
