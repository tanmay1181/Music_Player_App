package com.example.sansic.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sansic.data.toMp3File
import com.example.sansic.ui.viewmodel.AppViewModelProvider
import com.example.sansic.ui.viewmodel.AudioPlayerViewModel
import com.example.sansic.ui.viewmodel.FavoriteViewModel

@Composable
fun FavoriteScreen(audioPlayerViewModel: AudioPlayerViewModel,
                   favoriteViewModel: FavoriteViewModel,
                   modifier: Modifier){
    val favoriteSongs = favoriteViewModel.favoriteState.collectAsState().value.favoriteSongsList
    val musicPlayerState = audioPlayerViewModel.musicPlayerState.collectAsState().value
    val currentSong = musicPlayerState.currentSong
    val favoriteMp3Files = favoriteSongs.map {
        it.file.toMp3File()
    }

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = Color.Transparent
    ) {
        LazyColumn {
            items(favoriteSongs) { favoriteSong ->
                val mp3File = favoriteSong.file.toMp3File()
                mp3File.icon = favoriteSong.bitMap
                val isFavorite = favoriteSongs.any{it.file.uriString == mp3File.uri.toString()}
                Mp3Card(
                    mp3File = mp3File,
                    isPlaying = currentSong.uri == mp3File.uri,
                    onSongClick = {
                        audioPlayerViewModel.onMusicClick(mp3File)
                        audioPlayerViewModel.updateCurrentMp3Files(favoriteMp3Files)
                    },
                    isFavorite = isFavorite,
                    addToFavorites = {
                        favoriteViewModel.insertSong(mp3File)
                    },
                    removeFromFavorites = {
                        favoriteViewModel.deleteSong(mp3File)
                    },
                    modifier = modifier
                )
            }
        }
    }

}