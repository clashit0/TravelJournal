package com.abhinav.traveljournal.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abhinav.traveljournal.presentation.JournalViewmodel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalDetailScreen(
    journalId: Int,
    viewmodel: JournalViewmodel
){
    val journal = viewmodel.getJournalById(journalId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal Details") }
            )
        }
    ) { innerPadding ->
        if (journal == null){
            Box(Modifier.padding(innerPadding)
                .fillMaxSize(),
                contentAlignment = Alignment.Center){
                Text("Journal not found")
            }
        }else{
            Column(
                Modifier.padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = journal.title,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = journal.content,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Created at: ${Date(journal.createdAt)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}