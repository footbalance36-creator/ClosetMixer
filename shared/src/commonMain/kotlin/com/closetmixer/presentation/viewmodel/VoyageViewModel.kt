package com.closetmixer.presentation.viewmodel

import com.closetmixer.data.model.Article
import com.closetmixer.data.model.Voyage
import com.closetmixer.db.ClosetDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

data class VoyageUiState(
    val voyages: List<Voyage> = emptyList(),
    val selectedVoyage: Voyage? = null,
    val packingList: List<Article> = emptyList(),
    val allArticles: List<Article> = emptyList(),
    val isLoading: Boolean = false
)

class VoyageViewModel(private val db: ClosetDatabase) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _uiState = MutableStateFlow(VoyageUiState())
    val uiState: StateFlow<VoyageUiState> = _uiState.asStateFlow()

    init { loadVoyages() }

    fun loadVoyages() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val voyages = withContext(Dispatchers.Default) {
                db.closetDatabaseQueries.getAllVoyages().executeAsList().map {
                    Voyage(it.id, it.nom, it.destination, it.dateDebut, it.dateFin, it.dateCreation)
                }
            }
            _uiState.update { it.copy(voyages = voyages, isLoading = false) }
        }
    }

    fun createVoyage(id: String, nom: String, destination: String?, dateDebut: String?, dateFin: String?) {
        scope.launch {
            withContext(Dispatchers.Default) {
                db.closetDatabaseQueries.insertVoyage(
                    id, nom, destination, dateDebut, dateFin,
                    Clock.System.now().toEpochMilliseconds()
                )
            }
            loadVoyages()
        }
    }

    fun selectVoyage(voyage: Voyage) {
        scope.launch {
            _uiState.update { it.copy(selectedVoyage = voyage) }
            reloadPackingList(voyage.id)
        }
    }

    fun clearSelectedVoyage() {
        _uiState.update { it.copy(selectedVoyage = null, packingList = emptyList()) }
    }

    fun loadAllArticles() {
        scope.launch {
            val articles = withContext(Dispatchers.Default) {
                db.closetDatabaseQueries.getAllArticles().executeAsList().map { it.toArticle() }
            }
            _uiState.update { it.copy(allArticles = articles) }
        }
    }

    fun addArticleToVoyage(articleId: String) {
        val voyageId = _uiState.value.selectedVoyage?.id ?: return
        scope.launch {
            withContext(Dispatchers.Default) {
                db.closetDatabaseQueries.insertVoyageArticle(voyageId, articleId)
            }
            reloadPackingList(voyageId)
        }
    }

    fun removeArticleFromVoyage(articleId: String) {
        val voyageId = _uiState.value.selectedVoyage?.id ?: return
        scope.launch {
            withContext(Dispatchers.Default) {
                db.closetDatabaseQueries.removeVoyageArticle(voyageId, articleId)
            }
            reloadPackingList(voyageId)
        }
    }

    fun deleteVoyage(voyageId: String) {
        scope.launch {
            withContext(Dispatchers.Default) {
                db.closetDatabaseQueries.deleteVoyage(voyageId)
            }
            clearSelectedVoyage()
            loadVoyages()
        }
    }

    private suspend fun reloadPackingList(voyageId: String) {
        val articles = withContext(Dispatchers.Default) {
            db.closetDatabaseQueries.getArticlesForVoyage(voyageId).executeAsList()
                .map { it.toArticle() }
        }
        _uiState.update { it.copy(packingList = articles) }
    }

    private fun com.closetmixer.db.Article.toArticle() = Article(
        id, photoPath, categorie, sousCategorie, couleur, metal, tags, culture,
        dateAjout, nbUtilisations, isFavori
    )
}
