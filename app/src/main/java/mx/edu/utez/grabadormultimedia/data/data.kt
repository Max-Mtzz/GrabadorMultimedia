package mx.edu.utez.grabadormultimedia.data

import androidx.constraintlayout.helper.widget.Flow
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

// --- 1. Entidad (El Modelo para la BD) ---
// Define el tipo de medio
enum class MediaType { AUDIO, IMAGE, VIDEO }
@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: String, // La URI del archivo (ej. content://...)
    val name: String,
    val date: Long, // Fecha de creación (timestamp)
    val duration: Long, // Duración en ms (0 para imágenes)
    val type: MediaType
)

// --- 2. DAO (Data Access Object) ---
@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(item: MediaItem)
    @Query("SELECT * FROM media_items WHERE type = :type ORDER BY date DESC")
    fun getMediaByType(type: MediaType): Flow<List<MediaItem>>
}

// --- 3. Database (inicialización y control de BD)---
@Database(entities = [MediaItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "media_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- 4. Repositorio (Control de datos guardados con Room) —
class MediaRepository(private val mediaDao: MediaDao) {
    fun getAllAudio(): Flow<List<MediaItem>> {
        return mediaDao.getMediaByType(MediaType.AUDIO)
    }
    fun getAllImages(): Flow<List<MediaItem>> {
        return mediaDao.getMediaByType(MediaType.IMAGE)
    }
    fun getAllVideos(): Flow<List<MediaItem>> {
        return mediaDao.getMediaByType(MediaType.VIDEO)
    }
    suspend fun insertMedia(item: MediaItem) {
        mediaDao.insertMedia(item)
    }
}

// --- 5. SettingsRepository (control del volumen guardando con DataStore) —
// Extensión para crear la instancia de DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SettingsRepository(context: Context) {
    private val dataStore = context.dataStore
    companion object {
        val VOLUME_KEY = floatPreferencesKey("volume_level")
        const val DEFAULT_VOLUME = 0.5f
    }
    // Obtener el volumen guardado
    val userVolume: Flow<Float> = dataStore.data.map { preferences ->
        preferences[VOLUME_KEY] ?: DEFAULT_VOLUME
    }
    // Guardar el volumen
    suspend fun saveVolume(volume: Float) {
        dataStore.edit { settings ->
            val clampedVolume = volume.coerceIn(0.0f, 1.0f)
            settings[VOLUME_KEY] = clampedVolume
        }
    }
}

// --- 6. AudioRecorder(ayudante para grabar audio) —
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
// Deprecated en API 31, pero necesario para < 31
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }
    fun start(outputFile: File) {
// Asegúrate de detener cualquier grabación anterior
        stop()
        recorder = createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            try {
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
// Liberar en caso de error de preparación
                recorder?.release()
                recorder = null
            }
        }
    }
    fun stop() {
        try {
            recorder?.stop()
            recorder?.release()
        } catch (e: Exception) {
// A veces stop() falla si se llama muy rápido
            e.printStackTrace()
            recorder?.release()
        }
        recorder = null
    }
}