# Naming Conventions — ClosetMixer

## Kotlin (commonMain / androidMain / iosMain)

| Élément | Convention | Exemple |
|---------|-----------|---------|
| Classe / Object / Enum | PascalCase | `ArticleRepository`, `CulturalStyle` |
| Interface | PascalCase | `ArticleRepository` |
| Fonction | camelCase | `addArticle()`, `generateOutfit()` |
| Variable / Propriété | camelCase | `selectedCategory`, `photoPath` |
| Constante (`val` top-level) | SCREAMING_SNAKE | `COLOR_PALETTE` |
| Package | lowercase, points | `com.closetmixer.domain.model` |
| Fichier | PascalCase = nom classe principale | `WardrobeViewModel.kt` |

## Enums
- Nom enum : PascalCase singulier → `ArticleCategory`, `Metal`
- Valeurs enum : SCREAMING_SNAKE → `VETEMENT`, `BIJOU`, `AUCUN`
- Propriété `key` sur chaque enum = libellé affiché à l'utilisateur (FR)

## ViewModels
- Suffixe `ViewModel` → `WardrobeViewModel`, `OutfitViewModel`
- Propriétés d'état : `StateFlow<List<Article>>` nommées par contenu → `articles`
- UseCase injecté : suffixe `UseCase` → `addArticleUseCase`, `generateOutfitUseCase`
- Fonctions : verbe + objet → `addArticle()`, `savePlannedOutfit()`, `refreshArticles()`

## UseCases
- Suffixe `UseCase` → `AddArticleUseCase`, `GenerateOutfitUseCase`
- Une seule méthode publique : `invoke()` ou `execute()`

## Repositories
- Suffixe `Repository` → `ArticleRepository`, `WeatherRepository`
- Fonctions CRUD : `insert`, `getAll`, `getById`, `delete`

## SQLDelight (`.sq`)
- Fichier = nom table (PascalCase) : `Article.sq`, `Tenue.sq`
- Requêtes nommées : camelCase → `selectAll`, `selectByCategory`, `insertArticle`
- Colonnes : snake_case → `photo_path`, `sous_categorie`, `created_at`

## Compose (Android)
- Composables : PascalCase → `AddArticleScreen`, `ArticleCard`, `SectionLabel`
- Paramètres lambda : `on` + verbe → `onNavigateBack`, `onAddClick`, `onArticleClick`
- Préfixe `remember` pour états locaux → `rememberScrollState()`

## Ressources Android
- Layouts/drawables : snake_case → `ic_launcher_foreground`, `ic_launcher_background`
- Strings : snake_case → `app_name`, `add_article`
- Couleurs : snake_case → `splash_background`

## Routes de navigation
- Chaînes snake_case → `"wardrobe"`, `"add_article"`, `"outfit"`, `"calendar"`

## Fichiers `.sq` — convention de requête
```sql
-- Nom : verbe + Entité (camelCase)
selectAllArticles:
SELECT * FROM Article ORDER BY created_at DESC;

insertArticle:
INSERT INTO Article VALUES (?, ?, ?, ?, ?, ?, ?, ?);
```
