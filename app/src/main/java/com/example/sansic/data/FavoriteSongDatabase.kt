package com.example.sansic.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream

@Database(entities = [FavoriteSong::class], version = 1, exportSchema = false)
@TypeConverters(Mp3FileConverter::class)
abstract class FavoriteSongDatabase(): RoomDatabase() {
    abstract fun favoriteSongDao(): FavoriteSongDao

    companion object {
        @Volatile
        private var Instance: FavoriteSongDatabase? = null

        fun getDatabase(context: Context): FavoriteSongDatabase{
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                                context,
                                FavoriteSongDatabase::class.java,
                                "favoriteSong_db"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

@Entity(tableName = "favorites")
data class FavoriteSong(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val file: SerializableMp3File,
    val bitMap: Bitmap? = null
)

@Dao
interface FavoriteSongDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: FavoriteSong)

    @Delete
    suspend fun deleteSong(song: FavoriteSong)

    @Query("SELECT * FROM favorites ORDER BY id ASC")
    fun getAllFavoriteSongs(): Flow<List<FavoriteSong>>

//    @Query("SELECT * FROM favorites Where file = :file")
//    fun getFavoriteSong(file: Mp3File): Flow<FavoriteSong>

}

class Mp3FileConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromSerializableMp3File(file: SerializableMp3File): String {
        return gson.toJson(file)
    }

    @TypeConverter
    fun toSerializableMp3File(json: String): SerializableMp3File {
        return gson.fromJson(json, SerializableMp3File::class.java)
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) return null
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(bytes: ByteArray?): Bitmap? {
        return bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    }
}

data class SerializableMp3File(
    val uriString: String,
    val title: String,
    val album: String,
    val artist: String
)

fun Mp3File.toSerializable(): SerializableMp3File = SerializableMp3File(
    uriString = uri.toString(),
    title = title,
    album = album,
    artist = artist
)

fun SerializableMp3File.toMp3File(): Mp3File = Mp3File(
    uri = uriString.toUri(),
    title = title,
    album = album,
    artist = artist
)