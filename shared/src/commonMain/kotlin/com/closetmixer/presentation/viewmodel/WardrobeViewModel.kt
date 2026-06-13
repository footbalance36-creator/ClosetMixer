package com.closetmixer.presentation.viewmodel

import com.closetmixer.data.model.Article
import com.closetmixer.data.repository.ArticleRepository
import com.closetmixer.domain.model.ArticleCategory
import com.closetmixer.domain.usecase.AddArticleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WardrobeUiState(
    val articles: List<Article> = emptyList(),
    val selectedCategory: ArticleCategory? = null,
    val favorisOnly: Boolean = false,
    val selectedColor: String? = null,
    val availableColors: List<String> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class WardrobeViewModel(
    private val articleRepo: ArticleRepository,
    private val addArticleUseCase: AddArticleUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _uiState = MutableStateFlow(WardrobeUiState())
    val uiState: StateFlow<WardrobeUiState> = _uiState.asStateFlow()

    private var baseArticles: List<Article> = emptyList()
    private var searchJob: Job? = null

    init { loadArticles() }

    fun loadArticles(category: ArticleCategory? = null) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, selectedCategory = category, selectedColor = null, favorisOnly = false, searchQuery = "") }
            runCatching {
                if (category == null) articleRepo.getAllArticles()
                else articleRepo.getByCategory(category)
            }.onSuccess { all ->
                baseArticles = all
                val colors = extractColors(all)
                _uiState.update { it.copy(articles = all, availableColors = colors, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun search(query: String) {
        searchJob?.cancel()
        _uiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            _uiState.update { state ->
                state.copy(articles = applyFilters(baseArticles, state.favorisOnly, state.selectedColor))
            }
            return
        }
        searchJob = scope.launch {
            delay(300)
            runCatching { articleRepo.searchArticles(query) }.onSuccess { results ->
                _uiState.update { state ->
                    state.copy(articles = applyFilters(results, state.favorisOnly, state.selectedColor))
                }
            }
        }
    }

    fun toggleFavoris() {
        val newFavoris = !_uiState.value.favorisOnly
        _uiState.update { state ->
            state.copy(
                favorisOnly = newFavoris,
                articles = applyFilters(baseArticles, newFavoris, state.selectedColor)
            )
        }
    }

    fun filterByColor(color: String?) {
        val newColor = if (color == _uiState.value.selectedColor) null else color
        _uiState.update { state ->
            state.copy(
                selectedColor = newColor,
                articles = applyFilters(baseArticles, state.favorisOnly, newColor)
            )
        }
    }

    fun addArticle(
        id: String,
        photoPath: String,
        categorie: String,
        sousCategorie: String,
        couleur: String?,
        metal: String?,
        culture: String,
        onDone: () -> Unit
    ) {
        scope.launch {
            runCatching {
                addArticleUseCase.execute(
                    id = id,
                    photoPath = photoPath,
                    categorie = categorie,
                    sousCategorie = sousCategorie,
                    couleur = couleur,
                    metal = metal,
                    culture = culture
                )
            }.onSuccess {
                refreshBaseArticles()
                onDone()
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun toggleFavorite(articleId: String) {
        scope.launch { articleRepo.toggleFavorite(articleId); refreshBaseArticles() }
    }

    fun deleteArticle(articleId: String) {
        scope.launch { articleRepo.delete(articleId); refreshBaseArticles() }
    }

    private suspend fun refreshBaseArticles() {
        val category = _uiState.value.selectedCategory
        runCatching {
            if (category == null) articleRepo.getAllArticles()
            else articleRepo.getByCategory(category)
        }.onSuccess { all ->
            baseArticles = all
            val colors = extractColors(all)
            val filtered = applyFilters(all, _uiState.value.favorisOnly, _uiState.value.selectedColor)
            _uiState.update { it.copy(articles = filtered, availableColors = colors) }
        }
    }

    private fun applyFilters(articles: List<Article>, favorisOnly: Boolean, color: String?): List<Article> =
        articles
            .let { if (favorisOnly) it.filter { a -> a.isFavori == 1L } else it }
            .let { if (color != null) it.filter { a -> a.couleur?.lowercase()?.trim() == color } else it }

    private fun extractColors(articles: List<Article>): List<String> =
        articles.mapNotNull { it.couleur?.lowercase()?.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
}
