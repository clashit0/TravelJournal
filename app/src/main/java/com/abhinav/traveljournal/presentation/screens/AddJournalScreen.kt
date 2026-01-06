package com.abhinav.traveljournal.presentation.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.abhinav.traveljournal.common.AddEditContent
import com.abhinav.traveljournal.common.AudioRecorder
import com.abhinav.traveljournal.common.LocationProvider
import com.abhinav.traveljournal.common.ResultState
import com.abhinav.traveljournal.data.local.JournalEntity
import com.abhinav.traveljournal.presentation.JournalViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJournalScreen(
    viewmodel: JournalViewmodel,
    journalId: Int? = null,
    onSaved: () -> Unit
) {
    val context = LocalContext.current


    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val recorder = remember { AudioRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }
    var audioPath by remember { mutableStateOf<String?>(null) }

    val locationProvider = remember { LocationProvider(context) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }




    val locationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                locationProvider.getCurrentLocation { lat, lon ->
                    latitude = lat
                    longitude = lon
                }
            }
        }


    val audioPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                audioPath = recorder.startRecording()
                isRecording = true
            } else {
                Toast.makeText(context, "Microphone permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    val isEditMode = journalId != null


    var editImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            if (isEditMode) {
                editImageUri = uri
            } else {
                imageUri = uri
            }
        }

    LaunchedEffect(journalId) {
        journalId?.let {
            viewmodel.loadJournal(it)
        }
    }

    val journalState by viewmodel.selectedJournal.collectAsState()



    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(if (journalId == null) "Add Journal" else "Edit Journal")
            })
        }
    ) { innerPadding ->

        Box(Modifier.fillMaxSize()
            .padding(innerPadding)) {

            if (!isEditMode) {
                var title by remember { mutableStateOf("") }
                var description by remember { mutableStateOf("") }

                AddEditContent(
                    title = title,
                    onTitleChange = { title = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    imageUri = imageUri,
                    onPickImage = { imagePicker.launch("image/*") },
                    isRecording = isRecording,
                    onRecordClick = {
                        if (!isRecording) {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            audioPath = recorder.stopRecording()
                            isRecording = false

                        }
                    },
                    latitude = latitude,
                    longitude = longitude,
                    onLocationClick = {
                        locationPermissionLauncher.launch(
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    },
                    onSave = {
                        viewmodel.insertJournal(
                            title = title,
                            description = description,
                            journalId = null,
                            imageUri = imageUri?.toString(),
                            audioUri = audioPath,
                            latitude = latitude,
                            longitude = longitude
                        )
                        onSaved()
                    },
                    isEdit = false,
                    audioPath = audioPath


                )
            } else {

                when (journalState) {
                    is ResultState.Loading -> {
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is ResultState.Error -> {
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text((journalState as ResultState.Error).message)
                        }
                    }

                    is ResultState.Success -> {

                        val journal =
                            (journalState as ResultState.Success<JournalEntity>).data

                        var title by remember(journal) {
                            mutableStateOf(journal.title)
                        }

                        var description by remember(journal) {
                            mutableStateOf(journal.content)
                        }

                        var editAudioPath by remember(journal) {
                            mutableStateOf(journal.audioUri)
                        }



                        LaunchedEffect(journal) {
                            if (editImageUri == null) {
                                editImageUri = journal.imageUri?.let { Uri.parse(it) }
                            }
                        }




                        AddEditContent(
                            title = title,
                            onTitleChange = { title = it },
                            description = description,
                            onDescriptionChange = { description = it },
                            imageUri = editImageUri,
                            onPickImage = { imagePicker.launch("image/*") },
                            isRecording = isRecording,
                            onRecordClick = {
                                if (!isRecording) {
                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    editAudioPath = recorder.stopRecording()
                                    isRecording = false
                                }
                            },
                            latitude = latitude,
                            longitude = longitude,
                            onLocationClick = {
                                locationPermissionLauncher.launch(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            },
                            onSave = {
                                viewmodel.insertJournal(
                                    title = title,
                                    description = description,
                                    journalId = journal.id, // 🔥 UPDATE
                                    imageUri = editImageUri?.toString(),
                                    audioUri = editAudioPath,
                                    latitude = latitude,
                                    longitude = longitude
                                )
                                onSaved()
                            },
                            isEdit = true,
                            audioPath = editAudioPath
                        )


                    }
                }
            }
        }

    }


}
