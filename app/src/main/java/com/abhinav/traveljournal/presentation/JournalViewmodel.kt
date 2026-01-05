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
    private val repository: JournalRepository
): ViewModel() {
    private val _state = MutableStateFlow<ResultState<List<JournalEntity>>>(ResultState.Loading)
    val state: StateFlow<ResultState<List<JournalEntity>>> = _state.asStateFlow()

    init {
        getAllJournals()
    }
    private fun getAllJournals(){
        viewModelScope.launch {
            repository.getAllJournals().collectLatest { state ->
                _state.value =state
            }
        }
    }

    fun insertJournal(title: String, description: String) {
        viewModelScope.launch {
            when (
                val result = repository.insertJournal(
                    JournalEntity(
                        title = title,
                        content = description,
                        createdAt = System.currentTimeMillis()
                    )
                )
            ) {
                is ResultState.Success -> {
                    // No action needed
                    // Room Flow will emit updated list automatically
                }

                is ResultState.Error -> {
                    _state.value = ResultState.Error(result.message)
                }

                ResultState.Loading -> Unit
            }
        }
    }


}
