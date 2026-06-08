# Cm-mob_006 — Correction du module Calendrier

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

## Note pour Claude Code

> Commence par la migration SQLDelight (le schéma est la source de vérité).
> Ensuite corrige le Repository, puis le ViewModel, puis l'UI.
> Ne touche pas aux autres écrans — uniquement le module Calendrier.
> Commit message : `feat(calendar): planifier une tenue complète avec miniatures (Cm-mob_006)`
