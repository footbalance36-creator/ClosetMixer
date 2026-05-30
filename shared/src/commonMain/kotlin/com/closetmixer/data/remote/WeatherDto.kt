package com.closetmixer.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    val current: CurrentWeatherDto,
    val latitude: Double,
    val longitude: Double,
    val timezone: String
)

@Serializable
data class CurrentWeatherDto(
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("weathercode") val weatherCode: Int,
    @SerialName("relative_humidity_2m") val humidity: Int
)

fun Int.toWeatherDescription(): String = when (this) {
    0 -> "Ciel dégagé"
    1, 2, 3 -> "Partiellement nuageux"
    45, 48 -> "Brouillard"
    in 51..57 -> "Bruine"
    in 61..67 -> "Pluie"
    in 71..77 -> "Neige"
    in 80..82 -> "Averses"
    95 -> "Orage"
    else -> "Nuageux"
}

fun Int.toWeatherIcon(): String = when (this) {
    0 -> "☀️"
    1, 2, 3 -> "⛅"
    45, 48 -> "🌫️"
    in 51..57 -> "🌦️"
    in 61..67 -> "🌧️"
    in 71..77 -> "❄️"
    in 80..82 -> "🌦️"
    95 -> "⛈️"
    else -> "☁️"
}
