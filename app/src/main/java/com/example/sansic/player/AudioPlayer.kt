package com.example.sansic.player

import android.content.Context
import androidx.core.content.edit
import androidx.media3.exoplayer.ExoPlayer
import com.example.sansic.ui.viewmodel.PlayMode
import com.example.sansic.ui.viewmodel.SortType


class AudioPlayer(context: Context){
    val exoPlayer = ExoPlayer.Builder(context).build()

    val sharedPrefs = context.getSharedPreferences("music_prefs", Context.MODE_PRIVATE)

    // Retrieve last played song and seek position
    val getLastPlayedSong: Pair<String?, Long> = Pair(
        sharedPrefs.getString("last_song", null),
        sharedPrefs.getLong("last_position", 0L)
    )

    // Save last played song and position
    val saveLastPlayedSong: (songUri: String, position: Long) -> Unit = { uri, pos ->
        sharedPrefs.edit().apply {
            putString("last_song", uri)
            putLong("last_position", pos)
            apply()
        }
    }

    val getLastPlayMode: String =
        sharedPrefs.getString("last_playMode", PlayMode.CurrentLoop.mode).toString()

    val saveLastPlayMode: (playMode: PlayMode) -> Unit = {playMode ->
        sharedPrefs.edit().apply{
            putString("last_playMode", playMode.toString())
        }
    }

    val getLastSortType: SortType
        get() = try {
            SortType.valueOf(sharedPrefs.getString("last_sortType", SortType.DateAdded.name)!!)
        } catch (e: IllegalArgumentException) {
            SortType.DateAdded
        }

    val isSortAscending: Boolean
        get() = sharedPrefs.getBoolean("is_sort_ascending", true) // or false as default

    val saveSortSettings: (SortType, Boolean) -> Unit = { sortType, isAscending ->
        sharedPrefs.edit {
            putString("last_sortType", sortType.name)
            putBoolean("is_sort_ascending", isAscending)
        }
    }




}
