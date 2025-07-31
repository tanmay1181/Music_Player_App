package com.example.sansic.data

import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class OfflineMp3Repository(val mp3Database: Mp3Database): Mp3Repository {
    override suspend fun getAllMp3Files(sortType: String, sortOrder: String): List<Mp3File> =
        mp3Database.getAllMp3Files(sortType, sortOrder)

    override suspend fun getAllMp3FilesByDate(sortOrder: String): List<Mp3File> = mp3Database.getSortedMp3FilesByDate(sortOrder)
    override suspend fun getAllMp3FilesByName(sortOrder: String): List<Mp3File> = mp3Database.getSortedMp3FilesByName(sortOrder)
    override suspend fun getAllMp3FilesBySize(sortOrder: String): List<Mp3File> = mp3Database.getSortedMp3FilesBySize(sortOrder)
    override suspend fun getAllMp3FilesByDuration(sortOrder: String): List<Mp3File> = mp3Database.getSortedMp3FilesByDuration(sortOrder)

    override fun getSameAlbumMusic(mp3Files: List<Mp3File>): Map<String, List<Mp3File>> = mp3Database.getSameAlbumMusic(mp3Files)
    override fun getSameArtistMusic(mp3Files: List<Mp3File>): Map<String, List<Mp3File>> = mp3Database.getSameArtistMusic(mp3Files)

}

class OfflineFavoriteSongRepository(val favoriteSongDao: FavoriteSongDao): FavoriteSongRepository{
    override suspend fun insertSong(song: FavoriteSong) = favoriteSongDao.insertSong(song)

    override suspend fun deleteSong(song: FavoriteSong) = favoriteSongDao.deleteSong(song)

    override fun getAllFavoriteSong(): Flow<List<FavoriteSong>> = favoriteSongDao.getAllFavoriteSongs()

    //override fun getFavoriteSong(file: Mp3File): Flow<FavoriteSong> = favoriteSongDao.getFavoriteSong(file)
}

class OfflinePlaylistRepository(val playlistDao: PlaylistDao): PlaylistsRepository {
    override suspend fun insertPlaylist(playlist: Playlist) = playlistDao.createPlaylist(playlist)

    override suspend fun deletePlaylist(playlist: Playlist) = playlistDao.deletePlaylist(playlist)

    override suspend fun updatePlaylist(playlist: Playlist) = playlistDao.updatePlaylist(playlist)

    override fun getAllPlaylist(): Flow<List<Playlist>> = playlistDao.getAllPlaylists()

    override fun getPlaylist(id: Int): Flow<Playlist?> = playlistDao.getPlaylist(id)
}