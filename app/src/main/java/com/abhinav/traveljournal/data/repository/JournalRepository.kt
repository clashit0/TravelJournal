package com.abhinav.traveljournal.data.repository

import com.abhinav.traveljournal.common.ResultState
import com.abhinav.traveljournal.data.local.JournalEntity
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    suspend fun insertJournal(journal: JournalEntity): ResultState<JournalEntity>

    fun getAllJournals(): Flow<ResultState<List<JournalEntity>>>

    suspend fun deleteJournal(journal: JournalEntity): ResultState<Unit>
}