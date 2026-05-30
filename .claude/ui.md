# UI — ClosetMixer

## Android : Jetpack Compose + Material3

### Thème
- `ClosetMixerTheme` dans `androidApp/ui/theme/`
- Material3 BOM 2024.10.00
- Couleurs : `MaterialTheme.colorScheme.*` — jamais de couleur hardcodée
- Typography : `MaterialTheme.typography.*`

### Navigation
- `NavHostController` + routes string dans `AppNavigation.kt`
- Bottom bar : 4 onglets (Garde-robe, Tenue, Calendrier, Stats)
- Bottom bar **masquée** sur `add_article` (écran modal)
- Pattern : `navController.navigate("route")` / `onNavigateBack: () -> Unit`

### Screens (un fichier par écran)
| Route | Fichier | Description |
|-------|---------|-------------|
| `wardrobe` | `WardrobeScreen.kt` | Liste articles + bouton FAB Ajouter |
| `add_article` | `AddArticleScreen.kt` | Formulaire ajout article |
| `outfit` | `OutfitScreen.kt` | Génération tenue |
| `calendar` | `CalendarScreen.kt` | Calendrier mensuel |
| `stats` | `StatsScreen.kt` | Statistiques garde-robe |
| `settings` | `SettingsScreen.kt` | Langue, préférences |

### Composants réutilisables
- `SectionLabel(text)` — label de section avec `labelLarge` + couleur `primary`
- `ArticleCard` — card article avec photo (`AsyncImage` Coil) + métadonnées
- `ColorSwatch` — cercle de couleur cliquable avec check icon

### Règles Compose
- Chaque screen reçoit son ViewModel via `koinInject()` (PAS `koinViewModel()`)
- États locaux : `remember { mutableStateOf(...) }`
- États partagés : `viewModel.xxx.collectAsState()`
- Pas de logique métier dans les Composables — uniquement UI + délégation au VM
- `Scaffold` avec `TopAppBar` sur chaque écran non-tab

### Formulaire AddArticle
- Photo : `Box` cliquable → `ActivityResultContracts.GetContent()` → Coil `AsyncImage`
- Dropdowns : `ExposedDropdownMenuBox` + `.menuAnchor(MenuAnchorType.PrimaryNotEditable)`
- Palette couleur : `FlowRow` de `Box` circulaires 36dp
- Luminance check icon : `red*0.299f + green*0.587f + blue*0.114f > 0.5f` → noir/blanc

### Images
- Chargées avec **Coil** (`AsyncImage`)
- `contentScale = ContentScale.Crop` pour les thumbnails
- Chemin stocké : chemin interne `filesDir/articles/`

## iOS : SwiftUI

### Structure
- `iosApp/iosApp/` — point d'entrée SwiftUI
- Le shared KMP framework est intégré via `iosApp.xcworkspace`
- ViewModels KMP utilisés directement depuis Swift (via `@StateObject` ou `ObservableObject` wrapper)

### Règles SwiftUI
- Pas de logique métier dans les Views
- Chaque View correspond à un screen Android
- Utiliser les ViewModels du module `shared` (pas de VMs Swift séparés)

## Icône & Splash

### Icône adaptative
- Foreground : `ic_launcher_foreground.png` (copie de `spec/closetMixer.png`)
- Background : couleur `#F5ECD7` (beige chaud)
- XML : `mipmap-anydpi-v26/ic_launcher.xml`

### Splash Screen
- Bibliothèque : `androidx.core:core-splashscreen:1.0.1`
- `installSplashScreen()` avant `super.onCreate()` dans `MainActivity`
- Thème : `Theme.ClosetMixer.SplashScreen` avec `windowSplashScreenAnimatedIcon`
- Fond : `#F5ECD7`
