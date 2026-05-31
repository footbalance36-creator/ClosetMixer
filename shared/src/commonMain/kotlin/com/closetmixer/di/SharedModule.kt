package com.closetmixer.di

import app.cash.sqldelight.db.SqlDriver
import com.closetmixer.data.remote.WeatherApi
import com.closetmixer.data.repository.ArticleRepository
import com.closetmixer.data.repository.TenueRepository
import com.closetmixer.data.repository.WeatherRepository
import com.closetmixer.db.ClosetDatabase
import com.closetmixer.domain.usecase.AddArticleUseCase
import com.closetmixer.domain.usecase.GenerateOutfitUseCase
import com.closetmixer.domain.usecase.GetArticlesByCategoryUseCase
import com.closetmixer.domain.usecase.GetStatsUseCase
import com.closetmixer.domain.usecase.GetWeatherUseCase
import com.closetmixer.domain.usecase.PlanOutfitUseCase
import com.closetmixer.presentation.viewmodel.CalendarViewModel
import com.closetmixer.presentation.viewmodel.OutfitViewModel
import com.closetmixer.presentation.viewmodel.SettingsViewModel
import com.closetmixer.presentation.viewmodel.StatsViewModel
import com.closetmixer.presentation.viewmodel.VoyageViewModel
import com.closetmixer.presentation.viewmodel.WardrobeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val sharedModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) { level = LogLevel.NONE }
        }
    }

    // SqlDriver registered explicitly by platform module (androidModule / iosModule)
    single { ClosetDatabase(get<SqlDriver>()) }

    single { WeatherApi(get()) }
    single { ArticleRepository(get()) }
    single { TenueRepository(get()) }
    single { WeatherRepository(get()) }

    factory { AddArticleUseCase(get()) }
    factory { GetArticlesByCategoryUseCase(get()) }
    factory { GenerateOutfitUseCase(get()) }
    factory { GetWeatherUseCase(get()) }
    factory { PlanOutfitUseCase(get()) }
    factory { GetStatsUseCase(get()) }

    // singles so each screen gets the same instance (no AndroidX ViewModel used)
    single { WardrobeViewModel(get(), get()) }
    single { OutfitViewModel(get(), get()) }
    single { CalendarViewModel(get(), get()) }
    single { VoyageViewModel(get()) }
    single { StatsViewModel(get()) }
    single { SettingsViewModel(get()) }
}
