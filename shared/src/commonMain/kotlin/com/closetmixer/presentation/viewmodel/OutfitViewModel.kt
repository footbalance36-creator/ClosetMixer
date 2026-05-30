package com.closetmixer.presentation.viewmodel

import com.closetmixer.data.remote.WeatherDto
import com.closetmixer.domain.model.CulturalStyle
import com.closetmixer.domain.usecase.GenerateOutfitUseCase
import com.closetmixer.domain.usecase.GeneratedOutfit
import com.closetmixer.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OutfitUiState(
    val generatedOutfit: GeneratedOutfit? = null,
    val weather: WeatherDto? = null,
    val culturalStyle: CulturalStyle = CulturalStyle.NEUTRAL,
    val isLoading: Boolean = false,
    val error: String? = null
)

class OutfitViewModel(
    private val generateOutfit: GenerateOutfitUseCase,
    private val getWeather: GetWeatherUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _uiState = MutableStateFlow(OutfitUiState())
    val uiState: StateFlow<OutfitUiState> = _uiState.asStateFlow()

    fun loadWeather(lat: Double, lon: Double) {
        scope.launch {
            runCatching { getWeather.execute(lat, lon) }
                .onSuccess { weather -> _uiState.update { it.copy(weather = weather) } }
        }
    }

    fun generate(culturalStyle: CulturalStyle = CulturalStyle.NEUTRAL) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching { generateOutfit.generate(_uiState.value.weather, culturalStyle) }
                .onSuccess { outfit -> _uiState.update { it.copy(generatedOutfit = outfit, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun setCulturalStyle(style: CulturalStyle) {
        _uiState.update { it.copy(culturalStyle = style) }
    }
}
