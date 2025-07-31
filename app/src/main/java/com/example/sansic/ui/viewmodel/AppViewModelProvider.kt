package com.example.sansic.ui.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sansic.SanSicApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(sanSicApplication().container.mp3Repository,
                sanSicApplication().container.audioPlayer)
        }

        initializer {
            SearchViewModel(sanSicApplication().container.mp3Repository)
        }

        initializer {
            AudioPlayerViewModel(sanSicApplication().container.audioPlayer
                )
        }

        initializer {
            FavoriteViewModel(sanSicApplication().container.favoriteSongRepository)
        }

        initializer {
            RecentlyAddedViewModel(sanSicApplication().container.mp3Repository)
        }

        initializer {
            PlaylistViewModel(sanSicApplication().container.playlistRepository,
                sanSicApplication().container.mp3Repository)
        }
    }
}

fun CreationExtras.sanSicApplication(): SanSicApplication =
    (this[APPLICATION_KEY] as SanSicApplication)