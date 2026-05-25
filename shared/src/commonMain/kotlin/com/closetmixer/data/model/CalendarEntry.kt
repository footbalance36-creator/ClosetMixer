package com.closetmixer.data.model

data class CalendarEntry(
    val date: String,
    val tenueId: String,
    val meteo: String?,
    val temperature: Double?
)
