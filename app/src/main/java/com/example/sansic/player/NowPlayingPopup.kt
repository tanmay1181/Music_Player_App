package com.example.sansic.player

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sansic.R
import com.example.sansic.data.Mp3File
import com.example.sansic.data.saveAlbumArtToCache
import com.example.sansic.ui.theme.SanSicTheme
import com.example.sansic.ui.viewmodel.AudioPlayerViewModel
import com.example.sansic.ui.viewmodel.FavoriteViewModel
import com.example.sansic.ui.viewmodel.PlayMode
import java.io.File

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NowPlayingScreen(audioPlayerViewModel: AudioPlayerViewModel,
                     favoriteViewModel: FavoriteViewModel,
                     modifier: Modifier) {

    val musicPlayerState = audioPlayerViewModel.musicPlayerState.collectAsState().value
    val currentSong = musicPlayerState.currentSong
    val isMusicPlaying = musicPlayerState.isMusicPlaying
    val playMode = musicPlayerState.playMode
    val favoriteSongs = favoriteViewModel.favoriteState.collectAsState().value.favoriteSongsList

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = Color(0xFF2B004B).copy(alpha = 0.3f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                NowPlayingSongImage(mp3File = currentSong, modifier)

                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)){
                    Column(
                        modifier = modifier.weight(0.6f)
                    ){
                        Text(text = currentSong.title,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = modifier
                                .basicMarquee(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color.White)

                        Text(text = currentSong.artist,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = modifier
                                .basicMarquee(),
                            fontSize = 16.sp,
                            color = Color.White)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                        val isFavorite = favoriteSongs.any{it.file.uriString == currentSong.uri.toString()}
                        IconButton(onClick = {
                            if(isFavorite){
                                favoriteViewModel.deleteSong(currentSong)
                            }
                            else{
                                favoriteViewModel.insertSong(currentSong)
                            }
                        }) {
                            if(isFavorite){
                                Icon(painter = painterResource(R.drawable.baseline_favorite_24),
                                    contentDescription = null,
                                    tint = Color.White)
                            }
                            else{
                                Icon(painter = painterResource(R.drawable.outline_favorite_border_24),
                                    contentDescription = null,
                                    tint = Color.White)
                            }
                        }

                        IconButton(onClick = {

                        }) {
                            Icon(painter = painterResource(R.drawable.baseline_share_24),
                                contentDescription = null,
                                tint = Color.White)
                        }
                    }
                }

                MusicSeekBar(
                    currentPosition = musicPlayerState.playBackPosition,
                    totalDuration = musicPlayerState.duration,
                    onSeekChanged = {newPosition ->
                        audioPlayerViewModel.seekTo(newPosition)
                    }
                )
            }

            Row(
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
            ) {

                if(playMode == PlayMode.CurrentLoop){
                    MusicNavIconButtons({
                        audioPlayerViewModel.changePlayMode()
                    },
                        R.drawable.baseline_repeat_on_24,
                        40.dp,
                        modifier)
                }
                else{
                    MusicNavIconButtons({
                        audioPlayerViewModel.changePlayMode()
                    },
                        R.drawable.baseline_repeat_24,
                        40.dp,
                        modifier)
                }

                MusicNavIconButtons({
                    audioPlayerViewModel.playPrevious()
                },
                    R.drawable.baseline_skip_previous_24,
                    56.dp,
                    modifier)

                if(isMusicPlaying){
                    MusicNavIconButtons({
                        audioPlayerViewModel.playPauseSong()
                    },
                        R.drawable.baseline_pause_circle_24,
                        80.dp,
                        modifier)
                }
                else{
                    MusicNavIconButtons({
                        audioPlayerViewModel.playPauseSong()
                    },
                        R.drawable.baseline_play_circle_24,
                        80.dp,
                        modifier)
                }

                MusicNavIconButtons({
                    audioPlayerViewModel.playNext()
                },
                    R.drawable.baseline_skip_next_24,
                    56.dp,
                    modifier)

                if(playMode == PlayMode.Shuffle){
                    MusicNavIconButtons({
                        audioPlayerViewModel.changePlayMode()
                    },
                        R.drawable.baseline_shuffle_on_24,
                        40.dp,
                        modifier)
                }
                else{
                    MusicNavIconButtons({
                        audioPlayerViewModel.changePlayMode()
                    },
                        R.drawable.baseline_shuffle_24,
                        40.dp,
                        modifier)
                }


            }
        }
    }
}

@Composable
fun MusicNavIconButtons(onIconClick: () -> Unit,
                        icon: Int,
                        size: Dp,
                        modifier: Modifier){
    IconButton(onClick = {
        onIconClick()
    },
         modifier = modifier.size(size)) {
        Icon(painter = painterResource(icon),
            contentDescription = null,
            tint = Color.White,
            modifier = modifier.fillMaxSize())
    }
}

@Composable
fun NowPlayingSongImage(mp3File: Mp3File, modifier: Modifier){
    val context = LocalContext.current
    val imageFileState = produceState<File?>(initialValue = null, mp3File.uri) {
        val cacheDir = File(context.cacheDir, "album_art")
        val fileName = "album_${mp3File.uri.toString().hashCode()}.png"
        val cachedFile = File(cacheDir, fileName)

        value = if (cachedFile.exists()) cachedFile
        else saveAlbumArtToCache(context, mp3File.uri)
    }
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        AsyncImage(model = imageFileState.value ?: R.drawable.baseline_music_note_24,
            contentDescription = null,
            modifier = modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSeekBar(
    modifier: Modifier = Modifier,
    currentPosition: Long,
    totalDuration: Long,
    onSeekChanged: (Long) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(0f) }

    // Sync the slider position with the current position unless user is interacting
    LaunchedEffect(currentPosition, totalDuration) {
        if (totalDuration > 0 && !sliderPosition.isNaN()) {
            sliderPosition = currentPosition / totalDuration.toFloat()
        }
    }

    Column(modifier = modifier
        .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-12).dp)) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                color = Color.White
            )
            Text(
                text = formatTime(totalDuration),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                color = Color.White
            )
        }

        Slider(
            value = sliderPosition.coerceIn(0f, 1f),
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
                val newPosition = (totalDuration * sliderPosition).toLong()
                onSeekChanged(newPosition)
            },
            modifier = modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.Cyan,
                inactiveTrackColor = Color.LightGray
            ),
            thumb = {
                Box(
                    modifier
                        .size(16.dp)
                        .background(color = Color.White ,
                            shape = CircleShape)
                )
            },
            track = {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Color.LightGray.copy(alpha = 0.4f))
                )
                Box(
                    modifier = modifier
                        .fillMaxWidth(fraction = sliderPosition.coerceIn(0f, 1f))
                        .height(2.dp)
                        .background(Color.Gray.copy(alpha = 0.6f))
                )
            }
        )
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun NowPlayingNotification(openNowPlayingScreen: () -> Unit,
                           onSeekChanged: (Long) -> Unit,
                           modifier: Modifier,
                           paddingValues: PaddingValues){

//    val musicPlayerState = audioPlayerViewModel.musicPlayerState.collectAsState().value
//    val currentSong = musicPlayerState.currentSong
//    val isMusicPlaying = musicPlayerState.isMusicPlaying

//    val context = LocalContext.current
//    val imageFileState = produceState<File?>(initialValue = null, currentSong.uri) {
//        val cacheDir = File(context.cacheDir, "album_art")
//        val fileName = "album_${currentSong.uri.toString().hashCode()}.png"
//        val cachedFile = File(cacheDir, fileName)
//
//        value = if (cachedFile.exists()) cachedFile
//        else saveAlbumArtToCache(context, currentSong.uri)
//    }

    val currentPosition = 0L
    val totalDuration = 0L

    var sliderPosition by remember { mutableStateOf(0f) }

    // Sync the slider position with the current position unless user is interacting
    LaunchedEffect(currentPosition, totalDuration) {
        if (totalDuration > 0 && !sliderPosition.isNaN()) {
            sliderPosition = currentPosition / totalDuration.toFloat()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = paddingValues.calculateBottomPadding())
            .height(80.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF336B),
                        Color(0xFFFF0050)
                    ),
                )
            )
            .clickable(onClick = {
                openNowPlayingScreen()
            }),
        colors = CardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Transparent
        ),
        border = BorderStroke(width = 1.dp, color = Color.White),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = modifier
            .padding(8.dp)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
//            Row(
//                modifier = modifier
//                    .fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                AsyncImage(model = R.drawable.baseline_music_note_24,
//                    contentDescription = null,
//                    modifier = modifier
//                        .clip(shape = RoundedCornerShape(32.dp))
//                        .size(56.dp),
//                    contentScale = ContentScale.Crop)
//
//                Row(
//                    modifier = modifier.width(156.dp)
//                ) {
//                    Text(text = "currentSong.title",
//                        textAlign = TextAlign.Center,
//                        fontWeight = FontWeight.Bold,
//                        softWrap = false,
//                        maxLines = 1,
//                        fontSize = 12.sp,
//                        modifier = modifier
//                            .basicMarquee()
//                    )
//                }
//
//                IconButton(
//                    onClick = {
//                        //audioPlayerViewModel.playPrevious()
//                    }
//                ) {
//                    Icon(painter = painterResource(R.drawable.baseline_skip_previous_24),
//                        contentDescription = null)
//                }
//
//                IconButton(
//                    onClick = {
//                        //audioPlayerViewModel.playPauseSong()
//                    },
//                    modifier = modifier.size(40.dp)
//                ) {
//                    if(true){
//                        Icon(painter = painterResource(R.drawable.baseline_pause_circle_24),
//                            contentDescription = null,
//                            modifier = modifier.fillMaxSize())
//                    }
//                    else{
//                        Icon(painter = painterResource(R.drawable.baseline_play_circle_24),
//                            contentDescription = null,
//                            modifier = modifier.fillMaxSize())
//                    }
//                }
//                IconButton(
//                    onClick = {
//                        //audioPlayerViewModel.playNext()
//                    }
//                ) {
//                    Icon(painter = painterResource(R.drawable.baseline_skip_next_24),
//                        contentDescription = null)
//                }
//
//            }

            Row(modifier = modifier
                .fillMaxWidth()){
                Text(
                    text = formatTime(currentPosition),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    color = Color.White
                )
                Slider(
                    value = sliderPosition.coerceIn(0f, 1f),
                    onValueChange = { sliderPosition = it },
                    onValueChangeFinished = {
                        val newPosition = (totalDuration * sliderPosition).toLong()
                        onSeekChanged(newPosition)
                    },
                    modifier = modifier,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Cyan,
                        inactiveTrackColor = Color.LightGray
                    ),
                    thumb = {
                        Box(
                            modifier
                                .size(16.dp)
                                .background(color = Color.White ,
                                    shape = CircleShape)
                        )
                    },
                    track = {
                        Box(
                            modifier = modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(Color.LightGray.copy(alpha = 0.4f))
                        )
                        Box(
                            modifier = modifier
                                .fillMaxWidth(fraction = sliderPosition.coerceIn(0f, 1f))
                                .height(2.dp)
                                .background(Color.Gray.copy(alpha = 0.6f))
                        )
                    }
                )
                Text(
                    text = formatTime(currentPosition),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    color = Color.White
                )
            }
        }


    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(showBackground = true)
fun Preview(){
    SanSicTheme {
        NowPlayingNotification({},{}, Modifier, PaddingValues())

        //NowPlayingSongImage(Mp3File(), Modifier)
    }
}

