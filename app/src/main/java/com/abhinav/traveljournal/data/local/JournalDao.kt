package com.abhinav.traveljournal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abhinav.traveljournal.common.ResultState
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: JournalEntity)

    @Query("SELECT * FROM journal_table ORDER BY createdAt DESC")
    fun getAllJournals(): Flow<List<JournalEntity>>
}