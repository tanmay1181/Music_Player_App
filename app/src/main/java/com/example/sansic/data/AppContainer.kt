package com.example.sansic.data

import android.content.Context
import com.example.sansic.player.AudioPlayer

interface AppContainer {
    val mp3Repository: Mp3Repository
    val audioPlayer: AudioPlayer
    val favoriteSongRepository: FavoriteSongRepository
    val playlistRepository: PlaylistsRepository
}

class AppDataContainer(private val context: Context): AppContainer{
    override val mp3Repository: Mp3Repository by lazy {
        OfflineMp3Repository(Mp3Database(context))
    }

    override val audioPlayer: AudioPlayer by lazy {
        AudioPlayer(context)
    }

    override val favoriteSongRepository: FavoriteSongRepository by lazy {
        OfflineFavoriteSongRepository(FavoriteSongDatabase.getDatabase(context).favoriteSongDao())
    }

    override val playlistRepository: PlaylistsRepository by lazy {
        OfflinePlaylistRepository(PlaylistsDatabase.getDatabase(context).playlistDao())
    }
}