package com.closetmixer.data.repository

import com.closetmixer.data.model.CalendarEntry
import com.closetmixer.db.ClosetDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CalendarRepository(private val db: ClosetDatabase) {

    private val queries = db.closetDatabaseQueries

    suspend fun getMonthEntries(yearMonth: String): List<CalendarEntry> = withContext(Dispatchers.Default) {
        queries.getMonthEntries("$yearMonth%").executeAsList().map {
            CalendarEntry(it.date, it.tenueId, it.meteo, it.temperature)
        }
    }

    suspend fun setTenueForDate(date: String, tenueId: String) = withContext(Dispatchers.Default) {
        queries.setTenueForDate(date, tenueId)
    }

    suspend fun clearDate(date: String) = withContext(Dispatchers.Default) {
        queries.clearDate(date)
    }
}
