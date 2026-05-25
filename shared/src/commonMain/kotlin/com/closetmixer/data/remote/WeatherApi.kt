package com.closetmixer.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class WeatherApi(private val client: HttpClient) {

    suspend fun getWeather(lat: Double, lon: Double): WeatherDto {
        return client.get("https://api.open-meteo.com/v1/forecast") {
            parameter("latitude", lat)
            parameter("longitude", lon)
            parameter("current", "temperature_2m,weathercode,relative_humidity_2m")
            parameter("timezone", "auto")
        }.body()
    }
}
