package mx.edu.utez.grabadormultimedia.ViewModel

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat.getSystemService
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

    // --- Sensores (Acelerómetro) ---
    private val sensorManager: SensorManager =
        getSystemService(application, SensorManager::class.java) as SensorManager
    private var accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val _isAccelerometerEnabled = MutableStateFlow(false)
    val isAccelerometerEnabled: StateFlow<Boolean> = _isAccelerometerEnabled.asStateFlow()
