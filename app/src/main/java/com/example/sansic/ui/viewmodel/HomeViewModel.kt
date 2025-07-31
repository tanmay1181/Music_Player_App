package com.example.sansic.ui.viewmodel


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sansic.data.Mp3File
import com.example.sansic.data.Mp3Repository
import com.example.sansic.player.AudioPlayer
import com.example.sansic.screens.HeaderType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

enum class SortType(val type: String){
    DateAdded(MediaStore.Audio.Media.DATE_ADDED),
    Size(MediaStore.Audio.Media.SIZE),
    Duration(MediaStore.Audio.Media.DURATION),
    Name(MediaStore.Audio.Media.DISPLAY_NAME);
}

class HomeViewModel(private val mp3Repository: Mp3Repository,
                    private val audioPlayer: AudioPlayer): ViewModel(){

    private val _homeState = MutableStateFlow<HomeState>(HomeState())
    val homeState: StateFlow<HomeState> = _homeState

    private val allMp3Files = MutableStateFlow<List<Mp3File>>(emptyList())
    private val iconCache = mutableMapOf<Uri, Bitmap?>()

    fun populateLibrary(){
        viewModelScope.launch {
            allMp3Files.value = mp3Repository.getAllMp3FilesByName("ASC")
            initializeHomeState()
        }
    }

    fun initializeHomeState(){
        viewModelScope.launch(Dispatchers.IO) {
            val sortType = audioPlayer.getLastSortType
            val isAscending = audioPlayer.isSortAscending
            val sortOrder = if(isAscending) "ASC" else "DESC"
            allMp3Files.value = mp3Repository.getAllMp3Files(sortType.type, sortOrder)
            val oneWeekAgoAdded = System.currentTimeMillis() - 120 * 24 * 60 * 60 * 1000L
            val recentlyAddedMp3Files = allMp3Files.value.filter { it.dateAdded >= oneWeekAgoAdded }
                .sortedByDescending { it.dateAdded }

            _homeState.value = HomeState(
                headerType = HeaderType.Songs,
                mp3Files = allMp3Files.value,
                mp3Map = emptyMap(),
                sortType = sortType,
                recentlyAddedMp3Files = recentlyAddedMp3Files
            )
        }
    }

    fun setMp3FilesFromFolders(keyName: String){
        val newMp3Files = _homeState.value.mp3Map[keyName]
        if(newMp3Files != null){
            updateMp3Files(newMp3Files)
        }
    }

    fun updateMp3Files(newMp3Files: List<Mp3File>){
        _homeState.value = _homeState.value.copy(
            mp3Files = newMp3Files
        )
    }

    fun sortMp3Files(sortType: SortType) {

        if(sortType == _homeState.value.sortType) {
            _homeState.value = _homeState.value.copy(
                isAscending = !_homeState.value.isAscending
            )
        }
        else _homeState.value = _homeState.value.copy()

        _homeState.value = _homeState.value.copy(
            sortType = sortType
        )

        viewModelScope.launch {
            val sortOrder = if(_homeState.value.isAscending) "ASC" else "DESC"
            val sortedList = when (sortType) {
                SortType.DateAdded -> {
                    mp3Repository.getAllMp3FilesByDate(sortOrder)
                }

                SortType.Size -> {
                    mp3Repository.getAllMp3FilesBySize(sortOrder)
                }

                SortType.Name -> {
                    mp3Repository.getAllMp3FilesByName(sortOrder)
                }

                SortType.Duration -> {
                    mp3Repository.getAllMp3FilesByDuration(sortOrder)
                }
            }

            allMp3Files.value = sortedList
            updateMp3Files(allMp3Files.value)
            audioPlayer.saveSortSettings(sortType, _homeState.value.isAscending)
        }
    }

    fun updateHeaderType(headerType: HeaderType){
        _homeState.value = _homeState.value.copy(
            headerType = headerType
        )

        var newMp3Map = emptyMap<String, List<Mp3File>>()

        when(headerType){
            HeaderType.Songs -> {
                newMp3Map = emptyMap()
                updateMp3Files(allMp3Files.value)
            }

            HeaderType.Albums -> {
                newMp3Map = mp3Repository.getSameAlbumMusic(allMp3Files.value)
                updateMp3Files(emptyList())
            }

            HeaderType.Artists -> {
                newMp3Map = mp3Repository.getSameArtistMusic(allMp3Files.value)
                updateMp3Files(emptyList())
            }

        }

        _homeState.value = _homeState.value.copy(
            mp3Map = newMp3Map
        )
    }
}

data class HomeState(
    val headerType: HeaderType = HeaderType.Songs,
    val mp3Files: List<Mp3File> = emptyList(),
    val mp3Map: Map<String, List<Mp3File>> = emptyMap(),
    val sortType: SortType = SortType.DateAdded,
    val isAscending: Boolean = true,
    val recentlyAddedMp3Files: List<Mp3File> = emptyList()
)