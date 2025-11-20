package mx.edu.utez.grabadormultimedia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mx.edu.utez.grabadormultimedia.ui.components.VideoCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(
    mediaViewModel: MediaViewModel,
    navController: NavController
) {
    val videoList by mediaViewModel.allVideos.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mis Videos") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (videoList.isEmpty()) {
                item {
                    Text(
                        "No hay videos grabados.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            items(videoList) { item ->

                VideoCard(
                    item = item,
                    onClick = {
// Navegar al reproductor. La URI debe ser codificada.
                        val encodedUri = Uri.encode(item.uri)
                        navController.navigate(Screen.VideoPlayer.createRoute(encodedUri))
                    }
                )
            }
        }
    }
}