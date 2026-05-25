package com.closetmixer.domain.usecase

import com.closetmixer.data.model.Article
import com.closetmixer.data.repository.ArticleRepository
import kotlinx.datetime.Clock

class AddArticleUseCase(private val repo: ArticleRepository) {

    suspend fun execute(
        id: String,
        photoPath: String,
        categorie: String,
        sousCategorie: String,
        couleur: String? = null,
        metal: String? = null,
        tags: List<String> = emptyList(),
        culture: String = "neutral"
    ) {
        val article = Article(
            id = id,
            photoPath = photoPath,
            categorie = categorie,
            sousCategorie = sousCategorie,
            couleur = couleur,
            metal = metal,
            tags = tags.joinToString(","),
            culture = culture,
            dateAjout = Clock.System.now().toEpochMilliseconds(),
            nbUtilisations = 0,
            isFavori = 0
        )
        repo.insert(article)
    }
}
