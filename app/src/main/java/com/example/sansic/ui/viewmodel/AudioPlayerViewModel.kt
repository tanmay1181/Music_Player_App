package com.example.sansic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.sansic.data.Mp3File
import com.example.sansic.player.AudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.sansic.ui.viewmodel.PlayMode.CurrentLoop
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class PlayMode(val mode: String) {
    CurrentLoop("CurrentLoop"),
    Shuffle("Shuffle");

    companion object {
        private val map = PlayMode.entries.associateBy(PlayMode::mode)
        fun fromString(mode: String) = map[mode] ?: CurrentLoop
    }
}

class AudioPlayerViewModel(audioPlayer: AudioPlayer): ViewModel() {
    private val _audioPlayer = audioPlayer
    private val exoPlayer = audioPlayer.exoPlayer

    private val _musicPlayerState = MutableStateFlow<MusicPlayerState>(MusicPlayerState())
    val musicPlayerState: StateFlow<MusicPlayerState> = _musicPlayerState

    private val lastUri = audioPlayer.getLastPlayedSong

    //need an update

    private val playerListener = object : Player.Listener{
        override fun onPlaybackStateChanged(playbackState: Int) {
            if(playbackState == Player.STATE_ENDED){
                playNext()
            }
        }
    }

    init {
        exoPlayer.addListener(playerListener)
        viewModelScope.launch {
            while (true) {
                _musicPlayerState.value = _musicPlayerState.value.copy(
                    duration = exoPlayer.duration,
                    playBackPosition = exoPlayer.currentPosition
                )
                delay(500L) // update every 0.5s
            }
        }
    }

    private fun setMedia(mp3File: Mp3File){
        val mediaItem = MediaItem.fromUri(mp3File.uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _audioPlayer.saveLastPlayedSong(_musicPlayerState.value.currentSong.uri.toString(),
            positionMs)
    }

    fun setCurrentMusicIndex(){
        val currentMp3Files = _musicPlayerState.value.currentMp3Files
        val currentSong = _musicPlayerState.value.currentSong

        _musicPlayerState.value = _musicPlayerState.value.copy(
            currentIndex = currentMp3Files.indexOf(currentSong)
        )
    }

     fun updateCurrentMp3Files(songList: List<Mp3File>){
        _musicPlayerState.value = _musicPlayerState.value.copy(
            currentMp3Files = songList
        )
    }

    fun onMusicClick(mp3File: Mp3File){
        setMedia(mp3File)

        _musicPlayerState.value = _musicPlayerState.value.copy(
            currentSong = mp3File,
            isMusicPlaying = true
        )
        setCurrentMusicIndex()
        _audioPlayer.saveLastPlayedSong(mp3File.uri.toString(), exoPlayer.currentPosition)
    }

    //Music Player Options

    fun playPauseSong(){
        if(exoPlayer.isPlaying){
            exoPlayer.pause()
            _musicPlayerState.value = _musicPlayerState.value.copy(
                isMusicPlaying = false
            )
        }
        else{
            exoPlayer.play()
            _musicPlayerState.value = _musicPlayerState.value.copy(
                isMusicPlaying = true
            )
        }
    }

    fun playNext() {
        val currentMp3List = _musicPlayerState.value.currentMp3Files
        var currentIndex = _musicPlayerState.value.currentIndex

        if(_musicPlayerState.value.playMode == PlayMode.CurrentLoop){
            if(currentIndex < currentMp3List.lastIndex){
                currentIndex++
            }
            else{
                currentIndex = 0
            }
        }
        else if(_musicPlayerState.value.playMode == PlayMode.Shuffle){
            currentIndex = (0..currentMp3List.lastIndex).random()
        }

        val currentSong = currentMp3List[currentIndex]
        _musicPlayerState.value = _musicPlayerState.value.copy(
            currentSong = currentSong,
            isMusicPlaying = true,
            currentIndex = currentIndex
        )
        setMedia(currentSong)
        _audioPlayer.saveLastPlayedSong(currentSong.uri.toString(), exoPlayer.currentPosition)
    }

    fun playPrevious() {
        val currentMp3List = _musicPlayerState.value.currentMp3Files
        var currentIndex = _musicPlayerState.value.currentIndex

        if(_musicPlayerState.value.playMode == PlayMode.CurrentLoop){
            if(currentIndex > 0){
                currentIndex--
            }
            else{
                currentIndex = currentMp3List.lastIndex
            }
        }
        else if(_musicPlayerState.value.playMode == PlayMode.Shuffle){
            currentIndex = (0..currentMp3List.lastIndex).random()
        }

        val currentSong = currentMp3List[currentIndex]
        _musicPlayerState.value = _musicPlayerState.value.copy(
            currentSong = currentSong,
            isMusicPlaying = true,
            currentIndex = currentIndex
        )
        setMedia(currentSong)
        _audioPlayer.saveLastPlayedSong(currentSong.uri.toString(), exoPlayer.currentPosition)
    }

    fun seekConstantSeconds(constantMs: Long){
        val nextPos = exoPlayer.currentPosition + constantMs
        exoPlayer.seekTo(nextPos)
    }

    fun changePlayMode(){
        val playMode = _musicPlayerState.value.playMode
        if(playMode == PlayMode.Shuffle){
            _musicPlayerState.value = _musicPlayerState.value.copy(
                playMode = PlayMode.CurrentLoop
            )
        }
        else{
            _musicPlayerState.value = _musicPlayerState.value.copy(
                playMode = PlayMode.Shuffle
            )
        }
    }

    //On App Closes

    override fun onCleared() {
        super.onCleared()
        _audioPlayer.saveLastPlayMode(_musicPlayerState.value.playMode)
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

}

data class MusicPlayerState(
    val currentSong: Mp3File = Mp3File(),
    val currentIndex: Int = 0,
    val currentMp3Files: List<Mp3File> = emptyList(),
    val duration: Long = 0L,
    val playBackPosition: Long = 0L,
    val isMusicPlaying: Boolean = false,
    val playMode: PlayMode = CurrentLoop,
)