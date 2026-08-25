package com.example.data.local

import com.example.model.CalculationHistory
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {

    val allHistory: Flow<List<CalculationHistory>> = historyDao.getAllHistory()
    val favoriteHistory: Flow<List<CalculationHistory>> = historyDao.getFavoriteHistory()

    fun search(query: String): Flow<List<CalculationHistory>> = historyDao.searchHistory(query)

    suspend fun insert(expression: String, result: String, mode: String = "STANDARD", note: String = ""): Long {
        if (expression.isBlank() || result.isBlank() || result == "Error") return -1L
        val item = CalculationHistory(
            expression = expression,
            result = result,
            timestamp = System.currentTimeMillis(),
            mode = mode,
            note = note
        )
        return historyDao.insertHistory(item)
    }

    suspend fun toggleFavorite(history: CalculationHistory) {
        historyDao.updateHistory(history.copy(isFavorite = !history.isFavorite))
    }

    suspend fun updateNote(history: CalculationHistory, note: String) {
        historyDao.updateHistory(history.copy(note = note))
    }

    suspend fun deleteById(id: Long) {
        historyDao.deleteHistoryById(id)
    }

    suspend fun clearAll() {
        historyDao.clearAllHistory()
    }
}
