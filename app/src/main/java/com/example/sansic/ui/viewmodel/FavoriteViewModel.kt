package com.example.sansic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sansic.data.FavoriteSong
import com.example.sansic.data.FavoriteSongRepository
import com.example.sansic.data.Mp3File
import com.example.sansic.data.toSerializable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteSongRepository: FavoriteSongRepository): ViewModel(){

    val favoriteState: StateFlow<FavoriteState> =
        favoriteSongRepository.getAllFavoriteSong().map {
            FavoriteState(it)
        }.stateIn(
            initialValue = FavoriteState(),
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L)
        )

    fun insertSong(mp3File: Mp3File){
        viewModelScope.launch {
            val song = FavoriteSong(file = mp3File.toSerializable(), bitMap = mp3File.icon)
            favoriteSongRepository.insertSong(song)
        }
    }

    fun deleteSong(mp3File: Mp3File){
        viewModelScope.launch {
            val favoriteSongs = favoriteSongRepository.getAllFavoriteSong().first()
            val songToDelete = favoriteSongs.find {
                it.file.uriString == mp3File.uri.toString()
            }
            if(songToDelete != null){
                favoriteSongRepository.deleteSong(songToDelete)
            }
        }
    }

}

data class FavoriteState(
    val favoriteSongsList: List<FavoriteSong> = emptyList()
)