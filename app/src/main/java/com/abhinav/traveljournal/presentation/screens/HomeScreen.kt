package com.abhinav.traveljournal.presentation.screens

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abhinav.traveljournal.common.ResultState
import com.abhinav.traveljournal.data.local.JournalEntity
import com.abhinav.traveljournal.presentation.JournalViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewmodel: JournalViewmodel,
    onAddClick: () -> Unit,
    onJournalClick:(Int) -> Unit,
    onEdit: (JournalEntity) -> Unit
) {
    val state by viewmodel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Travel Journal") })
        },
        floatingActionButton = {
            FloatingActionButton(onAddClick) {
                Text("+")
            }
        }
    ) { innerPadding ->

        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            when(state){
                is ResultState.Loading ->{
                    CircularProgressIndicator(
                        Modifier.align(Alignment.Center)
                    )
                }

                is ResultState.Error ->{
                    Text(
                        text = (state as ResultState.Error).message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ResultState.Success ->{
                    val journals = (state as ResultState.Success<List<JournalEntity>>).data

                    if (journals.isEmpty()){
                        EmptyState()
                    }else{
                        JournalList(journals,
                            onItemClick = {id ->
                                onJournalClick(id)
                            },
                            onEdit ={journal->
                                onEdit(journal)
                            },
                            onDelete = {journal->
                                viewmodel.deleteJournal(journal)
                            })
                    }
                }
            }
        }

    }
}


@Composable
fun JournalList(journals: List<JournalEntity>,
                onItemClick:(Int) -> Unit,
                onEdit:(JournalEntity) -> Unit,
                onDelete: (JournalEntity) -> Unit
                ) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(journals) { journal ->
            JournalItem(journal = journal,
                onAddClick = { onItemClick(journal.id) },
                onEdit = onEdit,
                onDelete = onDelete)
        }
    }
}

@Composable
fun JournalItem(journal: JournalEntity,
                onAddClick: (Int) -> Unit,
                onEdit:(JournalEntity) -> Unit,
                onDelete: (JournalEntity) -> Unit) {


    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth()
            .combinedClickable(onClick = {onAddClick(journal.id)},
                onLongClick = {expanded = true}),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier.padding(16.dp)
        ) {
            Text(
                text = journal.title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = journal.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            DropdownMenuItem(
                text = {Text("Edit")},
                onClick = {
                    expanded = false
                    onEdit(journal)
                }
            )
            DropdownMenuItem(
                text = {Text("Delete")},
                onClick = {expanded = false
                onDelete(journal)}
            )
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No journals yet. Start writing ")
    }
}