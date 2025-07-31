package com.example.sansic.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import java.util.Date


data class Mp3File(
    val uri: Uri = Uri.EMPTY,
    val title: String = "title",
    val album: String = "album",
    val artist: String = "artist",
    val duration: Long = 0L,
    val dateAdded: Long = 0L,
    val size: Long = 0L,
    var icon: Bitmap? = null
)

fun extractMp3Icon(context: Context, uri: Uri): Bitmap?{
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val icon = retriever.embeddedPicture
        retriever.release()
        icon?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }
    }
    catch (e: Exception){
        null
    }
}

fun loadBitmapAsync(context: Context, uri: Uri): Flow<Bitmap?> = flow {
    val icon = extractMp3Icon(context, uri) // heavy
    emit(icon)
}.flowOn(Dispatchers.IO)

suspend fun saveAlbumArtToCache(context: Context, uri: Uri): File? = withContext(Dispatchers.IO) {
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val artBytes = retriever.embeddedPicture
        retriever.release()

        if (artBytes != null) {
            val cacheDir = File(context.cacheDir, "album_art")
            if (!cacheDir.exists()) cacheDir.mkdirs()

            val fileName = "album_${uri.toString().hashCode()}.png"
            val imageFile = File(cacheDir, fileName)

            FileOutputStream(imageFile).use { it.write(artBytes) }

            return@withContext imageFile
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext null
}

