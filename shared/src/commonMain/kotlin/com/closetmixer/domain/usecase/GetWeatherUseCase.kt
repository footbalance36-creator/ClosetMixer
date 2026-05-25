package com.closetmixer.domain.usecase

import com.closetmixer.data.remote.WeatherDto
import com.closetmixer.data.repository.WeatherRepository

class GetWeatherUseCase(private val repo: WeatherRepository) {

    suspend fun execute(lat: Double, lon: Double): WeatherDto =
        repo.getWeather(lat, lon)
}
