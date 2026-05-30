# Architecture — ClosetMixer

## Paradigme
Kotlin Multiplatform (KMP) — logique partagée entre Android et iOS, UI native sur chaque plateforme.

## Modules
```
ClosetMixer/
├── shared/          ← 100% Kotlin multiplatform
│   ├── commonMain   ← data, domain, presentation (ViewModels, UseCases)
│   ├── androidMain  ← implémentations expect/actual Android (DB driver, DI)
│   └── iosMain      ← implémentations expect/actual iOS (DB driver, DI)
├── androidApp/      ← Jetpack Compose, Koin, Navigation
└── iosApp/          ← SwiftUI
```

## Couches (Clean Architecture)
```
data/
  remote/     → WeatherApi (Open-Meteo, sans clé API)
  db/         → SQLDelight (DatabaseDriverFactory expect/actual)
  model/      → Article, Tenue, Voyage, CalendarEntry
  repository/ → ArticleRepository, TenueRepository, WeatherRepository

domain/
  model/      → ArticleCategory, ArticleSubCategory, CulturalStyle, Metal, AppLanguage
  usecase/    → AddArticle, GetArticlesByCategory, GenerateOutfit, GetWeather,
                PlanOutfit, GetStats

presentation/
  viewmodel/  → WardrobeViewModel, OutfitViewModel, CalendarViewModel,
                VoyageViewModel, StatsViewModel, SettingsViewModel
```

## Règles fondamentales
- Aucun import Android dans `commonMain`
- Les `expect class` sont uniquement dans `commonMain/data/db/`
- Les ViewModels KMP n'héritent PAS d'AndroidX ViewModel — utiliser `koinInject()` (pas `koinViewModel()`)
- Chaque couche ne dépend que de la couche inférieure

## DI (Koin)
- `androidModule` (androidApp) → fournit `SqlDriver` avec `androidContext()`
- `iosModule` (iosMain) → fournit `SqlDriver` sans contexte
- `sharedModule` (commonMain) → tout le reste, ViewModels en `single { }`
- Ordre de chargement : `modules(androidModule, sharedModule)`
