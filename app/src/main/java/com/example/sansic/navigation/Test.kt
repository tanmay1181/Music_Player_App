import android.content.Context
import android.provider.MediaStore
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Mp3File(
    val title: String,
    val artist: String,
)

class Mp3Repository(private val context: Context) {

    fun getAllMp3Files(): List<Mp3File> {
        val mp3Files = mutableListOf<Mp3File>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.IS_MUSIC
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

        context.contentResolver.query(
            collection, projection, selection, null, null
        )?.use { cursor ->
            val titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            while (cursor.moveToNext()) {
                val title = cursor.getString(titleIndex) ?: continue
                val artist = cursor.getString(artistIndex) ?: "Unknown"
                mp3Files.add(Mp3File(title, artist))
            }
        }
        return mp3Files
    }
}

class AudioViewModel(private val repo: Mp3Repository) : ViewModel() {

    private val _songs = MutableStateFlow<List<Mp3File>>(emptyList())
    val songs: StateFlow<List<Mp3File>> = _songs

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.getAllMp3Files()
            _songs.value = result
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MusicScreen(viewModel: AudioViewModel) {
    val permissionState = rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    val songs = viewModel.songs.collectAsState().value

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            viewModel.loadSongs()
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Music List") })
    }) {innerPadding ->
        if (songs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Songs Found or Permission Not Granted")
            }
        } else {
            LazyColumn {
                items(songs) { song ->
                    Column(Modifier.padding(8.dp)) {
                        Text(song.title, style = MaterialTheme.typography.bodyMedium)
                        Text(song.artist, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

}

