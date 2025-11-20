package mx.edu.utez.grabadormultimedia.ui

import android.app.Application
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.edu.utez.grabadormultimedia.ViewModel.MediaViewModel
import mx.edu.utez.grabadormultimedia.ViewModel.MediaViewModelFactory
import mx.edu.utez.grabadormultimedia.ViewModel.PlaybackViewModel
import mx.edu.utez.grabadormultimedia.ViewModel.PlaybackViewModelFactory
import mx.edu.utez.grabadormultimedia.data.AudioRecorder
import mx.edu.utez.grabadormultimedia.ui.screens.AudioListScreen
import mx.edu.utez.grabadormultimedia.ui.screens.ImageListScreen
import mx.edu.utez.grabadormultimedia.ui.screens.RecordingScreen
import mx.edu.utez.grabadormultimedia.ui.screens.VideoListScreen
import mx.edu.utez.grabadormultimedia.ui.screens.VideoPlayerScreen
import mx.edu.utez.grabadormultimedia.ui.theme.AppBottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as Application
// Factories para los ViewModels
    val mediaViewModel: MediaViewModel = viewModel(
        factory = MediaViewModelFactory(application)
    )
    val playbackViewModel: PlaybackViewModel = viewModel(
        factory = PlaybackViewModelFactory(application)
    )
    val audioRecorder = AudioRecorder(LocalContext.current)
    Scaffold(
        bottomBar = {
            AppBottomNavBar(navController = navController)
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Recording.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Recording.route) {
                RecordingScreen(mediaViewModel = mediaViewModel, audioRecorder =audioRecorder    )
            }
            composable(Screen.AudioList.route) {
                AudioListScreen(
                    mediaViewModel = mediaViewModel,
                    playbackViewModel = playbackViewModel
                )
            }
            composable(Screen.ImageList.route) {
                ImageListScreen(mediaViewModel = mediaViewModel)
            }

            composable(Screen.VideoList.route) {
                VideoListScreen(
                    mediaViewModel = mediaViewModel,
                    navController = navController
                )
            }
            composable(
                route = Screen.VideoPlayer.route,
                arguments = listOf(
                    navArgument("uri") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                val uriString = backStackEntry.arguments?.getString("uri")
                val uri = uriString?.let { Uri.parse(it) }

                if (uri != null) {
                    VideoPlayerScreen(
                        uri = uri,
                        playbackViewModel = playbackViewModel
                    )
                }
            }
        }
    }
}
