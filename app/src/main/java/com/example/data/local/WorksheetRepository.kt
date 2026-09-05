package com.example.data.local

import com.example.domain.WorksheetTapeEngine
import com.example.model.WorksheetDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorksheetRepository(private val dao: WorksheetDao) {

    fun getAllWorksheets(): Flow<List<WorksheetDocument>> {
        return dao.getAllWorksheets().map { entities ->
            entities.map { it.toDocument() }
        }
    }

    suspend fun getWorksheetById(id: String): WorksheetDocument? {
        return dao.getWorksheetById(id)?.toDocument()
    }

    suspend fun saveWorksheet(doc: WorksheetDocument) {
        val calculatedLines = WorksheetTapeEngine.recalculate(doc.lines)
        val finalGrandTotal = calculatedLines.lastOrNull()?.runningTotal ?: 0.0
        val updatedDoc = doc.copy(
            lines = calculatedLines,
            grandTotal = finalGrandTotal,
            updatedAt = System.currentTimeMillis()
        )
        dao.insertOrUpdate(WorksheetEntity.fromDocument(updatedDoc))
    }

    suspend fun deleteWorksheet(id: String) {
        dao.deleteWorksheet(id)
    }

    suspend fun populateInitialDataIfEmpty(currentCount: Int) {
        if (currentCount == 0) {
            val templates = WorksheetTapeEngine.getDefaultTemplates()
            templates.forEach { template ->
                val lines = WorksheetTapeEngine.recalculate(template.lines)
                val total = lines.lastOrNull()?.runningTotal ?: 0.0
                val doc = WorksheetDocument(
                    title = template.title,
                    lines = lines,
                    grandTotal = total
                )
                dao.insertOrUpdate(WorksheetEntity.fromDocument(doc))
            }
        }
    }
}
