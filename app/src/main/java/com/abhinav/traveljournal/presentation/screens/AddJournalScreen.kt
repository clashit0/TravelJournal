package com.abhinav.traveljournal.presentation.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.abhinav.traveljournal.common.AddEditContent
import com.abhinav.traveljournal.common.AudioRecorder
import com.abhinav.traveljournal.common.LocationProvider
import com.abhinav.traveljournal.common.ResultState
import com.abhinav.traveljournal.data.local.JournalEntity
import com.abhinav.traveljournal.presentation.JournalViewmodel
import androidx.core.net.toUri

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJournalScreen(
    viewmodel: JournalViewmodel,
    journalId: Int? = null,
    onSaved: () -> Unit
) {
    val context = LocalContext.current


    var imageUri by remember { mutableStateOf<List<Uri>>(emptyList()) }

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


    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.OpenMultipleDocuments()
        ) { uris ->
            if (uris.isNotEmpty()) {


                uris.forEach { uri ->
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                imageUri = imageUri + uris
            }
        }

    LaunchedEffect(imageUri) {
        Log.d("AddJournal", "Selected images: ${imageUri.size}")
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

        Box(Modifier
            .fillMaxSize()
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
                    onPickImage = { imagePicker.launch(arrayOf("image/*")) },
                    onRemoveImage = { uri ->
                        imageUri = imageUri - uri
                    },
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
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    },
                    onSave = {
                        viewmodel.insertJournal(
                            title = title,
                            description = description,
                            imageUri = imageUri.map { it.toString() },
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
                            imageUri = journal.imageUri.map { it.toUri() }
                            audioPath = journal.audioUri
                            latitude = journal.latitude
                            longitude = journal.longitude

                        }




                        AddEditContent(
                            title = title,
                            onTitleChange = { title = it },
                            description = description,
                            onDescriptionChange = { description = it },
                            imageUri = imageUri,
                            onPickImage = { imagePicker.launch(arrayOf("image/*")) },
                            onRemoveImage = { uri ->
                                imageUri = imageUri - uri
                            },
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
                                    imageUri = imageUri.map { it.toString() },
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
