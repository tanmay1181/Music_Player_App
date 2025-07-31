package com.example.sansic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sansic.data.Mp3File
import com.example.sansic.data.Mp3Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecentlyAddedViewModel(private val mp3Repository: Mp3Repository): ViewModel() {
    private val _recentlyAddedMp3s = MutableStateFlow<List<Mp3File>>(emptyList())
    val recentlyAddedMp3s: StateFlow<List<Mp3File>> = _recentlyAddedMp3s

    init {
        initializeRecentlyAddedMp3s()
    }

    fun initializeRecentlyAddedMp3s(){
        viewModelScope.launch {
            val oneWeekAgoAdded = System.currentTimeMillis() - 120 * 24 * 60 * 60 * 1000L
            val allMp3Files = mp3Repository.getAllMp3FilesByDate("ASC")
            val recentlyAddedMp3Files = allMp3Files.filter { it.dateAdded >= oneWeekAgoAdded }
                .sortedByDescending { it.dateAdded }
            _recentlyAddedMp3s.value = recentlyAddedMp3Files
        }
    }
}