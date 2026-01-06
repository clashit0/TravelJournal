package com.abhinav.traveljournal.data.repository

import com.abhinav.traveljournal.common.ResultState
import com.abhinav.traveljournal.data.local.JournalDao
import com.abhinav.traveljournal.data.local.JournalEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class JournalRepositoryImplementation(private val journalDao : JournalDao) : JournalRepository{
    override suspend fun insertJournal(journal: JournalEntity) : ResultState<JournalEntity> {

        return try {
            journalDao.insertJournal(journal)
             ResultState.Success(journal)
        }catch (e : Exception){
            ResultState.Error(e.message.toString())
        }
    }

    override fun getAllJournals(): Flow<ResultState<List<JournalEntity>>> = flow{
       emit(ResultState.Loading)
        try {
            journalDao.getAllJournals().collect{ journals ->
                emit(ResultState.Success(journals))
            }
        }catch (e: Exception){
            emit(ResultState.Error(e.message.toString()))

        }
    }

    override suspend fun deleteJournal(journal: JournalEntity): ResultState<Unit> {
        return try {
            journalDao.deleteJournal(journal)
            ResultState.Success(Unit)
        }catch (e: Exception){
            ResultState.Error(e.message?: "Delete failed")
        }
    }

    override suspend fun getJournalById(id: Int): ResultState<JournalEntity> {
        return try {
            val journal = journalDao.getJournalById(id)

            if(journal != null){
                ResultState.Success(journal)
            }else{
                ResultState.Error("Journal not found")
            }

        }catch (e: Exception){
            ResultState.Error(e.message?: "Get failed")

        }
    }
}