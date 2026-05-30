package com.closetmixer.data.repository

import com.closetmixer.data.remote.WeatherApi
import com.closetmixer.data.remote.WeatherDto

class WeatherRepository(private val api: WeatherApi) {

    suspend fun getWeather(lat: Double, lon: Double): WeatherDto =
        api.getWeather(lat, lon)
}
