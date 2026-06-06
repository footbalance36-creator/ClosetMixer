# Android — ClosetMixer

## Module : `androidApp`
Tout ce qui est Android-spécifique : Activity, DI Android, utilitaires, ressources.

## Structure
```
androidApp/src/main/java/com/closetmixer/android/
├── ClosetMixerApp.kt        ← Application class, Koin init
├── MainActivity.kt          ← SplashScreen + setContent
├── di/
│   └── AndroidModule.kt     ← SqlDriver via DatabaseDriverFactory(context)
├── data/
│   └── DataSeeder.kt        ← Seed 12 articles si DB vide
├── util/
│   └── ImageUtils.kt        ← copyImageToInternalStorage()
└── ui/
    ├── navigation/
    │   └── AppNavigation.kt ← NavHost + BottomBar
    ├── screen/
    │   ├── WardrobeScreen.kt
    │   ├── AddArticleScreen.kt
    │   ├── OutfitScreen.kt
    │   ├── CalendarScreen.kt
    │   ├── StatsScreen.kt
    │   └── SettingsScreen.kt
    └── theme/
        └── ClosetMixerTheme.kt
```

## DI Android (Koin)
```kotlin
// AndroidModule.kt
val androidModule = module {
    single<SqlDriver> { DatabaseDriverFactory(androidContext()).createDriver() }
}

// ClosetMixerApp.kt
startKoin {
    androidContext(this@ClosetMixerApp)
    modules(androidModule, sharedModule)  // androidModule TOUJOURS en premier
}
```

## ⚠️ Pré-production — Avant toute modification Android

- Lire le fichier cible en entier avant d'éditer.
- Vérifier tous les imports après modification (inutilisés = erreur de compilation).
- Ne pas modifier `fetchAndLoadWeather`, `WeatherApi`, `WeatherDto` sans confirmer que le nom de champ API correspond exactement à ce que retourne Open-Meteo.
- Ne pas ajouter de complexité (scope, delay, fallback) dans un fichier fonctionnel sans besoin explicite.
- Relire l'état final complet du fichier avant de conclure.

## Règles critiques
- `koinInject()` dans tous les Composables (PAS `koinViewModel()`)
- Les ViewModels KMP sont `single{}` dans Koin, pas `factory{}`
- `DatabaseDriverFactory` réside dans `shared/androidMain` — accès au `Context` via Koin `androidContext()`
- Les imports Android (`android.*`, `androidx.*`) sont **interdits** dans `commonMain`

## Gestion des images
```kotlin
// util/ImageUtils.kt
fun copyImageToInternalStorage(context: Context, uri: Uri): String {
    val dir = File(context.filesDir, "articles").also { it.mkdirs() }
    val file = File(dir, "${UUID.randomUUID()}.jpg")
    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(file).use { output -> input.copyTo(output) }
    }
    return file.absolutePath
}
```
- Copier l'image immédiatement après sélection (le `Uri` scoped storage expire)
- Stocker uniquement le chemin absolu en DB

## Manifeste
- `android:windowSoftInputMode="adjustResize"` sur `MainActivity` — formulaires scrollables
- Permissions déclarées : INTERNET, CAMERA, READ_MEDIA_IMAGES, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION

## Dates
- Utiliser `java.time.LocalDate` (API 26+, minSdk = 26) — PAS `kotlinx-datetime` dans `androidApp`
- `LocalDate.now().monthValue`, `LocalDate.now().year`, etc.

## Compile/Target SDK
- `compileSdk = 35`, `targetSdk = 35`, `minSdk = 26`
- `compileOptions { sourceCompatibility = JavaVersion.VERSION_17 }`

## Problèmes connus
Voir `.claude/build.md` — section « Problèmes connus résolus ».
