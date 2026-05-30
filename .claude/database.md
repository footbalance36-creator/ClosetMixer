# Database — ClosetMixer

## Technologie
SQLDelight 2.x — génère du Kotlin typé à partir de fichiers `.sq`.
Schéma : `shared/src/commonMain/sqldelight/com/closetmixer/db/ClosetDatabase.sq`
Package généré : `com.closetmixer.db`

## Tables

### Article
```sql
CREATE TABLE Article (
    id TEXT NOT NULL PRIMARY KEY,
    photo_path TEXT NOT NULL DEFAULT '',
    categorie TEXT NOT NULL,
    sous_categorie TEXT NOT NULL,
    couleur TEXT,
    metal TEXT,
    culture TEXT NOT NULL DEFAULT 'NEUTRAL',
    created_at INTEGER NOT NULL DEFAULT 0
);
```
- `id` : UUID généré côté app (`UUID.randomUUID().toString()`)
- `photo_path` : chemin absolu dans `filesDir/articles/`
- `couleur` : nom de la couleur (ex. "Bleu marine"), nullable
- `metal` : uniquement pour `categorie = BIJOU`, sinon null
- `created_at` : timestamp Unix (ms)

### Tenue
```sql
CREATE TABLE Tenue (
    id TEXT NOT NULL PRIMARY KEY,
    nom TEXT NOT NULL,
    created_at INTEGER NOT NULL DEFAULT 0
);
```

### TenueArticle (relation N-N)
```sql
CREATE TABLE TenueArticle (
    tenue_id TEXT NOT NULL,
    article_id TEXT NOT NULL,
    PRIMARY KEY (tenue_id, article_id)
);
```

### CalendarEntry
```sql
CREATE TABLE CalendarEntry (
    id TEXT NOT NULL PRIMARY KEY,
    date TEXT NOT NULL,
    tenue_id TEXT NOT NULL
);
```
- `date` : format ISO 8601 `YYYY-MM-DD`

### Voyage
```sql
CREATE TABLE Voyage (
    id TEXT NOT NULL PRIMARY KEY,
    destination TEXT NOT NULL,
    date_depart TEXT NOT NULL,
    date_retour TEXT NOT NULL,
    created_at INTEGER NOT NULL DEFAULT 0
);
```

### VoyageArticle (relation N-N)
```sql
CREATE TABLE VoyageArticle (
    voyage_id TEXT NOT NULL,
    article_id TEXT NOT NULL,
    PRIMARY KEY (voyage_id, article_id)
);
```

## Règles SQLDelight
- Ne jamais modifier les fichiers dans `build/` — générés automatiquement
- Toute nouvelle requête s'écrit dans le `.sq`, pas en Kotlin
- Nommer les requêtes en camelCase : `selectAll`, `insertArticle`, `deleteById`
- Le schéma est la source de vérité — les migrations seront dans `migrations/`

## Driver (expect/actual)
| Plateforme | Implémentation |
|-----------|---------------|
| Android | `AndroidSqliteDriver(ClosetDatabase.Schema, context, "closetmixer.db")` |
| iOS | `NativeSqliteDriver(ClosetDatabase.Schema, "closetmixer.db")` |

## Repositories
- `ArticleRepository` → CRUD sur `Article`
- `TenueRepository` → CRUD sur `Tenue` + `TenueArticle`
- `WeatherRepository` → pas de DB, appelle `WeatherApi`
- Pas d'ORM — les repositories mappent directement les types SQLDelight générés

## DataSeeder (Android uniquement)
`androidApp/.../data/DataSeeder.kt` — insère 12 articles d'exemple si la DB est vide.
Appelé dans `ClosetMixerApp.onCreate()` après `startKoin`.
