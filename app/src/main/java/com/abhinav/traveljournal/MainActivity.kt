package com.abhinav.traveljournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.abhinav.traveljournal.data.local.JournalDatabase
import com.abhinav.traveljournal.data.repository.JournalRepositoryImplementation
import com.abhinav.traveljournal.presentation.JournalViewmodel
import com.abhinav.traveljournal.presentation.navigation.AppNavGraph
import com.abhinav.traveljournal.ui.theme.TravelJournalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = JournalDatabase.getDatabase(applicationContext)
        val repository = JournalRepositoryImplementation(database.journalDao())
        val viewModel = JournalViewmodel(repository)

        enableEdgeToEdge()
        setContent {
            TravelJournalTheme {
                AppNavGraph(viewModel)
            }
        }
    }
}
