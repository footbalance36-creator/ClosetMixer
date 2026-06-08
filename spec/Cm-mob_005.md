# Closet Mixer — Spec technique v005
> État réel du code au 07/06/2026 — branche `feature/Cm-mob_005`

---

## 1. Stack technique réelle

| Composant | Librairie | Remarque |
|-----------|-----------|----------|
| Langage partagé | Kotlin Multiplatform | shared module |
| UI Android | Jetpack Compose + Material 3 | Stitch design system |
| Base de données | SQLDelight 2.x | ClosetDatabase.sq |
| Réseau | Ktor Client | Open-Meteo (gratuit, sans clé) |
| DI | Koin 3.x KMP | sharedModule + androidModule |
| Réactivité | Coroutines + StateFlow | Pas d'AndroidX ViewModel |
| Datetime | kotlinx-datetime | utilisé dans CalendarViewModel |
| Navigation | Compose Navigation | 8 routes |

**Pas de :** moko-resources, Coil, OpenWeatherMap, AndroidX ViewModel, DataSeeder.

---

## 2. Structure réelle du projet

```
ClosetMixer/
├── shared/
│   └── src/commonMain/kotlin/com/closetmixer/
│       ├── data/
│       │   ├── db/DatabaseDriverFactory.kt
│       │   ├── model/
│       │   │   ├── Article.kt
│       │   │   ├── CalendarEntry.kt
│       │   │   ├── Tenue.kt
│       │   │   └── Voyage.kt
│       │   ├── remote/
│       │   │   ├── WeatherApi.kt          ← Open-Meteo
│       │   │   └── WeatherDto.kt
│       │   ├── repository/
│       │   │   ├── ArticleRepository.kt
│       │   │   ├── TenueRepository.kt
│       │   │   └── WeatherRepository.kt
│       │   └── storage/SettingsStorage.kt
│       ├── di/SharedModule.kt
│       ├── domain/
│       │   ├── model/Category.kt
│       │   └── usecase/
│       │       ├── AddArticleUseCase.kt
│       │       ├── GenerateOutfitUseCase.kt
│       │       ├── GetArticlesByCategoryUseCase.kt
│       │       ├── GetStatsUseCase.kt
│       │       ├── GetWeatherUseCase.kt
│       │       └── PlanOutfitUseCase.kt
│       └── presentation/viewmodel/
│           ├── CalendarViewModel.kt
│           ├── OutfitViewModel.kt
│           ├── SettingsViewModel.kt
│           ├── StatsViewModel.kt
│           ├── VoyageViewModel.kt
│           └── WardrobeViewModel.kt
│
├── androidApp/src/main/java/com/closetmixer/android/
│   ├── ClosetMixerApp.kt
│   ├── MainActivity.kt
│   ├── data/AndroidSettingsStorage.kt
│   ├── di/AndroidModule.kt
│   ├── ui/
│   │   ├── component/
│   │   │   ├── ArticleCard.kt
│   │   │   ├── BottomNavBar.kt
│   │   │   ├── CategoryChips.kt
│   │   │   ├── OutfitRow.kt
│   │   │   └── WeatherBanner.kt
│   │   ├── navigation/AppNavigation.kt
│   │   ├── screen/
│   │   │   ├── AddArticleScreen.kt
│   │   │   ├── CalendarScreen.kt
│   │   │   ├── OnboardingScreen.kt
│   │   │   ├── OutfitScreen.kt
│   │   │   ├── SettingsScreen.kt
│   │   │   ├── StatsScreen.kt
│   │   │   ├── VoyageScreen.kt
│   │   │   └── WardrobeScreen.kt
│   │   └── theme/
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   └── util/ImageUtils.kt
│
└── spec/
    ├── Cm-mob_001.md   ← prompt initial
    ├── Cm-mob_002.md   ← prompt Stitch UI
    └── Cm-mob_005.md   ← ce fichier
```

---

## 3. Navigation (AppNavigation.kt)

8 routes, barre de navigation inférieure sur 5 onglets :

| Route | Écran | Onglet nav |
|-------|-------|------------|
| `onboarding` | OnboardingScreen | non |
| `wardrobe` | WardrobeScreen | Garde-robe |
| `outfit` | OutfitScreen | Tenues |
| `calendar` | CalendarScreen | Calendrier |
| `voyage` | VoyageScreen | Voyage |
| `stats` | StatsScreen | Stats |
| `settings` | SettingsScreen | non |
| `add_article` | AddArticleScreen | non |

**Comportement :** onboarding affiché une seule fois (SharedPreferences `onboarding_done`). `NavigationBar` sans insets système (`WindowInsets(0)` + `navigationBarsPadding()`).

---

## 4. Design system — Stitch

- **Style :** luxe minimaliste
- **Couleurs :** Champagne Gold (`#C9A84C`), surface sombre, `MaterialTheme.colorScheme.primary` = gold
- **Typographie :** Playfair Display (titres) + Inter (corps)
- **Règle layout :** aucun `Scaffold` imbriqué dans les écrans — tous les écrans utilisent `Column(Modifier.fillMaxSize())` directement pour éviter le double padding status bar

---

## 5. Injection de dépendances (Koin)

### androidModule
```
SqlDriver       → DatabaseDriverFactory(androidContext()).createDriver()
SettingsStorage → AndroidSettingsStorage(androidContext())
```

### sharedModule
```
// Singles infrastructure
HttpClient, ClosetDatabase, WeatherApi
ArticleRepository, TenueRepository, WeatherRepository

// Use cases (factory)
AddArticleUseCase, GetArticlesByCategoryUseCase, GenerateOutfitUseCase
GetWeatherUseCase, PlanOutfitUseCase, GetStatsUseCase

// ViewModels (single)
WardrobeViewModel(get(), get())
OutfitViewModel(get(), get())
CalendarViewModel(get(), get(), get())   ← PlanOutfitUseCase, GetWeatherUseCase, GetArticlesByCategoryUseCase
VoyageViewModel(get())
StatsViewModel(get())
SettingsViewModel(get())
```

---

## 6. ViewModels — pattern réel

Pas d'AndroidX ViewModel. Pattern utilisé :

```kotlin
class XxxViewModel(...) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _uiState = MutableStateFlow(XxxUiState())
    val uiState: StateFlow<XxxUiState> = _uiState.asStateFlow()
}
```

---

## 7. Écrans — état fonctionnel

### WardrobeScreen
- Grille 2 colonnes d'articles avec photos
- Filtres par catégorie (chips)
- Bouton `+` → AddArticleScreen
- Swipe-to-delete sur chaque article
- Toggle favori

### AddArticleScreen
- Formulaire : catégorie, sous-catégorie, couleur, métal, culture
- Listes déroulantes (pas de boutons radio)
- Prise de photo ou sélection galerie
- Sauvegarde via `WardrobeViewModel.addArticle()`

### OutfitScreen
- Génération aléatoire de tenue depuis les articles de la garde-robe
- Météo via Open-Meteo (géolocalisation)
- Affichage météo + suggestion tenue adaptée
- **Limitation :** les tenues générées ne sont pas sauvegardées en base

### CalendarScreen
- Calendrier mensuel avec grille 7 colonnes
- Navigation mois précédent / suivant (`<` `>`)
- Indicateur point doré sur les jours planifiés
- Tap sur un jour → panel inférieur :
  - Si planifié : icône ✓ + bouton "Modifier"
  - Si vide : bouton "Planifier une article"
- `ModalBottomSheet` : liste des articles de la garde-robe à affecter au jour sélectionné
- L'ID article est stocké comme référence dans `CalendarEntry.tenueId`
- Résumé du mois : nombre de jours planifiés

### VoyageScreen
- Création et gestion de voyages
- Valise virtuelle : ajout d'articles à un voyage

### StatsScreen
- Répartition des articles par catégorie (barre de progression)
- KPI : total articles, articles jamais portés
- Total tenues (comptera 0 tant qu'aucune tenue n'est sauvegardée)

### SettingsScreen
- Photo de profil
- Paramètres langue, style culturel
- Bouton déconnexion (placeholder)

### OnboardingScreen
- Affiché une seule fois au premier lancement
- Enregistre `onboarding_done = true` dans SharedPreferences

---

## 8. Base de données SQLDelight

Fichier : `shared/src/commonMain/sqldelight/com/closetmixer/db/ClosetDatabase.sq`

### Tables
| Table | Description |
|-------|-------------|
| `Article` | Vêtements, chaussures, bijoux, accessoires |
| `Tenue` | Tenues composées (non utilisé activement) |
| `TenueArticle` | Relation Tenue ↔ Article |
| `CalendarEntry` | Planification jour → articleId (stocké dans `tenueId`) |
| `Voyage` | Voyages |
| `VoyageArticle` | Relation Voyage ↔ Article |

### Queries principales utilisées
```sql
getAllArticles, getArticlesByCategory, insertArticle,
updateArticleFavori, deleteArticle,
getCalendarEntry, getMonthEntries, insertCalendarEntry,
countArticlesByCategory, countTotalTenues, countNeverUsed,
getAllVoyages, insertVoyage
```

---

## 9. API Météo — Open-Meteo

```
GET https://api.open-meteo.com/v1/forecast
  ?latitude=...&longitude=...
  &current=temperature_2m,weathercode,relative_humidity_2m
  &timezone=auto
```

**Sans clé API.** Réponse mappée dans `WeatherDto` / `CurrentWeatherDto`.

---

## 10. Données de démonstration

**Aucune.** `DataSeeder.kt` supprimé. `ClosetMixerApp` démarre avec une base vide. Les données affichées dans l'appli viennent uniquement de ce que l'utilisateur ajoute.

---

## 11. CI/CD

- **Fastlane** configuré (branch `feature/Cm-mob_004`)
- **GitHub Actions** : déploiement automatique Play Store
- Keystore : `spec/closetmixer.jks`
- Service account : `spec/footbalance-fe5df-b58fce7dda8f.json`

---

## 12. Points à améliorer (connus)

| # | Problème | Impact |
|---|----------|--------|
| 1 | `OutfitScreen` ne sauvegarde pas les tenues générées | `StatsScreen.totalTenues` reste à 0 |
| 2 | `CalendarEntry.tenueId` stocke un `articleId` (détournement de champ) | Fonctionnel mais sémantique incorrecte |
| 3 | Pas de vue détail article dans la garde-robe | Pas d'édition possible |
