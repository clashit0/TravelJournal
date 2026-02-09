package com.abhinav.traveljournal.presentation.screens


import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.abhinav.traveljournal.common.AudioPlayer
import com.abhinav.traveljournal.presentation.JournalViewmodel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalDetailScreen(
    journalId: Int,
    viewmodel: JournalViewmodel
){
    val journal = viewmodel.getJournalById(journalId)
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var showPlayer by remember { mutableStateOf(false) }
    val context = LocalContext.current




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



                if (journal.imageUri.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(journal.imageUri) { uriString ->
                            Image(
                                painter = rememberAsyncImagePainter(uriString.toUri()),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(180.dp)
                                    .aspectRatio(1f)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable {
                                        selectedImage = uriString
                                    }
                            )
                        }
                    }
                }



                journal.audioUri?.let { path->
                    if (showPlayer){
                        AudioPlayer(audioPath = path)
                    }


                    if(!showPlayer){
                        Button(onClick = {
                            showPlayer = true
                        }) {
                            Text("Play Audio")
                        }
                    }

                }

                if (journal.latitude != null && journal.longitude != null){
                    val lat = journal.latitude
                    val lon = journal.longitude
                    val locationUri = "geo:$lat,$lon?q=$lat,$lon".toUri()
                    val intent = Intent(Intent.ACTION_VIEW, locationUri)
                    intent.setPackage("com.google.android.apps.maps")
                    Button(onClick = {
                        context.startActivity(intent)
                    }) {
                        Text("Open location")
                    }
                }

                Text(
                    text = "Created at: ${Date(journal.createdAt)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        //  FULL SCREEN IMAGE PREVIEW DIALOG
        selectedImage?.let { uriString ->
            Dialog(onDismissRequest = { selectedImage = null }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .clickable { selectedImage = null },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(uriString.toUri()),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }



    }
}