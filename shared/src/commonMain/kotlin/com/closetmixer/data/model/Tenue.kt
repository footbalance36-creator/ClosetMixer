package com.closetmixer.data.model

data class Tenue(
    val id: String,
    val nom: String,
    val occasion: String?,
    val saison: String?,
    val isFavori: Long,
    val dateCreation: Long,
    val datePortee: Long?
)
