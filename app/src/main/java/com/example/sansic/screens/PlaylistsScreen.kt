package com.example.sansic.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sansic.R
import com.example.sansic.data.Mp3File
import com.example.sansic.data.Playlist
import com.example.sansic.data.SerializableMp3File
import com.example.sansic.data.saveAlbumArtToCache
import com.example.sansic.data.toMp3File
import com.example.sansic.data.toSerializable
import com.example.sansic.screens.SongAddCard
import com.example.sansic.ui.theme.SanSicTheme
import com.example.sansic.ui.viewmodel.AudioPlayerViewModel
import com.example.sansic.ui.viewmodel.PlaylistViewModel
import java.io.File
import kotlin.toString

@Composable
fun PlaylistScreen(playlistViewModel: PlaylistViewModel,
                   onNavigateToCurrentPlaylist: () -> Unit,
                   modifier: Modifier){
    val playlistState = playlistViewModel.playlistState.collectAsState().value
    val allPlaylists = playlistState.playlists

    val showDialog = remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()){
        LazyVerticalGrid(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier.fillMaxSize(),
            columns = GridCells.Fixed(3),
        ) {
            items(allPlaylists) { playlist ->
                PlaylistCard(
                    playlist,
                    {
                        playlistViewModel.setCurrentPlaylist(it)
                        onNavigateToCurrentPlaylist()
                    },
                    modifier)
            }
        }

        FloatingActionButton(
            onClick = {
                showDialog.value = true
            },
            containerColor = Color(0xFF6200EE),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Playlist")
        }

        if(showDialog.value){
            AddPlaylistDialog(
                onConfirm = {
                    playlistViewModel.insertPlaylist(it)
                },
                dialogClose = {showDialog.value = false},
                modifier = modifier
            )
        }
    }
}

@Composable
fun PlaylistCard(playlist: Playlist,
                 onPlaylistClick:(id: Int) ->Unit,
                 modifier: Modifier){
    val playlistId = playlist.id

    Card(modifier = modifier
        .height(140.dp)
        .clickable(
            onClick = {
                onPlaylistClick(playlistId)
            }
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(R.drawable.baseline_audio_file_24),
                contentDescription = null,
                modifier = modifier
                    .weight(1f)
                    .fillMaxSize())
            Row(modifier = modifier
                .weight(0.3f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = playlist.name, fontSize = 12.sp)
                Text(text = playlist.mp3Files.size.toString() + " Songs", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PlaylistsSongView(playlistViewModel: PlaylistViewModel,
                      audioPlayerViewModel: AudioPlayerViewModel,
                      modifier: Modifier){
    val currentPlaylist = playlistViewModel.playlistState.collectAsState().value.currentPlaylist
    val currentSong = audioPlayerViewModel.musicPlayerState.collectAsState().value.currentSong
    val currentPlaylistSongs = currentPlaylist.mp3Files

    val showBottomSheet = remember { mutableStateOf(false) }
    val mp3Files = playlistViewModel.allMp3Files.collectAsState().value

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn {
            items(currentPlaylistSongs) { serializeFile ->
                val mp3File = serializeFile.toMp3File()
                Mp3Card(
                    mp3File = mp3File,
                    mp3File == currentSong,
                    {},
                    false,
                    {},
                    {},
                    modifier
                )
            }
        }

        FloatingActionButton(
            onClick = {
                showBottomSheet.value = true
            },
            containerColor = Color(0xFF6200EE),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Playlist")
        }

        if(showBottomSheet.value){
            AddSongToPlaylistSheet(
                mp3List = mp3Files,
                onDismissRequest = {
                    showBottomSheet.value = false
                },
                {
                    val updatedList = currentPlaylistSongs + it
                    val updatedPlaylist = currentPlaylist.copy(mp3Files = updatedList)
                    playlistViewModel.updatePlaylist(updatedPlaylist)
                },
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSongToPlaylistSheet(mp3List: List<Mp3File>,
                           onDismissRequest: () -> Unit,
                           addSongsToPlaylist: (List<SerializableMp3File>) -> Unit,
                           modifier: Modifier){
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val currentlySelectedSongs = remember { mutableStateOf(emptyList<SerializableMp3File>()) }

    ModalBottomSheet(
        onDismissRequest = {onDismissRequest()},
        sheetState = sheetState,
        containerColor = Color(0xFF9A0175)
    ) {
        Box(modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 400.dp, max = 600.dp)){
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {

                items(mp3List) { mp3 ->
                    val serializeMp3 = mp3.toSerializable()
                    val isSelected = currentlySelectedSongs.value.any {
                        serializeMp3.uriString == it.uriString
                    }
                    SongAddCard(
                        mp3,
                        {
                            if(isSelected){
                                currentlySelectedSongs.value -= serializeMp3
                            }
                            else{
                                currentlySelectedSongs.value += serializeMp3
                            }
                        },
                        isSelected,
                        modifier)
                }
            }

            if(currentlySelectedSongs.value.isNotEmpty()){
                Row(modifier = modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent)
                    .align(alignment = Alignment.BottomEnd),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    FloatingActionButton(
                        onClick = {
                            addSongsToPlaylist(currentlySelectedSongs.value)
                            onDismissRequest()
                        },
                        shape = RoundedCornerShape(32.dp),
                        containerColor = Color(0xFF6200EE),
                        contentColor = Color.White,
                    ) {
                        Text("Add")
                    }

                    FloatingActionButton(
                        onClick = {onDismissRequest()},
                        shape = RoundedCornerShape(32.dp),
                        containerColor = Color(0xFF6200EE),
                        contentColor = Color.White,
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }

    }
}

@Composable
fun SongAddCard(mp3File: Mp3File,
                selectOrDeSelectSong: () -> Unit,
                isSelected: Boolean,
                modifier: Modifier){
    val context = LocalContext.current

    val imageFileState = produceState<File?>(initialValue = null, mp3File.uri) {
        val cacheDir = File(context.cacheDir, "album_art")
        val fileName = "album_${mp3File.uri.toString().hashCode()}.png"
        val cachedFile = File(cacheDir, fileName)

        value = if (cachedFile.exists()) cachedFile
        else saveAlbumArtToCache(context, mp3File.uri)
    }

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
            .clickable(
                onClick = {
                    selectOrDeSelectSong()
                }
            )
    ) {
        AsyncImage(
            model = imageFileState.value ?: R.drawable.baseline_music_note_24,
            contentDescription = null,
            modifier = modifier
                .size(40.dp)
                .clip(shape = RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = modifier.weight(1f)
        ) {
            Text(text = mp3File.title,
                color = Color.White,
                softWrap = false,
                maxLines = 1,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .basicMarquee())
            Text(text = mp3File.artist ,
                color = Color.White,
                fontSize = 12.sp,
                softWrap = false,
                modifier = modifier
                    .basicMarquee())
        }

        SelectedIndicator(isSelected)
    }
}

@Composable
fun SelectedIndicator(isSelected: Boolean){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color.Cyan else Color.LightGray)
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun AddPlaylistDialog(onConfirm: (Playlist) -> Unit,
                      dialogClose: () -> Unit,
                      modifier: Modifier){
    val playlistName = remember {mutableStateOf("") }

    AlertDialog(icon = {  },
        title = {
            Text(text = "Choose a Sort Type")
        },
        text = {
            TextField(
                value = playlistName.value,
                onValueChange = {playlistName.value = it},
            )
        },
        onDismissRequest = {
            dialogClose()
        },
        confirmButton = {
            Button(
                onClick = {
                    val playlist = Playlist(name = playlistName.value, mp3Files = emptyList())
                    onConfirm(playlist)
                    dialogClose()
                }
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    dialogClose()
                }
            ) {
                Text(text = "Cancel")
            }
        })
}

@Composable
@Preview(showBackground = true)
fun PlaylistPreview(){
    SanSicTheme {
        Row(horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()) {
            FloatingActionButton(
                onClick = {

                },
                shape = RoundedCornerShape(32.dp),
                containerColor = Color(0xFF6200EE),
                contentColor = Color.White,
            ) {
                Text("Add")
            }

            FloatingActionButton(
                onClick = {},
                shape = RoundedCornerShape(32.dp)
            ) {
                Text("Cancel")
            }
        }
    }
}