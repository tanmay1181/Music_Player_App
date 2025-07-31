package com.example.sansic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.sansic.SanSicApplication
import com.example.sansic.data.Mp3File
import com.example.sansic.data.Mp3Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(mp3Repository: Mp3Repository): ViewModel() {
    private val _mp3Files = MutableStateFlow<List<Mp3File>>(emptyList())
    val mp3Files: StateFlow<List<Mp3File>> = _mp3Files

//    init {
//        viewModelScope.launch {
//            _mp3Files.value = mp3Repository.getAllMp3Files("ASC")
//        }
//    }
}