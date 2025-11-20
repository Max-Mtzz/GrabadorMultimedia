package mx.edu.utez.grabadormultimedia.ViewModel

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.lifecycle.AndroidViewModel
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaybackViewModel(
    application: Application,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application), SensorEventListener {
    // --- ExoPlayer ---
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
