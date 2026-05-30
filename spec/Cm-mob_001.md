# Closet Mixer — Prompt complet pour Claude Code

## Objectif
Tu es un expert Kotlin Multiplatform (KMP). Crée un projet complet **Closet Mixer** : une application Android & iOS de gestion de garde-robe et de génération de tenues. Tu vas créer le repo GitHub, la structure du projet, le code source, et t'assurer que la compilation fonctionne sur Android et iOS.

---

## 1. Initialisation du repo GitHub

```bash
# Créer et initialiser le repo
git init closet-mixer
cd closet-mixer
gh repo create closet-mixer --public --description "Closet Mixer - Wardrobe & Outfit Generator App (KMP)" --push --source=.
```

Crée un `.gitignore` adapté à KMP (Android Studio, Xcode, Gradle, Kotlin).
Crée un `README.md` avec description, screenshots placeholder, et instructions de build.

---

## 2. Stack technique exacte

| Composant | Librairie | Version |
|-----------|-----------|---------|
| Langage partagé | Kotlin Multiplatform | 2.0.x |
| UI Android | Jetpack Compose + Material 3 | latest stable |
| UI iOS | SwiftUI | iOS 16+ |
| Base de données | SQLDelight | 2.x |
| Réseau | Ktor Client | 2.x |
| DI | Koin | 3.x (KMP) |
| Réactivité | Kotlin Coroutines + StateFlow | latest |
| Images | Coil (Android) + SDWebImage (iOS) | latest |
| i18n | Moko-resources | latest |
| Datetime | Kotlinx-datetime | latest |
| Sérialisation | Kotlinx-serialization | latest |
| Navigation Android | Compose Navigation | latest |

---

## 3. Structure du projet

```
closet-mixer/
├── shared/
│   ├── src/
│   │   ├── commonMain/kotlin/com/closetmixer/
│   │   │   ├── data/
│   │   │   │   ├── db/
│   │   │   │   │   ├── ClosetDatabase.sq       ← SQLDelight schema
│   │   │   │   │   └── DatabaseDriverFactory.kt
│   │   │   │   ├── model/
│   │   │   │   │   ├── Article.kt
│   │   │   │   │   ├── Tenue.kt
│   │   │   │   │   ├── Voyage.kt
│   │   │   │   │   └── CalendarEntry.kt
│   │   │   │   ├── repository/
│   │   │   │   │   ├── ArticleRepository.kt
│   │   │   │   │   ├── TenueRepository.kt
│   │   │   │   │   └── WeatherRepository.kt
│   │   │   │   └── remote/
│   │   │   │       ├── WeatherApi.kt
│   │   │   │       └── WeatherDto.kt
│   │   │   ├── domain/
│   │   │   │   ├── usecase/
│   │   │   │   │   ├── AddArticleUseCase.kt
│   │   │   │   │   ├── GetArticlesByCategoryUseCase.kt
│   │   │   │   │   ├── GenerateOutfitUseCase.kt
│   │   │   │   │   ├── GetWeatherUseCase.kt
│   │   │   │   │   ├── PlanOutfitUseCase.kt
│   │   │   │   │   └── GetStatsUseCase.kt
│   │   │   │   └── model/
│   │   │   │       ├── Category.kt
│   │   │   │       └── AppLanguage.kt
│   │   │   └── presentation/
│   │   │       └── viewmodel/
│   │   │           ├── WardrobeViewModel.kt
│   │   │           ├── OutfitViewModel.kt
│   │   │           ├── CalendarViewModel.kt
│   │   │           ├── VoyageViewModel.kt
│   │   │           ├── StatsViewModel.kt
│   │   │           └── SettingsViewModel.kt
│   │   ├── androidMain/
│   │   └── iosMain/
├── androidApp/
│   └── src/main/
│       ├── java/com/closetmixer/android/
│       │   ├── MainActivity.kt
│       │   └── ui/
│       │       ├── theme/
│       │       │   ├── Theme.kt
│       │       │   ├── Color.kt
│       │       │   └── Type.kt
│       │       ├── screen/
│       │       │   ├── WardrobeScreen.kt
│       │       │   ├── OutfitScreen.kt
│       │       │   ├── CalendarScreen.kt
│       │       │   ├── VoyageScreen.kt
│       │       │   ├── StatsScreen.kt
│       │       │   └── SettingsScreen.kt
│       │       ├── component/
│       │       │   ├── ArticleCard.kt
│       │       │   ├── OutfitRow.kt
│       │       │   ├── WeatherBanner.kt
│       │       │   ├── CategoryChips.kt
│       │       │   └── BottomNavBar.kt
│       │       └── navigation/
│       │           └── AppNavigation.kt
│       └── res/
│           ├── drawable/
│           └── values/
└── iosApp/
    └── iosApp/
        ├── ContentView.swift
        ├── Views/
        │   ├── WardrobeView.swift
        │   ├── OutfitView.swift
        │   ├── CalendarView.swift
        │   ├── VoyageView.swift
        │   ├── StatsView.swift
        │   └── SettingsView.swift
        └── Components/
            ├── ArticleCard.swift
            └── WeatherBanner.swift
```

---

## 4. Schéma SQLDelight complet

Crée `shared/src/commonMain/sqldelight/com/closetmixer/db/ClosetDatabase.sq` :

```sql
CREATE TABLE Article (
    id TEXT PRIMARY KEY NOT NULL,
    photoPath TEXT NOT NULL,
    categorie TEXT NOT NULL,
    sousCategorie TEXT NOT NULL,
    couleur TEXT,
    metal TEXT,
    tags TEXT NOT NULL DEFAULT '[]',
    culture TEXT NOT NULL DEFAULT 'neutral',
    dateAjout INTEGER NOT NULL,
    nbUtilisations INTEGER NOT NULL DEFAULT 0,
    isFavori INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE Tenue (
    id TEXT PRIMARY KEY NOT NULL,
    nom TEXT NOT NULL,
    occasion TEXT,
    saison TEXT,
    isFavori INTEGER NOT NULL DEFAULT 0,
    dateCreation INTEGER NOT NULL,
    datePortee INTEGER
);

CREATE TABLE TenueArticle (
    tenueId TEXT NOT NULL,
    articleId TEXT NOT NULL,
    FOREIGN KEY (tenueId) REFERENCES Tenue(id) ON DELETE CASCADE,
    FOREIGN KEY (articleId) REFERENCES Article(id) ON DELETE CASCADE,
    PRIMARY KEY (tenueId, articleId)
);

CREATE TABLE CalendarEntry (
    date TEXT PRIMARY KEY NOT NULL,
    tenueId TEXT NOT NULL,
    meteo TEXT,
    temperature REAL,
    FOREIGN KEY (tenueId) REFERENCES Tenue(id) ON DELETE CASCADE
);

CREATE TABLE Voyage (
    id TEXT PRIMARY KEY NOT NULL,
    nom TEXT NOT NULL,
    destination TEXT,
    dateDebut TEXT,
    dateFin TEXT,
    dateCreation INTEGER NOT NULL
);

CREATE TABLE VoyageArticle (
    voyageId TEXT NOT NULL,
    articleId TEXT NOT NULL,
    FOREIGN KEY (voyageId) REFERENCES Voyage(id) ON DELETE CASCADE,
    FOREIGN KEY (articleId) REFERENCES Article(id) ON DELETE CASCADE,
    PRIMARY KEY (voyageId, articleId)
);

-- Queries Articles
getAllArticles:
SELECT * FROM Article ORDER BY dateAjout DESC;

getArticlesByCategory:
SELECT * FROM Article WHERE categorie = ? ORDER BY dateAjout DESC;

getArticlesBySubCategory:
SELECT * FROM Article WHERE sousCategorie = ? ORDER BY dateAjout DESC;

getFavoriteArticles:
SELECT * FROM Article WHERE isFavori = 1;

getMostUsedArticles:
SELECT * FROM Article ORDER BY nbUtilisations DESC LIMIT 10;

getNeverUsedArticles:
SELECT * FROM Article WHERE nbUtilisations = 0;

insertArticle:
INSERT INTO Article(id, photoPath, categorie, sousCategorie, couleur, metal, tags, culture, dateAjout, nbUtilisations, isFavori)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0);

updateArticleFavori:
UPDATE Article SET isFavori = ? WHERE id = ?;

incrementUsage:
UPDATE Article SET nbUtilisations = nbUtilisations + 1 WHERE id = ?;

deleteArticle:
DELETE FROM Article WHERE id = ?;

-- Queries Tenues
getAllTenues:
SELECT * FROM Tenue ORDER BY dateCreation DESC;

getFavoriteTenues:
SELECT * FROM Tenue WHERE isFavori = 1;

getArticlesForTenue:
SELECT a.* FROM Article a
INNER JOIN TenueArticle ta ON a.id = ta.articleId
WHERE ta.tenueId = ?;

insertTenue:
INSERT INTO Tenue(id, nom, occasion, saison, isFavori, dateCreation)
VALUES (?, ?, ?, ?, 0, ?);

updateTenueFavori:
UPDATE Tenue SET isFavori = ? WHERE id = ?;

-- Queries Calendrier
getCalendarEntry:
SELECT * FROM CalendarEntry WHERE date = ?;

getMonthEntries:
SELECT * FROM CalendarEntry WHERE date LIKE ? ORDER BY date;

insertCalendarEntry:
INSERT OR REPLACE INTO CalendarEntry(date, tenueId, meteo, temperature)
VALUES (?, ?, ?, ?);

-- Queries Stats
countArticlesByCategory:
SELECT categorie, COUNT(*) as count FROM Article GROUP BY categorie;

countTotalTenues:
SELECT COUNT(*) FROM Tenue;

countNeverUsed:
SELECT COUNT(*) FROM Article WHERE nbUtilisations = 0;
```

---

## 5. Modèles de données Kotlin

```kotlin
// shared/src/commonMain/kotlin/com/closetmixer/domain/model/Category.kt

enum class ArticleCategory(val key: String) {
    VETEMENT("vetement"),
    CHAUSSURE("chaussure"),
    BIJOU("bijou"),
    MAQUILLAGE("maquillage"),
    COUVRE_CHEF("couvre_chef"),
    ACCESSOIRE("accessoire"),
    SKINCARE("skincare")
}

enum class ArticleSubCategory(val category: ArticleCategory, val key: String) {
    // Vêtements
    HAUT(ArticleCategory.VETEMENT, "haut"),
    BAS(ArticleCategory.VETEMENT, "bas"),
    ROBE(ArticleCategory.VETEMENT, "robe"),
    VESTE(ArticleCategory.VETEMENT, "veste"),
    MANTEAU(ArticleCategory.VETEMENT, "manteau"),
    ABAYA(ArticleCategory.VETEMENT, "abaya"),
    KAFTAN(ArticleCategory.VETEMENT, "kaftan"),
    KIMONO(ArticleCategory.VETEMENT, "kimono"),
    HANBOK(ArticleCategory.VETEMENT, "hanbok"),
    SARI(ArticleCategory.VETEMENT, "sari"),

    // Chaussures
    TALON(ArticleCategory.CHAUSSURE, "talon"),
    BASKET(ArticleCategory.CHAUSSURE, "basket"),
    SANDALE(ArticleCategory.CHAUSSURE, "sandale"),
    BOTTE(ArticleCategory.CHAUSSURE, "botte"),
    BABOUCHE(ArticleCategory.CHAUSSURE, "babouche"),

    // Bijoux
    COLLIER(ArticleCategory.BIJOU, "collier"),
    BAGUE(ArticleCategory.BIJOU, "bague"),
    BRACELET(ArticleCategory.BIJOU, "bracelet"),
    BOUCLE_OREILLE(ArticleCategory.BIJOU, "boucle_oreille"),
    MONTRE(ArticleCategory.BIJOU, "montre"),

    // Maquillage
    ROUGE_LEVRES(ArticleCategory.MAQUILLAGE, "rouge_levres"),
    FOND_DE_TEINT(ArticleCategory.MAQUILLAGE, "fond_de_teint"),
    PALETTE_YEUX(ArticleCategory.MAQUILLAGE, "palette_yeux"),
    MASCARA(ArticleCategory.MAQUILLAGE, "mascara"),
    PARFUM(ArticleCategory.MAQUILLAGE, "parfum"),

    // Couvre-chefs
    HIJAB(ArticleCategory.COUVRE_CHEF, "hijab"),
    TURBAN(ArticleCategory.COUVRE_CHEF, "turban"),
    VOILE(ArticleCategory.COUVRE_CHEF, "voile"),
    CHAPEAU(ArticleCategory.COUVRE_CHEF, "chapeau"),
    CASQUETTE(ArticleCategory.COUVRE_CHEF, "casquette"),
    BONNET(ArticleCategory.COUVRE_CHEF, "bonnet"),
    KANZASHI(ArticleCategory.COUVRE_CHEF, "kanzashi"),

    // Accessoires
    SAC(ArticleCategory.ACCESSOIRE, "sac"),
    CEINTURE(ArticleCategory.ACCESSOIRE, "ceinture"),
    LUNETTES(ArticleCategory.ACCESSOIRE, "lunettes"),
    ECHARPE(ArticleCategory.ACCESSOIRE, "echarpe"),
    EVENTAIL(ArticleCategory.ACCESSOIRE, "eventail"),

    // Skincare (K-beauty / J-beauty)
    SERUM(ArticleCategory.SKINCARE, "serum"),
    CREME(ArticleCategory.SKINCARE, "creme"),
    CUSHION(ArticleCategory.SKINCARE, "cushion"),
    MASQUE(ArticleCategory.SKINCARE, "masque")
}

enum class Metal(val key: String) {
    OR("or"), ARGENT("argent"), ROSE("rose"), FANTAISIE("fantaisie"), AUCUN("aucun")
}

enum class AppLanguage(val code: String, val isRTL: Boolean, val nativeName: String) {
    FRENCH("fr", false, "Français"),
    ENGLISH("en", false, "English"),
    ARABIC("ar", true, "العربية"),
    TURKISH("tr", false, "Türkçe"),
    INDONESIAN("id", false, "Indonesia"),
    SPANISH("es", false, "Español"),
    KOREAN("ko", false, "한국어"),
    JAPANESE("ja", false, "日本語")
}

enum class CulturalStyle(val key: String) {
    MODEST("modest"),       // Hijab, Abaya, mode modeste
    K_FASHION("k_fashion"), // Style coréen
    J_FASHION("j_fashion"), // Style japonais, Harajuku
    TRADITIONAL("traditional"), // Kaftan, Sari, Hanbok
    NEUTRAL("neutral")
}
```

---

## 6. ViewModels partagés

```kotlin
// shared/src/commonMain/kotlin/com/closetmixer/presentation/viewmodel/WardrobeViewModel.kt

class WardrobeViewModel(
    private val articleRepo: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WardrobeUiState())
    val uiState: StateFlow<WardrobeUiState> = _uiState.asStateFlow()

    init { loadArticles() }

    fun loadArticles(category: ArticleCategory? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val articles = if (category == null)
                articleRepo.getAllArticles()
            else
                articleRepo.getByCategory(category)
            _uiState.update { it.copy(articles = articles, isLoading = false) }
        }
    }

    fun toggleFavorite(articleId: String) {
        viewModelScope.launch { articleRepo.toggleFavorite(articleId) }
    }

    fun deleteArticle(articleId: String) {
        viewModelScope.launch { articleRepo.delete(articleId) }
    }
}

data class WardrobeUiState(
    val articles: List<Article> = emptyList(),
    val selectedCategory: ArticleCategory? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

---

## 7. UI Android — écrans Jetpack Compose

### WardrobeScreen.kt
```kotlin
@Composable
fun WardrobeScreen(viewModel: WardrobeViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val categories = ArticleCategory.entries

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.wardrobe_title)) },
                actions = {
                    IconButton(onClick = { /* navigate to add */ }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            // Category chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.selectedCategory == null,
                        onClick = { viewModel.loadArticles(null) },
                        label = { Text(stringResource(R.string.all)) }
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = state.selectedCategory == cat,
                        onClick = { viewModel.loadArticles(cat) },
                        label = { Text(cat.key) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            // Grid of articles
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.articles) { article ->
                    ArticleCard(
                        article = article,
                        onFavoriteClick = { viewModel.toggleFavorite(article.id) }
                    )
                }
            }
        }
    }
}
```

---

## 8. API Météo

```kotlin
// shared/src/commonMain/kotlin/com/closetmixer/data/remote/WeatherApi.kt

@Serializable
data class WeatherDto(
    val main: MainDto,
    val weather: List<WeatherDescDto>,
    val name: String
)

@Serializable
data class MainDto(val temp: Double, val humidity: Int)

@Serializable
data class WeatherDescDto(val description: String, val icon: String)

class WeatherApi(private val client: HttpClient) {
    suspend fun getWeather(lat: Double, lon: Double, lang: String = "fr"): WeatherDto {
        return client.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("lang", lang)
            parameter("units", "metric")
            parameter("appid", BuildConfig.WEATHER_API_KEY)
        }.body()
    }
}
```

---

## 9. Injection de dépendances Koin

```kotlin
// shared/src/commonMain/kotlin/com/closetmixer/di/SharedModule.kt

val sharedModule = module {
    single { DatabaseDriverFactory(get()) }
    single { createDatabase(get()) }
    single { ArticleRepository(get()) }
    single { TenueRepository(get()) }
    single { WeatherRepository(get()) }
    single { HttpClient(/* engine */) { install(ContentNegotiation) { json() } } }
    viewModel { WardrobeViewModel(get()) }
    viewModel { OutfitViewModel(get(), get()) }
    viewModel { CalendarViewModel(get(), get()) }
    viewModel { VoyageViewModel(get()) }
    viewModel { StatsViewModel(get()) }
    viewModel { SettingsViewModel() }
}
```

---

## 10. Génération de tenues (algorithme)

```kotlin
// shared/src/commonMain/kotlin/com/closetmixer/domain/usecase/GenerateOutfitUseCase.kt

class GenerateOutfitUseCase(private val articleRepo: ArticleRepository) {

    suspend fun generate(
        weather: Weather? = null,
        culturalStyle: CulturalStyle = CulturalStyle.NEUTRAL,
        occasion: String? = null
    ): GeneratedOutfit {
        val allArticles = articleRepo.getAllArticles()

        val haut = allArticles.filter { it.categorie == ArticleCategory.VETEMENT.key
            && it.sousCategorie in listOf("haut","robe","abaya","kimono","hanbok") }.randomOrNull()

        val bas = if (haut?.sousCategorie == "robe" || haut?.sousCategorie == "abaya") null
            else allArticles.filter { it.sousCategorie == "bas" }.randomOrNull()

        val chaussure = allArticles.filter { it.categorie == ArticleCategory.CHAUSSURE.key }
            .let { list ->
                if (weather != null && weather.temperature < 10)
                    list.filter { it.sousCategorie in listOf("botte","basket") }.randomOrNull()
                        ?: list.randomOrNull()
                else list.randomOrNull()
            }

        val bijou = allArticles.filter { it.categorie == ArticleCategory.BIJOU.key }.randomOrNull()
        val maquillage = allArticles.filter { it.categorie == ArticleCategory.MAQUILLAGE.key }.randomOrNull()
        val couvreChef = if (culturalStyle == CulturalStyle.MODEST)
            allArticles.filter { it.categorie == ArticleCategory.COUVRE_CHEF.key }.randomOrNull()
            else null

        return GeneratedOutfit(
            haut = haut,
            bas = bas,
            chaussure = chaussure,
            bijou = bijou,
            maquillage = maquillage,
            couvreChef = couvreChef
        )
    }
}

data class GeneratedOutfit(
    val haut: Article?,
    val bas: Article?,
    val chaussure: Article?,
    val bijou: Article?,
    val maquillage: Article?,
    val couvreChef: Article?
)
```

---

## 11. Gradle build files

### settings.gradle.kts
```kotlin
rootProject.name = "ClosetMixer"
include(":shared", ":androidApp")
pluginManagement {
    repositories {
        google(); mavenCentral(); gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories { google(); mavenCentral() }
}
```

### shared/build.gradle.kts
```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget { compilations.all { kotlinOptions { jvmTarget = "17" } } }
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework { baseName = "shared"; isStatic = true }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.android.driver)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}
```

---

## 12. Permissions Android

Dans `androidApp/src/main/AndroidManifest.xml` :
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

---

## 13. Fichiers de traduction (moko-resources)

Crée les fichiers strings pour chaque langue :
- `MR/base/strings.xml` (Français — défaut)
- `MR/en/strings.xml`
- `MR/ar/strings.xml` (avec direction RTL)
- `MR/ko/strings.xml`
- `MR/ja/strings.xml`
- `MR/tr/strings.xml`
- `MR/id/strings.xml`
- `MR/es/strings.xml`

Clés minimales à inclure :
`app_name, wardrobe, outfits, calendar, voyage, stats, settings, add_article, generate_outfit, favorites, all_categories, premium_title, language, cultural_style`

---

## 14. Instructions de build

Après avoir créé tous les fichiers :

```bash
# Vérifier la configuration KMP
./gradlew :shared:build

# Build Android debug
./gradlew :androidApp:assembleDebug

# Build iOS framework
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Lancer sur émulateur Android
./gradlew :androidApp:installDebug

# Ouvrir Xcode pour iOS
open iosApp/iosApp.xcworkspace
```

---

## 15. Variables d'environnement

Crée un fichier `local.properties` (non commité) :
```properties
WEATHER_API_KEY=ta_clé_openweathermap
```

Et référence-le dans `androidApp/build.gradle.kts` via `BuildConfig`.

---

## 16. Checklist finale Claude Code

- [ ] Repo GitHub créé et pushé
- [ ] Projet KMP compilable sans erreur sur Android
- [ ] Framework iOS généré sans erreur
- [ ] SQLDelight génère les queries
- [ ] Koin injecte tous les ViewModels
- [ ] Navigation Compose fonctionnelle entre les 6 écrans
- [ ] 8 langues configurées dans moko-resources
- [ ] API météo fonctionnelle (mock si pas de clé)
- [ ] README.md avec instructions de build complètes
- [ ] `.gitignore` adapté KMP

---

## Note finale pour Claude Code

> Commence par créer la structure Gradle et vérifier que `./gradlew :shared:build` passe sans erreur. Ensuite implémente les écrans un par un dans l'ordre : WardrobeScreen → OutfitScreen → CalendarScreen → VoyageScreen → StatsScreen → SettingsScreen. Pour chaque écran, crée d'abord le ViewModel partagé, puis l'UI Android Compose, puis l'UI iOS SwiftUI. Commit après chaque écran fonctionnel.
