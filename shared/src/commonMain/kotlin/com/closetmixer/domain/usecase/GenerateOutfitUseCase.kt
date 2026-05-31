package com.closetmixer.domain.usecase

import com.closetmixer.data.model.Article
import com.closetmixer.data.remote.WeatherDto
import com.closetmixer.data.repository.ArticleRepository
import com.closetmixer.domain.model.ArticleCategory
import com.closetmixer.domain.model.CulturalStyle

class GenerateOutfitUseCase(private val articleRepo: ArticleRepository) {

    suspend fun generate(
        weather: WeatherDto? = null,
        culturalStyle: CulturalStyle = CulturalStyle.NEUTRAL,
        occasion: String? = null
    ): GeneratedOutfit {
        val allArticles = articleRepo.getAllArticles()
        val temp = weather?.current?.temperature

        val haut = allArticles.filter {
            it.categorie == ArticleCategory.VETEMENT.key &&
                it.sousCategorie in listOf("haut", "chemise", "pull", "robe", "abaya", "kimono", "hanbok")
        }.randomOrNull()

        val bas = if (haut?.sousCategorie in listOf("robe", "abaya")) null
        else allArticles.filter {
            it.sousCategorie in listOf("bas", "pantalon", "jupe", "short")
        }.randomOrNull()

        val chaussure = allArticles.filter { it.categorie == ArticleCategory.CHAUSSURE.key }
            .let { list ->
                if (temp != null && temp < 10.0)
                    list.filter { it.sousCategorie in listOf("botte", "basket") }.randomOrNull()
                        ?: list.randomOrNull()
                else list.randomOrNull()
            }

        val bijou = allArticles.filter { it.categorie == ArticleCategory.BIJOU.key }.randomOrNull()
        val maquillage = allArticles.filter { it.categorie == ArticleCategory.MAQUILLAGE.key }.randomOrNull()
        val couvreChef = if (culturalStyle == CulturalStyle.MODEST)
            allArticles.filter { it.categorie == ArticleCategory.COUVRE_CHEF.key }.randomOrNull()
        else null

        return GeneratedOutfit(haut, bas, chaussure, bijou, maquillage, couvreChef)
    }
}

data class GeneratedOutfit(
    val haut: Article?,
    val bas: Article?,
    val chaussure: Article?,
    val bijou: Article?,
    val maquillage: Article?,
    val couvreChef: Article?
)
