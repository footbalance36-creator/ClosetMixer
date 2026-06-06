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

        val fullBodyItems = listOf("robe", "abaya", "kaftan", "sari", "kimono", "hanbok")

        val haut = allArticles.filter {
            it.categorie == ArticleCategory.VETEMENT.key &&
                it.sousCategorie in listOf("haut", "chemise", "pull", "veste", "manteau") + fullBodyItems
        }.randomOrNull()

        val bas = if (haut?.sousCategorie in fullBodyItems) null
        else allArticles.filter {
            it.categorie == ArticleCategory.VETEMENT.key &&
                it.sousCategorie in listOf("pantalon", "jupe", "short")
        }.randomOrNull()

        val chaussure = allArticles.filter { it.categorie == ArticleCategory.CHAUSSURE.key }
            .let { list ->
                if (temp != null && temp < 10.0)
                    list.filter { it.sousCategorie in listOf("botte", "basket") }.randomOrNull()
                        ?: list.randomOrNull()
                else list.randomOrNull()
            }

        val bijou = run {
            val bijoux = allArticles.filter { it.categorie == ArticleCategory.BIJOU.key }
            val outfitColors = listOfNotNull(haut?.couleur, bas?.couleur, chaussure?.couleur)
                .map { it.lowercase() }
            val warmColors = setOf("or", "doré", "camel", "marron", "rouge", "bordeaux",
                "corail", "orange", "jaune", "rose", "fuchsia", "kaki")
            val coolColors = setOf("bleu", "bleu ciel", "bleu marine", "violet", "lavande",
                "menthe", "vert", "vert olive", "argenté", "gris", "gris clair", "gris foncé")
            val warmCount = outfitColors.count { it in warmColors }
            val coolCount = outfitColors.count { it in coolColors }
            val preferredMetals = when {
                warmCount > coolCount -> listOf("or", "rose")
                coolCount > warmCount -> listOf("argent")
                else -> null
            }
            preferredMetals
                ?.let { metals -> bijoux.filter { it.metal in metals }.randomOrNull() }
                ?: bijoux.randomOrNull()
        }
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
