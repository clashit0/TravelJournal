package com.abhinav.traveljournal.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abhinav.traveljournal.presentation.JournalViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJournalScreen(
    viewmodel: JournalViewmodel,
    onSaved: () -> Unit
){
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Journal") })
        }
    ) { innerPadding->
        Column(
            Modifier.padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {title = it},
                label = {Text("Title")},
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )

            OutlinedTextField(
                value = description,
                onValueChange = {description = it},
                label = {Text("Description")},
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    viewmodel.insertJournal(title,description)
                    onSaved()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Text("Save Journal")
            }
        }
    }
}