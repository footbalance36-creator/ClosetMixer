package com.closetmixer.domain.usecase

import com.closetmixer.db.ClosetDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AppStats(
    val articlesByCategory: Map<String, Long>,
    val totalTenues: Long,
    val neverUsedCount: Long
)

class GetStatsUseCase(private val db: ClosetDatabase) {

    suspend fun execute(): AppStats = withContext(Dispatchers.Default) {
        val queries = db.closetDatabaseQueries
        val byCategory = queries.countArticlesByCategory().executeAsList()
            .associate { it.categorie to it.count }
        val totalTenues = queries.countTotalTenues().executeAsOne()
        val neverUsed = queries.countNeverUsed().executeAsOne()
        AppStats(byCategory, totalTenues, neverUsed)
    }
}
