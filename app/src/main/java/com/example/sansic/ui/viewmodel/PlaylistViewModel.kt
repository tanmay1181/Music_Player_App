package com.example.sansic.ui.viewmodel

import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.example.sansic.data.Mp3File
import com.example.sansic.data.Mp3Repository
import com.example.sansic.data.Playlist
import com.example.sansic.data.PlaylistsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaylistViewModel(private  val playlistRepository: PlaylistsRepository,
                        mp3Repository: Mp3Repository): ViewModel() {
    private val _playlistState = MutableStateFlow<PlaylistState>(PlaylistState())
    val playlistState: StateFlow<PlaylistState> = _playlistState

    val allMp3Files = MutableStateFlow(emptyList<Mp3File>())

    init {
        setPlaylists()
        viewModelScope.launch {
            allMp3Files.value = mp3Repository
                .getAllMp3Files(MediaStore.Audio.Media.DATE_ADDED, "ASC")
        }
    }

    fun insertPlaylist(playlist: Playlist){
        viewModelScope.launch {
            playlistRepository.insertPlaylist(playlist)
        }
    }

    fun updatePlaylist(playlist: Playlist){
        viewModelScope.launch {
            playlistRepository.updatePlaylist(playlist)
        }
    }

    fun setPlaylists(){
        viewModelScope.launch {
            playlistRepository.getAllPlaylist().collect {playlists ->
                _playlistState.update { it.copy(playlists = playlists) }
            }
        }
    }

    fun setCurrentPlaylist(id: Int){
        viewModelScope.launch {
            playlistRepository.getPlaylist(id).collect { playlist ->
                playlist?.let {
                    _playlistState.update { it.copy(currentPlaylist = playlist) }
                }
            }
        }
    }
}

data class PlaylistState(
    val playlists: List<Playlist> = emptyList(),
    val currentPlaylist: Playlist = Playlist(name = "playlist", mp3Files = emptyList())
)