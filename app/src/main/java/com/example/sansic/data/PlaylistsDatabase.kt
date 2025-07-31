package com.example.sansic.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.room.Update
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import java.io.ByteArrayOutputStream

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val mp3Files: List<SerializableMp3File>
)

@Dao
interface PlaylistDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createPlaylist(playlist: Playlist)

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Update
    suspend fun updatePlaylist(playlist: Playlist)

    @Query("Select * from playlists Order by id ASC")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("Select * from playlists where id = :id Limit 1")
    fun getPlaylist(id: Int): Flow<Playlist?>
}

@Database(entities = [Playlist::class], version = 1, exportSchema = false)
@TypeConverters(PlaylistSongConverter::class)
abstract class PlaylistsDatabase(): RoomDatabase(){
    abstract fun playlistDao(): PlaylistDao

    companion object{
        @Volatile
        private var Instance: PlaylistsDatabase? = null

        fun getDatabase(context: Context): PlaylistsDatabase{
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    PlaylistsDatabase::class.java,
                    "playlist_db"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}


class PlaylistSongConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromPlaylistSongList(list: List<SerializableMp3File>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toPlaylistSongList(json: String?): List<SerializableMp3File> {
        if (json.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<SerializableMp3File>>() {}.type
        return gson.fromJson(json, listType)
    }
}






