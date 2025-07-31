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
import com.example.sansic.data.toSerializable
import com.example.sansic.ui.viewmodel.AppViewModelProvider
import com.example.sansic.ui.viewmodel.AudioPlayerViewModel
import com.example.sansic.ui.viewmodel.FavoriteViewModel
import com.example.sansic.ui.viewmodel.RecentlyAddedViewModel

@Composable
fun RecentlyAddedMp3Screen(recentlyAddedViewModel: RecentlyAddedViewModel = viewModel(factory = AppViewModelProvider.Factory),
                           audioPlayerViewModel: AudioPlayerViewModel,
                           favoriteViewModel: FavoriteViewModel,
                           modifier: Modifier){

    val recentlyAddedMp3Files = recentlyAddedViewModel.recentlyAddedMp3s.collectAsState().value
    val currentSong = audioPlayerViewModel.musicPlayerState.collectAsState().value.currentSong
    val favoriteMp3Files = favoriteViewModel.favoriteState.collectAsState().value.favoriteSongsList

    Surface(modifier = Modifier
        .fillMaxSize(),
        color = Color.Transparent,
    ){
        LazyColumn {
            items(recentlyAddedMp3Files) { mp3File ->
                val isFavorite = favoriteMp3Files.any { it.file.uriString == mp3File.uri.toString() }
                Mp3Card(
                    mp3File = mp3File,
                    isPlaying = currentSong.uri == mp3File.uri,
                    onSongClick = {
                        audioPlayerViewModel.onMusicClick(mp3File)
                        audioPlayerViewModel.updateCurrentMp3Files(recentlyAddedMp3Files)
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