package com.closetmixer.presentation.viewmodel

import com.closetmixer.data.model.Article
import com.closetmixer.data.model.CalendarEntry
import com.closetmixer.data.model.Tenue
import com.closetmixer.data.repository.CalendarRepository
import com.closetmixer.data.repository.TenueRepository
import com.closetmixer.domain.usecase.GenerateOutfitUseCase
import com.closetmixer.domain.usecase.GeneratedOutfit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class CalendarUiState(
    val currentMonth: String = "",
    val monthEntries: Map<String, Pair<CalendarEntry, List<Article>>> = emptyMap(),
    val selectedDate: String? = null,
    val selectedEntry: Pair<CalendarEntry, List<Article>>? = null,
    val isLoading: Boolean = false,
    val showSheet: Boolean = false,
    val savedTenues: List<Pair<Tenue, List<Article>>> = emptyList(),
    val isSavedTenuesLoading: Boolean = false,
    val generatedOutfit: GeneratedOutfit? = null,
    val isGenerating: Boolean = false
)

class CalendarViewModel(
    private val calendarRepo: CalendarRepository,
    private val tenueRepo: TenueRepository,
    private val generateOutfitUseCase: GenerateOutfitUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        val now = Clock.System.todayIn(TimeZone.currentSystemDefault())
        loadMonth("${now.year}-${now.monthNumber.toString().padStart(2, '0')}")
    }

    fun loadMonth(yearMonth: String) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, currentMonth = yearMonth) }
            val rawEntries = calendarRepo.getMonthEntries(yearMonth)
            val enriched = mutableMapOf<String, Pair<CalendarEntry, List<Article>>>()
            rawEntries.forEach { entry ->
                val articles = entry.tenueId?.let { tenueRepo.getArticlesForTenue(it) } ?: emptyList()
                enriched[entry.date] = entry to articles
            }
            val currentSelected = _uiState.value.selectedDate
            val selectedEntry = currentSelected?.let { enriched[it] }
            _uiState.update { it.copy(monthEntries = enriched, selectedEntry = selectedEntry, isLoading = false) }
        }
    }

    fun prevMonth() = shiftMonth(-1)
    fun nextMonth() = shiftMonth(1)

    private fun shiftMonth(delta: Int) {
        val current = _uiState.value.currentMonth
        if (current.isEmpty()) return
        val parts = current.split("-")
        var year = parts[0].toInt()
        var month = parts[1].toInt() + delta
        if (month > 12) { month = 1; year++ }
        if (month < 1) { month = 12; year-- }
        loadMonth("$year-${month.toString().padStart(2, '0')}")
    }

    fun selectDate(date: String) {
        val entry = _uiState.value.monthEntries[date]
        _uiState.update { it.copy(selectedDate = date, selectedEntry = entry) }
    }

    fun openSheet(date: String) {
        _uiState.update { it.copy(selectedDate = date, showSheet = true, generatedOutfit = null) }
        loadSavedTenues()
    }

    fun closeSheet() {
        _uiState.update { it.copy(showSheet = false, generatedOutfit = null) }
    }

    private fun loadSavedTenues() {
        scope.launch {
            _uiState.update { it.copy(isSavedTenuesLoading = true) }
            val tenues = tenueRepo.getAllTenues()
            val enriched = tenues.map { tenue ->
                tenue to tenueRepo.getArticlesForTenue(tenue.id)
            }
            _uiState.update { it.copy(savedTenues = enriched, isSavedTenuesLoading = false) }
        }
    }

    fun assignTenue(date: String, tenueId: String) {
        scope.launch {
            calendarRepo.setTenueForDate(date, tenueId)
            _uiState.update { it.copy(showSheet = false, generatedOutfit = null) }
            loadMonth(_uiState.value.currentMonth)
        }
    }

    fun removeTenue(date: String) {
        scope.launch {
            calendarRepo.clearDate(date)
            _uiState.update { it.copy(selectedEntry = null) }
            loadMonth(_uiState.value.currentMonth)
        }
    }

    fun generateOutfit() {
        scope.launch {
            _uiState.update { it.copy(isGenerating = true, generatedOutfit = null) }
            val outfit = generateOutfitUseCase.generate()
            _uiState.update { it.copy(generatedOutfit = outfit, isGenerating = false) }
        }
    }

    fun useGeneratedOutfit(date: String) {
        val outfit = _uiState.value.generatedOutfit ?: return
        scope.launch {
            val tenueId = Clock.System.now().toEpochMilliseconds().toString()
            val tenue = Tenue(
                id = tenueId,
                nom = "Tenue générée",
                occasion = null,
                saison = null,
                isFavori = 0L,
                dateCreation = Clock.System.now().toEpochMilliseconds(),
                datePortee = null
            )
            tenueRepo.insert(tenue)
            listOfNotNull(outfit.haut, outfit.bas, outfit.chaussure, outfit.bijou).forEach {
                tenueRepo.addArticleToTenue(tenueId, it.id)
            }
            assignTenue(date, tenueId)
        }
    }
}
