package com.example.sansic.data

import kotlinx.coroutines.flow.Flow


interface Mp3Repository {
    suspend fun getAllMp3Files(sortType: String, sortOrder: String): List<Mp3File>
    suspend fun getAllMp3FilesByDate(sortOrder: String): List<Mp3File>
    suspend fun getAllMp3FilesByName(sortOrder: String): List<Mp3File>
    suspend fun getAllMp3FilesBySize(sortOrder: String): List<Mp3File>
    suspend fun getAllMp3FilesByDuration(sortOrder: String): List<Mp3File>

    fun getSameAlbumMusic(mp3Files: List<Mp3File>): Map<String, List<Mp3File>>
    fun getSameArtistMusic(mp3Files: List<Mp3File>): Map<String, List<Mp3File>>
}

interface FavoriteSongRepository {
    suspend fun insertSong(song: FavoriteSong)
    suspend fun deleteSong(song: FavoriteSong)
    fun getAllFavoriteSong(): Flow<List<FavoriteSong>>
    //fun getFavoriteSong(file: Mp3File): Flow<FavoriteSong>
}

interface PlaylistsRepository {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    fun getAllPlaylist(): Flow<List<Playlist>>
    fun getPlaylist(id: Int): Flow<Playlist?>
}