package com.closetmixer.presentation.viewmodel

import com.closetmixer.data.model.Article
import com.closetmixer.data.repository.ArticleRepository
import com.closetmixer.domain.model.ArticleCategory
import com.closetmixer.domain.usecase.AddArticleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WardrobeUiState(
    val articles: List<Article> = emptyList(),
    val selectedCategory: ArticleCategory? = null,
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

    init { loadArticles() }

    fun loadArticles(category: ArticleCategory? = null) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, selectedCategory = category) }
            runCatching {
                if (category == null) articleRepo.getAllArticles()
                else articleRepo.getByCategory(category)
            }.onSuccess { articles ->
                _uiState.update { it.copy(articles = articles, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
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
                loadArticles(_uiState.value.selectedCategory)
                onDone()
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun toggleFavorite(articleId: String) {
        scope.launch { articleRepo.toggleFavorite(articleId); loadArticles(_uiState.value.selectedCategory) }
    }

    fun deleteArticle(articleId: String) {
        scope.launch { articleRepo.delete(articleId); loadArticles(_uiState.value.selectedCategory) }
    }
}
