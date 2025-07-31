package com.example.sansic.data

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class Mp3Database(val context: Context) {
    suspend fun getAllMp3Files(sortOrderType: String,
                               sortOrder: String): List<Mp3File> =
        withContext(Dispatchers.IO) {
            val mp3Files = mutableListOf<Mp3File>()
            val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val selectionArgs = arrayOf("audio/mpeg")

            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                "$sortOrderType COLLATE NOCASE $sortOrder"
            )?.use {cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                //val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)

                while (cursor.moveToNext()){
                    //val path = cursor.getString(dataCol)
                    val id = cursor.getLong(idColumn)
                    val duration = cursor.getLong(durationCol)
                    val fileName = cursor.getString(nameColumn) ?: continue

                    if(duration < 30000) continue
                    if(!fileName.endsWith(".mp3", ignoreCase = true)) continue

                    val album = cursor.getString(albumCol) ?: "Unknown Album"
                    val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                    val dateAdded = cursor.getInt(dateCol)
                    val dateAddedInMillis = dateAdded * 1000L
                    val contentUri = ContentUris.withAppendedId(collection, id)
                    //val icon = extractMp3IconSafely(context, contentUri)?.asImageBitmap()
                    val file = Mp3File(contentUri, fileName, album, artist, duration, dateAddedInMillis, sizeCol * 1000L)
                    mp3Files.add(file)
                }
            }
            mp3Files
        }

    suspend fun getSortedMp3FilesByDate(sortOrder: String): List<Mp3File> =
        getAllMp3Files(MediaStore.Audio.Media.DATE_ADDED, sortOrder)

    suspend fun getSortedMp3FilesByName(sortOrder: String): List<Mp3File> =
        getAllMp3Files(MediaStore.Audio.Media.DISPLAY_NAME, sortOrder)

    suspend fun getSortedMp3FilesBySize(sortOrder: String): List<Mp3File> =
        getAllMp3Files(MediaStore.Audio.Media.SIZE, sortOrder)

    suspend fun getSortedMp3FilesByDuration(sortOrder: String): List<Mp3File> =
        getAllMp3Files(MediaStore.Audio.Media.DURATION, sortOrder)

    fun getSameAlbumMusic(mp3Files: List<Mp3File>): Map<String, List<Mp3File>> =
        mp3Files.groupBy { it.album }

    fun getSameArtistMusic(mp3Files: List<Mp3File>): Map<String, List<Mp3File>> =
        mp3Files.groupBy { it.artist }
}