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

## ⚠️ Règles qualité — Pré-production (OBLIGATOIRES)

> Le projet est en phase pré-production. Une régression bloque la livraison.

### Avant de commencer toute tâche
1. **Lire en entier** chaque fichier à modifier — jamais de modification sans lecture préalable complète.
2. **Identifier les dépendances** — quels Screens, ViewModels, Repos, DI utilisent ce code ?
3. **Tracer le flux complet** — UI → ViewModel → UseCase → Repository → API/DB → retour UI.
4. **Lister les risques** — qu'est-ce qui peut casser si ce fichier change ?
5. **Périmètre strict** — ne toucher que les fichiers nécessaires à la tâche demandée.

### Avant de déclarer "c'est fait"
- Relire chaque fichier modifié dans son **état final complet** (pas seulement le diff).
- Vérifier que tous les imports sont utilisés et qu'il n'en manque aucun.
- **Ne jamais changer un nom de champ API** sans confirmer ce que l'API retourne réellement — si l'ancien code fonctionnait, le conserver.
- En cas de doute sur un changement, **revenir à la version originale fonctionnelle**.

### Interdictions
- Ne pas ajouter de complexité non demandée (fallback, abstraction, refactoring) sans validation explicite.
- Ne pas modifier du code fonctionnel adjacent dans le même commit qu'une nouvelle fonctionnalité.
- Ne pas introduire de changement dans un fichier hors périmètre de la tâche.

---

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
