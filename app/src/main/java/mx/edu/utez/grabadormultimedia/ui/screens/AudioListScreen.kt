package mx.edu.utez.grabadormultimedia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.edu.utez.grabadormultimedia.ViewModel.PlaybackViewModel
import mx.edu.utez.grabadormultimedia.ui.components.AudioCard
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff


// --- 16. Screen 2: Lista de Audio ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioListScreen(
    mediaViewModel: MediaViewModel,
    playbackViewModel: PlaybackViewModel
) {
    val audioList by mediaViewModel.allAudio.collectAsState()
    val isPlaying by playbackViewModel.isPlaying.collectAsState()
    val isAccelerometerOn by playbackViewModel.isAccelerometerEnabled.collectAsState()
    val currentVolume by playbackViewModel.currentVolume.collectAsState()
    var currentlyPlayingUri by remember { mutableStateOf<String?>(null) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Audios") },
                actions = {
                    Text(
                        text = "Vol: ${(currentVolume * 100).toInt()}%",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    IconButton(onClick = { playbackViewModel.toggleAccelerometer() }) {
                        Icon(
                            if (isAccelerometerOn) Icons.Default.Sensors else Icons.Default.SensorsOff,
                            contentDescription = "Control por Acelerómetro"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (audioList.isEmpty()) {
                item {
                    Text(
                        "No hay audios grabados.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            items(audioList) { item ->
                AudioCard(
                    item = item,
                    isPlaying = isPlaying && currentlyPlayingUri == item.uri,
                    onPlayClick = {
                        if (currentlyPlayingUri == item.uri) {
                            playbackViewModel.togglePlayPause()
                        } else {
                            playbackViewModel.playMedia(item.uri)
                            currentlyPlayingUri = item.uri
                        }
                    }
                )
            }
        }
    }
}