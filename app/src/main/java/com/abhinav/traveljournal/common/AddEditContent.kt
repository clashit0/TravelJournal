package com.abhinav.traveljournal.common

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onRemoveImage: (Uri) -> Unit,
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(imageUri) { uri ->
                    uri.let {
                        Box {
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(120.dp)
                                    .aspectRatio(1f)
                                    .clip(MaterialTheme.shapes.medium)
                            )


                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.error,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .clickable {
                                        onRemoveImage(it) //  remove callback
                                    }
                            ) {
                                Text(
                                    text = "✕",
                                    color = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
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
