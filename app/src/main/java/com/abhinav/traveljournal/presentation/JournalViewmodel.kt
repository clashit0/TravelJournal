package com.abhinav.traveljournal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhinav.traveljournal.common.ResultState
import com.abhinav.traveljournal.data.local.JournalEntity
import com.abhinav.traveljournal.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class JournalViewmodel(
    private val repository: JournalRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<ResultState<List<JournalEntity>>>(ResultState.Loading)
    val state: StateFlow<ResultState<List<JournalEntity>>> = _state.asStateFlow()

    private val _selectedJournal = MutableStateFlow<ResultState<JournalEntity>>(ResultState.Loading)

    val selectedJournal: StateFlow<ResultState<JournalEntity>> = _selectedJournal.asStateFlow()

    private var editingJournalId: Int? = null

    init {
        getAllJournals()
    }

    private fun getAllJournals() {
        viewModelScope.launch {
            repository.getAllJournals().collectLatest { state ->
                _state.value = state
            }
        }
    }

    fun loadJournal(id: Int){
        viewModelScope.launch {
            editingJournalId = id
            _selectedJournal.value = ResultState.Loading
            _selectedJournal.value = repository.getJournalById(id)
        }
    }

    fun insertJournal(
        title: String,
        description: String,
        journalId: Int? = null,
        imageUri: String?,
        audioUri: String?,
        latitude: Double?,
        longitude: Double?
    ) {
        viewModelScope.launch {
            val finalId = editingJournalId ?: 0

            val journal = JournalEntity(
                id = finalId,
                title = title,
                content = description,
                imageUri = imageUri,
                audioUri = audioUri,
                latitude = latitude,
                longitude = longitude,
                createdAt = System.currentTimeMillis()
            )


            when (
                val result = repository.insertJournal(
                    JournalEntity(
                        id = journalId ?: 0,
                        title = title,
                        content = description,
                        imageUri = imageUri,
                        audioUri = audioUri,
                        latitude = latitude,
                        longitude = longitude,
                        createdAt = System.currentTimeMillis()
                    )
                )
            ) {


                is ResultState.Error -> {
                    _state.value = ResultState.Error(result.message)
                }

                else -> {
                    editingJournalId = null
                }
            }
        }
    }

    fun getJournalById(id: Int): JournalEntity? {
        return when (val state = state.value) {
            is ResultState.Success -> {
                state.data.find { it.id == id }
            }

            else -> null
        }
    }

    fun deleteJournal(journal: JournalEntity) {
        viewModelScope.launch {
            when (val result = repository.deleteJournal(journal)) {
                is ResultState.Error -> {
                    _state.value = ResultState.Error(result.message)
                }

                else -> null
            }
        }
    }



}
