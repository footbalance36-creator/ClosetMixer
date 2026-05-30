package com.closetmixer.data.model

data class Article(
    val id: String,
    val photoPath: String,
    val categorie: String,
    val sousCategorie: String,
    val couleur: String?,
    val metal: String?,
    val tags: String,
    val culture: String,
    val dateAjout: Long,
    val nbUtilisations: Long,
    val isFavori: Long
)
