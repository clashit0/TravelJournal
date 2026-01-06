package com.abhinav.traveljournal.common

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import okhttp3.Address

@Composable
fun AddEditContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    imageUri: Uri?,
    onPickImage: () -> Unit,
    isRecording: Boolean,
    onRecordClick: () -> Unit,
    latitude: Double?,
    longitude: Double?,
    onLocationClick: () -> Unit,
    onSave: () -> Unit,
    audioPath: String?,
    isEdit: Boolean
){

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )



        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = onPickImage) {
            Text("Add Photo")
        }
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Button(onClick = onRecordClick) {
            Text(if (isRecording) "Stop Recording" else "Start Recording")
            if (isEdit && audioPath != null && !isRecording) {
                Text(
                    text = "🎤 Audio recording attached",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Button(onClick = onLocationClick) {
            Text(if (latitude != null && longitude != null) "Location Saved" else "Add Location")
        }

        Button(onClick = onSave) {
            Text(if (isEdit) "Update Journal" else "Save Journal")
        }
    }
}