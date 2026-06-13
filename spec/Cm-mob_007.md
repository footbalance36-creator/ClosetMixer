# Cm-mob_006 — Corrections & Nouvelles fonctionnalités

## Contexte
Le MOA Cm-mob_005 contient une ambiguïté sur le calendrier (E-04 / F-07).
Le terme "article" doit être remplacé partout par "tenue complète".
Voici la spécification corrigée à implémenter.

---

## Correction F-07

**AVANT (incorrect) :**
> F-07 — Planifier un article sur un jour du calendrier

**APRÈS (correct) :**
> F-07 — Planifier une tenue complète (haut + bas + chaussures + bijou/accessoire)
> sur un jour du calendrier, avec affichage en miniatures au tap sur le jour.

---

## Correction E-04 — Calendrier (spec complète corrigée)

### Vue mensuelle
- Grille 7 colonnes (Dim→Sam), navigation < > entre les mois
- **Point doré** sous les jours où une tenue est planifiée
- **Fond or pâle** sur le jour sélectionné
- **Couleur primaire** pour aujourd'hui

### Tap sur un jour — Panel inférieur

**Cas 1 — Aucune tenue planifiée :**
```
[ icône calendrier vide ]
"Aucune tenue pour ce jour"
[ Bouton pleine largeur : "Planifier une tenue" ]
```

**Cas 2 — Une tenue est planifiée :**
```
"Lundi 9 juin"
[ Miniature haut ] [ Miniature bas ] [ Miniature chaussure ] [ Miniature bijou ]
   sous-catégorie     sous-catégorie     sous-catégorie       sous-catégorie
[ Bouton "Modifier" ]   [ Bouton "Retirer" ]
```
- Miniatures : carrés 64dp, ratio 3:4, coins 8dp, photo réelle de l'article
- Sous-catégorie en label 10sp sous chaque miniature
- Si un slot est vide (ex: pas de bijou), afficher carré gris avec icône "+"

### ModalBottomSheet — Sélecteur de tenue
Ouvert au tap sur "Planifier une tenue" ou "Modifier".

**Structure du sheet :**
1. Titre : "Choisir une tenue pour le [date]"
2. **Onglet 1 — Tenues sauvegardées** : liste des tenues favorites/créées
   - Chaque item : miniatures des articles (row horizontale) + nom de la tenue
   - Tap → affecte la tenue au jour → ferme le sheet
3. **Onglet 2 — Générer une tenue** :
   - Bouton "Générer selon la météo du jour"
   - Affiche la tenue générée avec ses miniatures
   - Bouton "Régénérer" + Bouton "Utiliser cette tenue"

---

## Modèle de données — correction SQLDelight

```sql
-- AVANT (incorrect) :
CREATE TABLE CalendarEntry (
    date TEXT PRIMARY KEY NOT NULL,
    articleId TEXT NOT NULL,   -- ❌ un seul article
    ...
);

-- APRÈS (correct) :
CREATE TABLE CalendarEntry (
    date TEXT PRIMARY KEY NOT NULL,
    tenueId TEXT,              -- ✅ référence une tenue complète
    meteo TEXT,
    temperature REAL,
    FOREIGN KEY (tenueId) REFERENCES Tenue(id) ON DELETE SET NULL
);
```

Si `tenueId` est NULL → jour sans tenue planifiée.
Si la tenue référencée est supprimée → SET NULL (le jour reste mais sans tenue).

---

## ViewModel — correction CalendarViewModel

```kotlin
class CalendarViewModel(
    private val calendarRepo: CalendarRepository,
    private val tenueRepo: TenueRepository,
    private val outfitUseCase: GenerateOutfitUseCase,
    private val weatherRepo: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    // Charger les entrées du mois affiché
    fun loadMonth(year: Int, month: Int) {
        viewModelScope.launch {
            val entries = calendarRepo.getMonthEntries(year, month)
            // Pour chaque entrée, charger les articles de la tenue
            val enriched = entries.map { entry ->
                val articles = entry.tenueId?.let { tenueRepo.getArticlesForTenue(it) } ?: emptyList()
                entry to articles
            }
            _uiState.update { it.copy(monthEntries = enriched) }
        }
    }

    // Sélectionner un jour
    fun selectDay(date: LocalDate) {
        val entry = _uiState.value.monthEntries.find { it.first.date == date.toString() }
        _uiState.update { it.copy(selectedDate = date, selectedEntry = entry) }
    }

    // Affecter une tenue à un jour
    fun assignTenue(date: LocalDate, tenueId: String) {
        viewModelScope.launch {
            calendarRepo.setTenueForDate(date.toString(), tenueId)
            loadMonth(date.year, date.monthNumber)
        }
    }

    // Retirer la tenue d'un jour
    fun removeTenue(date: LocalDate) {
        viewModelScope.launch {
            calendarRepo.clearDate(date.toString())
            loadMonth(date.year, date.monthNumber)
        }
    }

    // Générer une tenue pour le jour sélectionné
    fun generateForDate(date: LocalDate, style: CulturalStyle) {
        viewModelScope.launch {
            val weather = weatherRepo.getCurrent()
            val outfit = outfitUseCase.generate(weather, style)
            // Sauvegarder la tenue générée puis l'affecter
            val tenueId = tenueRepo.saveTenue(outfit)
            assignTenue(date, tenueId)
        }
    }
}

data class CalendarUiState(
    val currentYear: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
    val currentMonth: Int = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).monthNumber,
    val monthEntries: List<Pair<CalendarEntry, List<Article>>> = emptyList(),
    val selectedDate: LocalDate? = null,
    val selectedEntry: Pair<CalendarEntry, List<Article>>? = null,
    val isLoading: Boolean = false
)
```

---

## UI Compose — CalendarScreen corrigé (extraits clés)

```kotlin
// Miniatures de la tenue planifiée
@Composable
fun PlannedOutfitMiniatures(articles: List<Article>) {
    val slots = listOf("Haut", "Bas", "Chaussure", "Bijou")
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        slots.forEachIndexed { index, label ->
            val article = articles.getOrNull(index)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    if (article != null) {
                        AsyncImage(
                            model = article.photoPath,
                            contentDescription = article.sousCategorie,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                Text(
                    text = article?.sousCategorie ?: label,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Panel jour sélectionné
@Composable
fun SelectedDayPanel(
    date: LocalDate,
    entry: Pair<CalendarEntry, List<Article>>?,
    onPlanClick: () -> Unit,
    onModifyClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = date.format(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (entry?.first?.tenueId != null) {
                PlannedOutfitMiniatures(articles = entry.second)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onModifyClick, modifier = Modifier.weight(1f)) {
                        Text("Modifier")
                    }
                    OutlinedButton(
                        onClick = onRemoveClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Retirer")
                    }
                }
            } else {
                Button(onClick = onPlanClick, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Planifier une tenue")
                }
            }
        }
    }
}
```

---

## Checklist d'implémentation pour Claude Code

- [ ] Modifier `CalendarEntry` dans SQLDelight : `articleId` → `tenueId` (TEXT, nullable, FK Tenue)
- [ ] Ajouter les queries SQLDelight : `setTenueForDate`, `clearDate`, `getMonthWithTenues`
- [ ] Corriger `CalendarRepository` en conséquence
- [ ] Corriger `CalendarViewModel` avec le nouveau `CalendarUiState`
- [ ] Implémenter `PlannedOutfitMiniatures` (4 slots, photos réelles)
- [ ] Implémenter `SelectedDayPanel` (2 cas : vide / tenue planifiée)
- [ ] Implémenter `ModalBottomSheet` avec 2 onglets (tenues sauvegardées / générer)
- [ ] Mettre à jour le MOA : remplacer "article" par "tenue" dans F-07 et E-04
- [ ] Versionner en Cm-mob_006

---

## Note pour Claude Code — Calendrier

> Commence par la migration SQLDelight (le schéma est la source de vérité).
> Ensuite corrige le Repository, puis le ViewModel, puis l'UI.
> Ne touche pas aux autres écrans — uniquement le module Calendrier.
> Commit message : `feat(calendar): planifier une tenue complète avec miniatures (Cm-mob_006)`

---

---

# Nouvelles fonctionnalités — Roadmap V1 → V3

## Déjà livré (Cm-mob_005)
- ✅ Filtre couleur dans la garde-robe
- ✅ Filtre favoris dans la garde-robe
- ✅ Paramètres : changement de langue
- ✅ Paramètres : noter l'application (rate app)

---

## 🔴 Priorité V1 — À implémenter maintenant

### F-22 — Notification tenue du jour
**Impact rétention : ⭐⭐⭐⭐⭐ — Effort : Faible**

Une notification locale à heure configurable (défaut 7h30) affichant la tenue planifiée du jour. Zéro backend, 100% local via `WorkManager`.

**Comportement :**
- Si une tenue est planifiée ce jour → notification avec miniature de la tenue + texte "Votre tenue du jour est prête ✨"
- Si aucune tenue planifiée → notification avec texte "Vous n'avez pas de tenue prévue aujourd'hui — Générer une tenue ?"
- Tap sur la notification → ouvre l'app sur l'écran Calendrier au jour courant
- Configurable dans Paramètres : activer/désactiver + choisir l'heure

**Implémentation Android :**
```kotlin
// WorkManager — tâche quotidienne récurrente
val dailyWork = PeriodicWorkRequestBuilder<OutfitReminderWorker>(
    repeatInterval = 1,
    repeatIntervalTimeUnit = TimeUnit.DAYS
)
.setInitialDelay(calculateDelayUntil(hour = 7, minute = 30), TimeUnit.MILLISECONDS)
.setConstraints(Constraints.Builder().build())
.build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "outfit_daily_reminder",
    ExistingPeriodicWorkPolicy.UPDATE,
    dailyWork
)
```

**Permissions AndroidManifest :**
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

**Paramètres à ajouter dans SettingsViewModel :**
```kotlin
data class NotificationSettings(
    val enabled: Boolean = true,
    val hour: Int = 7,
    val minute: Int = 30
)
```

---

### F-23 — Recadrage photo 3:4 à l'ajout
**Impact rétention : ⭐⭐⭐⭐ — Effort : Faible**

Forcer un recadrage au ratio 3:4 au moment de l'ajout d'un article garantit une grille parfaitement homogène (standard des apps de mode premium).

**Librairie recommandée :** `ucrop` (Yalantis) — légère, KMP-compatible côté Android

```kotlin
// Lancement du crop après sélection galerie
UCrop.of(sourceUri, destinationUri)
    .withAspectRatio(3f, 4f)
    .withMaxResultSize(600, 800)
    .withOptions(UCrop.Options().apply {
        setCompressionQuality(85)
        setHideBottomControls(false)
        setFreeStyleCropEnabled(false)
    })
    .start(context as Activity)
```

---

### F-24 — Recherche textuelle dans la garde-robe
**Impact rétention : ⭐⭐⭐⭐ — Effort : Faible**

Barre de recherche sur sous-catégorie, couleur et tags. Indispensable au-delà de 50 articles.

**Query SQLDelight à ajouter :**
```sql
searchArticles:
SELECT * FROM Article
WHERE lower(sousCategorie) LIKE '%' || lower(:query) || '%'
   OR lower(couleur) LIKE '%' || lower(:query) || '%'
   OR lower(tags) LIKE '%' || lower(:query) || '%'
ORDER BY dateAjout DESC;
```

**UI :** `SearchBar` Material 3 en haut de l'écran Garde-robe, avec debounce 300ms sur la saisie.

---

### F-25 — Vue semaine dans le calendrier
**Impact rétention : ⭐⭐⭐⭐ — Effort : Moyen**

Basculer entre vue mensuelle et vue semaine via un toggle en haut du calendrier.

**Vue semaine :**
- 7 colonnes horizontales scrollables (lundi → dimanche)
- Chaque colonne : jour + miniature de la tenue planifiée (photo du haut)
- Tap → ouvre le panel du jour
- Idéal pour planifier la semaine de travail le dimanche soir

```kotlin
enum class CalendarViewMode { MONTH, WEEK }

// Ajouter dans CalendarUiState
val viewMode: CalendarViewMode = CalendarViewMode.MONTH
```

---

## 🟡 Priorité V2 — Prochaine itération

### F-26 — Tags libres sur les articles
**Impact : ⭐⭐⭐ — Effort : Moyen**

Permettre à l'utilisateur d'ajouter ses propres tags (`été`, `mariage`, `confort`, `bureau`).

- Champ texte libre dans le formulaire d'ajout avec suggestions basées sur les tags existants
- Chips cliquables dans la garde-robe pour filtrer par tag
- Stocké en JSON dans la colonne `tags` existante (déjà dans le schéma ✅)

---

### F-27 — Palette couleur intelligente dans la génération
**Impact : ⭐⭐⭐⭐⭐ — Effort : Moyen**

Remplacer la génération aléatoire par une logique de complémentarité des couleurs — le vrai différenciateur vs la concurrence.

**Règles de base à implémenter :**
```kotlin
object ColorHarmony {
    // Éviter deux pièces à motifs ensemble
    // Éviter rouge + orange, rose + rouge
    // Couleurs neutres (noir, blanc, beige) = compatibles avec tout
    val neutrals = listOf("noir", "blanc", "beige", "crème", "gris")
    val warmTones = listOf("rouge", "orange", "jaune", "corail")
    val coldTones = listOf("bleu", "vert", "violet", "turquoise")

    fun areCompatible(color1: String, color2: String): Boolean {
        if (color1 in neutrals || color2 in neutrals) return true
        if (color1 in warmTones && color2 in coldTones) return true
        if (color1 == color2) return true // monochrome
        return false
    }
}
```

---

### F-28 — Export / sauvegarde locale
**Impact : ⭐⭐⭐ — Effort : Moyen**

Bouton "Exporter ma garde-robe" dans Paramètres → génère un fichier `closetmixer_backup_[date].zip` dans le dossier Téléchargements contenant :
- `articles.json` — tous les articles avec métadonnées
- `tenues.json` — toutes les tenues
- `photos/` — toutes les photos originales

Permet migration future vers compte cloud sans perte de données.

---

### F-29 — Statistiques enrichies
**Impact : ⭐⭐⭐ — Effort : Moyen**

Ajouter aux stats existantes :
- **Prix d'achat optionnel** par article → calcul du coût estimé au port (`prix / nbUtilisations`)
- **Tenue la plus portée ce mois**
- **Score d'utilisation** : % d'articles portés au moins 1× (ex: "Vous utilisez 68% de votre garde-robe 🎉")
- **Couleur dominante** de la garde-robe (graphique camembert)

```sql
-- Nouvelle colonne à ajouter
ALTER TABLE Article ADD COLUMN prixAchat REAL;

-- Query coût au port
getCoutAuPort:
SELECT id, sousCategorie, prixAchat,
       CASE WHEN nbUtilisations > 0 THEN prixAchat / nbUtilisations ELSE prixAchat END as coutParPort
FROM Article
WHERE prixAchat IS NOT NULL
ORDER BY coutParPort ASC;
```

---

## 🟢 Priorité V3 — Vision long terme

### F-30 — Widget écran d'accueil Android
**Impact : ⭐⭐⭐⭐ — Effort : Élevé**

Widget 2×2 ou 4×2 via **Glance API** (Jetpack) affichant la tenue du jour directement sur l'écran d'accueil Android. Visibilité maximale, rétention maximale.

---

### F-31 — Alerte météo intelligente
**Impact : ⭐⭐⭐ — Effort : Élevé**

Via `WorkManager` la nuit précédente : si la météo prévue est incompatible avec la tenue planifiée (ex: pluie + sandales, froid + robe légère), envoyer une alerte :
> *"Il pleuvra demain — votre tenue du 10 juin n'est peut-être pas adaptée 🌧️"*

---

## Tableau récapitulatif

| ID | Fonctionnalité | Version | Impact | Effort |
|----|---------------|---------|--------|--------|
| F-22 | Notification tenue du jour | V1 🔴 | ⭐⭐⭐⭐⭐ | Faible |
| F-23 | Recadrage photo 3:4 | V1 🔴 | ⭐⭐⭐⭐ | Faible |
| F-24 | Recherche textuelle | V1 🔴 | ⭐⭐⭐⭐ | Faible |
| F-25 | Vue semaine calendrier | V1 🟠 | ⭐⭐⭐⭐ | Moyen |
| F-26 | Tags libres | V2 🟡 | ⭐⭐⭐ | Moyen |
| F-27 | Couleurs intelligentes | V2 🟡 | ⭐⭐⭐⭐⭐ | Moyen |
| F-28 | Export ZIP sauvegarde | V2 🟡 | ⭐⭐⭐ | Moyen |
| F-29 | Stats enrichies + prix | V2 🟡 | ⭐⭐⭐ | Moyen |
| F-30 | Widget écran d'accueil | V3 🟢 | ⭐⭐⭐⭐ | Élevé |
| F-31 | Alerte météo intelligente | V3 🟢 | ⭐⭐⭐ | Élevé |

---

## Note pour Claude Code — Nouvelles fonctionnalités

> Implémenter dans cet ordre strict :
> 1. F-22 (notification) — modifier `SettingsViewModel` + créer `OutfitReminderWorker`
> 2. F-23 (recadrage) — modifier `AddArticleScreen` uniquement
> 3. F-24 (recherche) — ajouter query SQLDelight + `SearchBar` dans `WardrobeScreen`
> 4. F-25 (vue semaine) — modifier `CalendarScreen` avec toggle vue
>
> Commit par fonctionnalité. Ne pas regrouper en un seul commit.
> Versionner l'ensemble en `Cm-mob_007`.
