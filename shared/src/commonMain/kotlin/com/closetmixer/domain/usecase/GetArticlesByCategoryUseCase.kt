package com.closetmixer.domain.usecase

import com.closetmixer.data.model.Article
import com.closetmixer.data.repository.ArticleRepository
import com.closetmixer.domain.model.ArticleCategory

class GetArticlesByCategoryUseCase(private val repo: ArticleRepository) {

    suspend fun execute(category: ArticleCategory? = null): List<Article> =
        if (category == null) repo.getAllArticles()
        else repo.getByCategory(category)
}
