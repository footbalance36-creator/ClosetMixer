package com.closetmixer.domain.usecase

import com.closetmixer.data.model.CalendarEntry
import com.closetmixer.data.remote.WeatherDto
import com.closetmixer.db.ClosetDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlanOutfitUseCase(private val db: ClosetDatabase) {

    suspend fun execute(date: String, tenueId: String, weather: WeatherDto? = null) =
        withContext(Dispatchers.Default) {
            db.closetDatabaseQueries.insertCalendarEntry(
                date = date,
                tenueId = tenueId,
                meteo = weather?.current?.weatherCode?.toString(),
                temperature = weather?.current?.temperature
            )
        }

    suspend fun getEntry(date: String): CalendarEntry? = withContext(Dispatchers.Default) {
        db.closetDatabaseQueries.getCalendarEntry(date).executeAsOneOrNull()?.let {
            CalendarEntry(it.date, it.tenueId, it.meteo, it.temperature)
        }
    }

    suspend fun getMonthEntries(yearMonth: String): List<CalendarEntry> = withContext(Dispatchers.Default) {
        db.closetDatabaseQueries.getMonthEntries("$yearMonth%").executeAsList().map {
            CalendarEntry(it.date, it.tenueId, it.meteo, it.temperature)
        }
    }
}
