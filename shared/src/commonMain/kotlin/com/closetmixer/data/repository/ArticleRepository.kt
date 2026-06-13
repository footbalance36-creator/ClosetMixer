package com.closetmixer.data.repository

import com.closetmixer.data.model.Article
import com.closetmixer.db.ClosetDatabase
import com.closetmixer.domain.model.ArticleCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ArticleRepository(private val db: ClosetDatabase) {

    private val queries = db.closetDatabaseQueries

    suspend fun getAllArticles(): List<Article> = withContext(Dispatchers.Default) {
        queries.getAllArticles().executeAsList().map { it.toModel() }
    }

    suspend fun getByCategory(category: ArticleCategory): List<Article> = withContext(Dispatchers.Default) {
        queries.getArticlesByCategory(category.key).executeAsList().map { it.toModel() }
    }

    suspend fun getFavorites(): List<Article> = withContext(Dispatchers.Default) {
        queries.getFavoriteArticles().executeAsList().map { it.toModel() }
    }

    suspend fun getMostUsed(): List<Article> = withContext(Dispatchers.Default) {
        queries.getMostUsedArticles().executeAsList().map { it.toModel() }
    }

    suspend fun getNeverUsed(): List<Article> = withContext(Dispatchers.Default) {
        queries.getNeverUsedArticles().executeAsList().map { it.toModel() }
    }

    suspend fun insert(article: Article) = withContext(Dispatchers.Default) {
        queries.insertArticle(
            id = article.id,
            photoPath = article.photoPath,
            categorie = article.categorie,
            sousCategorie = article.sousCategorie,
            couleur = article.couleur,
            metal = article.metal,
            tags = article.tags,
            culture = article.culture,
            dateAjout = article.dateAjout
        )
    }

    suspend fun toggleFavorite(id: String) = withContext(Dispatchers.Default) {
        val current = queries.getAllArticles().executeAsList().firstOrNull { it.id == id }
        val newVal = if (current?.isFavori == 1L) 0L else 1L
        queries.updateArticleFavori(newVal, id)
    }

    suspend fun incrementUsage(id: String) = withContext(Dispatchers.Default) {
        queries.incrementUsage(id)
    }

    suspend fun searchArticles(query: String): List<Article> = withContext(Dispatchers.Default) {
        queries.searchArticles(query).executeAsList().map { it.toModel() }
    }

    suspend fun delete(id: String) = withContext(Dispatchers.Default) {
        queries.deleteArticle(id)
    }

    private fun com.closetmixer.db.Article.toModel() = Article(
        id = id,
        photoPath = photoPath,
        categorie = categorie,
        sousCategorie = sousCategorie,
        couleur = couleur,
        metal = metal,
        tags = tags,
        culture = culture,
        dateAjout = dateAjout,
        nbUtilisations = nbUtilisations,
        isFavori = isFavori
    )
}
