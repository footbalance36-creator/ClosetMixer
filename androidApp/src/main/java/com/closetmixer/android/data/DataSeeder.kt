package com.closetmixer.android.data

import com.closetmixer.db.ClosetDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DataSeeder {

    suspend fun seedIfEmpty(db: ClosetDatabase) = withContext(Dispatchers.IO) {
        val queries = db.closetDatabaseQueries
        val count = queries.getAllArticles().executeAsList().size
        if (count > 0) return@withContext

        val now = System.currentTimeMillis()

        val articles = listOf(
            listOf("a1", "", "vetement", "haut", "Blanc", "aucun", "casual", "neutral", now),
            listOf("a2", "", "vetement", "bas", "Bleu marine", "aucun", "casual", "neutral", now - 1000),
            listOf("a3", "", "vetement", "robe", "Rouge", "aucun", "soirée", "neutral", now - 2000),
            listOf("a4", "", "vetement", "abaya", "Noir", "aucun", "modest", "modest", now - 3000),
            listOf("a5", "", "chaussure", "talon", "Beige", "aucun", "élégant", "neutral", now - 4000),
            listOf("a6", "", "chaussure", "basket", "Blanc", "aucun", "sport", "neutral", now - 5000),
            listOf("a7", "", "chaussure", "botte", "Marron", "aucun", "hiver", "neutral", now - 6000),
            listOf("a8", "", "bijou", "collier", "Or", "or", "bijou", "neutral", now - 7000),
            listOf("a9", "", "bijou", "boucle_oreille", "Argent", "argent", "bijou", "neutral", now - 8000),
            listOf("a10", "", "maquillage", "rouge_levres", "Rouge", "aucun", "makeup", "neutral", now - 9000),
            listOf("a11", "", "couvre_chef", "hijab", "Beige", "aucun", "modest", "modest", now - 10000),
            listOf("a12", "", "accessoire", "sac", "Noir", "aucun", "sac", "neutral", now - 11000),
        )

        articles.forEach { a ->
            queries.insertArticle(
                id = a[0] as String,
                photoPath = a[1] as String,
                categorie = a[2] as String,
                sousCategorie = a[3] as String,
                couleur = a[4] as String,
                metal = a[5] as String,
                tags = a[6] as String,
                culture = a[7] as String,
                dateAjout = a[8] as Long
            )
        }
    }
}
