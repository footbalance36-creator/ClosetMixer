package com.closetmixer.data.repository

import com.closetmixer.data.model.Article
import com.closetmixer.data.model.Tenue
import com.closetmixer.db.ClosetDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TenueRepository(private val db: ClosetDatabase) {

    private val queries = db.closetDatabaseQueries

    suspend fun getAllTenues(): List<Tenue> = withContext(Dispatchers.Default) {
        queries.getAllTenues().executeAsList().map { it.toModel() }
    }

    suspend fun getFavorites(): List<Tenue> = withContext(Dispatchers.Default) {
        queries.getFavoriteTenues().executeAsList().map { it.toModel() }
    }

    suspend fun getArticlesForTenue(tenueId: String): List<Article> = withContext(Dispatchers.Default) {
        queries.getArticlesForTenue(tenueId).executeAsList().map {
            Article(it.id, it.photoPath, it.categorie, it.sousCategorie, it.couleur,
                it.metal, it.tags, it.culture, it.dateAjout, it.nbUtilisations, it.isFavori)
        }
    }

    suspend fun insert(tenue: Tenue) = withContext(Dispatchers.Default) {
        queries.insertTenue(tenue.id, tenue.nom, tenue.occasion, tenue.saison, tenue.dateCreation)
    }

    suspend fun addArticleToTenue(tenueId: String, articleId: String) = withContext(Dispatchers.Default) {
        queries.insertTenueArticle(tenueId, articleId)
    }

    suspend fun toggleFavorite(id: String) = withContext(Dispatchers.Default) {
        val current = queries.getAllTenues().executeAsList().firstOrNull { it.id == id }
        val newVal = if (current?.isFavori == 1L) 0L else 1L
        queries.updateTenueFavori(newVal, id)
    }

    suspend fun delete(id: String) = withContext(Dispatchers.Default) {
        queries.deleteTenue(id)
    }

    private fun com.closetmixer.db.Tenue.toModel() = Tenue(
        id = id, nom = nom, occasion = occasion, saison = saison,
        isFavori = isFavori, dateCreation = dateCreation, datePortee = datePortee
    )
}
