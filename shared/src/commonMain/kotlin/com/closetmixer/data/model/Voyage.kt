package com.closetmixer.data.model

data class Voyage(
    val id: String,
    val nom: String,
    val destination: String?,
    val dateDebut: String?,
    val dateFin: String?,
    val dateCreation: Long
)
