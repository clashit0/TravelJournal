package com.abhinav.traveljournal.common

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun AddEditContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    imageUri: List<Uri>,
    onPickImage: () -> Unit,
    isRecording: Boolean,
    onRecordClick: () -> Unit,
    latitude: Double?,
    longitude: Double?,
    onLocationClick: () -> Unit,
    onSave: () -> Unit,
    audioPath: String?,
    isEdit: Boolean
) {

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
            Text("Add Photos")
        }

        if (imageUri.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(imageUri) { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .height(120.dp)
                            .aspectRatio(1f)
                    )
                }
            }
        }


        Button(onClick = onRecordClick) {
            Text(if (isRecording) "Stop Recording" else "Start Recording")
        }

        if (isEdit && audioPath != null && !isRecording) {
            Text(
                text = "🎤 Audio recording attached",
                style = MaterialTheme.typography.bodyMedium
            )
        }


        Button(onClick = onLocationClick) {
            Text(
                if (latitude != null && longitude != null)
                    "Location Saved"
                else
                    "Add Location"
            )
        }


        Button(onClick = onSave) {
            Text(if (isEdit) "Update Journal" else "Save Journal")
        }
    }
}
