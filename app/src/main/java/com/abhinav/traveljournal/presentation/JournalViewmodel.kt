package com.abhinav.traveljournal.presentation

import com.abhinav.traveljournal.data.local.JournalEntity

class JournalViewmodel()


data class JournalState(
    var journals : List<JournalEntity> = emptyList(),
    var isLoading: Boolean = false,
    var error:String? = null
)