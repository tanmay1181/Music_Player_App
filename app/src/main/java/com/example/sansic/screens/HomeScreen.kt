package com.example.sansic.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.sansic.R
import com.example.sansic.data.Mp3File
import com.example.sansic.data.loadBitmapAsync
import com.example.sansic.data.saveAlbumArtToCache
import com.example.sansic.ui.viewmodel.AppViewModelProvider
import com.example.sansic.ui.viewmodel.AudioPlayerViewModel
import com.example.sansic.ui.viewmodel.FavoriteViewModel
import com.example.sansic.ui.viewmodel.HomeViewModel
import com.example.sansic.ui.viewmodel.MusicPlayerState
import com.example.sansic.ui.viewmodel.SortType
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

enum class HeaderType(val showName: String){
    Songs("Songs"),
    Artists("Artists"),
    Albums("Albums"),
}

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)

@Composable
fun HomeScreen(onSongClick: (mp3File: Mp3File) -> Unit,
               viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
               audioPlayerViewModel: AudioPlayerViewModel,
               favoriteViewModel: FavoriteViewModel,
               gotoRecentlyAddedMp3Screen: () -> Unit,
               goToNowPlayingScreen:() -> Unit,
               modifier: Modifier){
    val listState = rememberLazyListState()

    val homeState by viewModel.homeState.collectAsState()
    val selectedHeader = homeState.headerType
    val currentMp3List = homeState.mp3Files
    val selectedSort = homeState.sortType
    val recentlyAddedMp3Files = homeState.recentlyAddedMp3Files
    val differentTypeMp3Map = homeState.mp3Map

    val musicPlayerState by audioPlayerViewModel.musicPlayerState.collectAsState()
    val currentSong = musicPlayerState.currentSong

    var showDialog = remember { mutableStateOf(false) }

    val favoriteMp3Files = favoriteViewModel.favoriteState.collectAsState().value.favoriteSongsList

    val permissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    LaunchedEffect(permissionState.status.isGranted) {
        if(permissionState.status.isGranted){
            //audioPlayerViewModel.populateMp3Library()
            viewModel.populateLibrary()
        }
        else{
            permissionState.launchPermissionRequest()
        }
    }

    Surface(modifier = Modifier
        .fillMaxSize(),
        color = Color.Transparent,
    ) {

        if(showDialog.value){
            AlertDialog(icon = {  },
                title = {
                    Text(text = "Choose a Sort Type")
                },
                text = {

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(SortType.entries.toList()){ sortType ->
                            val isSelectedSort = sortType == selectedSort
                            Text(text = sortType.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if(isSelectedSort) Color.Cyan else Color.Unspecified,
                                modifier = Modifier.clickable(onClick = {
                                    showDialog.value = false
                                    viewModel.sortMp3Files(sortType)
                                })
                                    .height(20.dp))
                        }
                    }
                },
                onDismissRequest = {showDialog.value = false},
                confirmButton = {},
                dismissButton = {})
        }

        LazyColumn(
            state = listState
        ) {
            if(selectedHeader == HeaderType.Songs){
                if(recentlyAddedMp3Files.isNotEmpty()){
                    item {
                        RecentlyAddedSection(
                            recentlyAddedMp3Files,
                            audioPlayerViewModel,
                            musicPlayerState,
                            {
                                goToNowPlayingScreen()
                            },
                            onSeeAllClick = {
                                gotoRecentlyAddedMp3Screen()
                            },
                            modifier = modifier,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            stickyHeader {
                StickyHeader(
                    HeaderType.entries.toList(),
                    selectedHeader,
                    {
                        viewModel.updateHeaderType(it)
                    },
                    {
                        showDialog.value = true
                    },
                    selectedHeader == HeaderType.Songs,
                    listState)

                //Spacer(modifier = Modifier.height(16.dp))
            }

            if(currentMp3List.isEmpty()){
                items(differentTypeMp3Map.keys.toList()){ cardHeader ->
                    NonMp3ListCard(
                        cardHeader,
                        {
                            viewModel.setMp3FilesFromFolders(cardHeader)
                        },
                        Modifier
                    )
                }
            }
            else{
                items(currentMp3List) { mp3File ->
                    val isFavorite = favoriteMp3Files.any{it.file.uriString == mp3File.uri.toString()}
                    Mp3Card(
                        mp3File = mp3File,
                        isPlaying = currentSong.uri == mp3File.uri,
                        onSongClick = {
                            onSongClick(mp3File)
                            goToNowPlayingScreen()
                        },
                        isFavorite = isFavorite,
                        addToFavorites = {
                            favoriteViewModel.insertSong(mp3File)
                        },
                        removeFromFavorites = {
                            favoriteViewModel.deleteSong(mp3File)
                        },
                        modifier = modifier
                    )
                }
            }
        }
    }
}

@Composable
fun RecentlyAddedSection(recentlyAdded: List<Mp3File>,
                         audioPlayerViewModel: AudioPlayerViewModel,
                         musicPlayerState: MusicPlayerState,
                         goToNowPlayingScreen: () -> Unit,
                         onSeeAllClick: () -> Unit,
                         modifier: Modifier = Modifier){

    val hasMoreThanFiveSongs = recentlyAdded.size > 5
    val showRecentlyAddedSongs = recentlyAdded.takeIf { it.size > 5 }?.take(5) ?: recentlyAdded
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(text = "Recently Added",
                color = Color.White,
                fontWeight = FontWeight.Bold)

            if(hasMoreThanFiveSongs){
                Text(text = "See All",
                    color = Color.Cyan,
                    fontSize = 12.sp,
                    modifier = modifier.clickable(
                        onClick = {
                            onSeeAllClick()
                        }
                    ))
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.padding(2.dp)
        ) {
            items(showRecentlyAddedSongs) { mp3File ->
                RecentlyAddedCard(mp3File,
                    {
                        audioPlayerViewModel.onMusicClick(it)
                        audioPlayerViewModel.updateCurrentMp3Files(recentlyAdded)
                    },
                    {
                        goToNowPlayingScreen()
                    },
                    musicPlayerState,
                    modifier = modifier)
            }
        }
    }
}

@Composable
fun RecentlyAddedCard(mp3File: Mp3File,
                      playMusic:(mp3File: Mp3File) -> Unit,
                      goToNowPlayingScreen: () -> Unit,
                      musicPlayerState: MusicPlayerState,
                      modifier: Modifier){
    val context = LocalContext.current
    val imageFileState = produceState<File?>(initialValue = null, mp3File.uri) {
        val cacheDir = File(context.cacheDir, "album_art")
        val fileName = "album_${mp3File.uri.toString().hashCode()}.png"
        val cachedFile = File(cacheDir, fileName)

        value = if (cachedFile.exists()) cachedFile
        else saveAlbumArtToCache(context, mp3File.uri)
    }


    val isPlaying = musicPlayerState.currentSong.uri == mp3File.uri

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(model = imageFileState.value ?: R.drawable.baseline_music_note_24,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(80.dp)
                .clip(shape = RoundedCornerShape(4.dp))
                .clickable(onClick = {
                    playMusic(mp3File)
                    goToNowPlayingScreen()
                })
                .background(Color.White))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.width(72.dp)
        ) {

            val textColor = if(isPlaying) Color.Cyan else Color.White

            Text(text = mp3File.title,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = modifier
                    .basicMarquee())
            Text(text = mp3File.artist,
                color = textColor,
                fontSize = 8.sp,
                modifier = modifier
                    .offset(y = (-8).dp)
                    .basicMarquee())
        }

    }
}

@Composable
fun StickyHeader(stickyHeaderList: List<HeaderType>,
                 selectedHeader: HeaderType,
                 onHeaderClick: (HeaderType) -> Unit,
                 onSort: () -> Unit,
                 canSort: Boolean,
                 listState: LazyListState){

    val isHeaderAtTop = remember {
        derivedStateOf { listState.firstVisibleItemIndex >= 1 }
    }
    val bgColor by animateColorAsState(
        if (isHeaderAtTop.value) Color(0xFF2B004B) else Color.Transparent,
        label = "HeaderColor"
    )
    Box(
        modifier = Modifier
            .background(bgColor)
            .padding(2.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)) {
                items(stickyHeaderList){ stickyHeader ->
                    val cardContainerColor =
                        if(selectedHeader == stickyHeader) Color.Cyan else Color.LightGray
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor =  cardContainerColor,
                            contentColor = Color(0xFF2B004B),
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .size(width = 108.dp, height = 32.dp)
                            .padding()
                            .clickable(onClick = {
                                onHeaderClick(stickyHeader)
                            })
                    ) {
                        Text(text = stickyHeader.showName,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            maxLines = 1,)
                    }

                }
            }
            if(canSort){
                IconButton(
                    onClick = {
                        onSort()
                    },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(painter = painterResource(R.drawable.baseline_swap_vert_24),
                        contentDescription = null,
                        tint = Color.White)
                }
            }

        }

    }


}

@Composable
fun Mp3Card(mp3File: Mp3File ,
            isPlaying: Boolean,
            onSongClick:() -> Unit,
            isFavorite: Boolean,
            addToFavorites: () -> Unit,
            removeFromFavorites: () -> Unit,
            modifier: Modifier){

    val context = LocalContext.current
    val imageFileState = produceState<File?>(initialValue = null, mp3File.uri) {
        val cacheDir = File(context.cacheDir, "album_art")
        val fileName = "album_${mp3File.uri.toString().hashCode()}.png"
        val cachedFile = File(cacheDir, fileName)

        value = if (cachedFile.exists()) cachedFile
        else saveAlbumArtToCache(context, mp3File.uri)
    }

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable(
            onClick = {
                onSongClick()
            }
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth(),) {
            AsyncImage(model = imageFileState.value ?: R.drawable.baseline_music_note_24,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(56.dp)
                    .clip(shape = RoundedCornerShape(32.dp))
                    .background(Color.White))

            Spacer(modifier.width(16.dp))
            Box(
                modifier = modifier
                    .weight(1f)
                    .clipToBounds()
                    .padding(horizontal = 8.dp)
            ){
                Column {
                    Text(text = mp3File.title,
                        color = if(isPlaying) Color.Cyan else Color.White,
                        softWrap = false,
                        maxLines = 1,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = modifier
                            .basicMarquee())
                    Text(text = mp3File.artist ,
                        color = if(isPlaying) Color.Cyan else Color.White,
                        fontSize = 12.sp,
                        softWrap = false,
                        modifier = modifier
                            .basicMarquee())
                }
            }
            Spacer(modifier.width(12.dp))
            Row {
                Icon(imageVector = if(isFavorite)
                    Icons.Filled.Favorite
                    else Icons.Filled.FavoriteBorder,
                    tint = Color.White,
                    contentDescription = null,
                    modifier = modifier
                        .clickable(
                            onClick = {
                                if(isFavorite){
                                    removeFromFavorites()
                                }
                                else{
                                    addToFavorites()
                                }
                            }
                        ))
                Spacer(modifier.width(16.dp))
                Icon(imageVector = Icons.Filled.Menu,
                    tint = Color.White,
                    contentDescription = null,
                    modifier = modifier
                        .clickable(
                            onClick = {
                                //Show song options
                            }
                        ))

            }
        }

        HorizontalDivider(thickness = 1.dp,
            color = Color.Gray,)
    }

}

@Preview(showBackground = true)
@Composable
fun JustPreview(){
    AlertDialog(icon = {  },
        title = {
            Text(text = "Choose a Sort Type")
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SortType.entries.toList()){ sortType ->
                    Text(text = sortType.type,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable(onClick = {

                    })
                        .height(20.dp))
                }
            }
        },
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {})
}

@Composable
fun getImageBitmapFromDrawable(@DrawableRes resId: Int): ImageBitmap {
    val context = LocalContext.current
    val drawable = ContextCompat.getDrawable(context, resId)!!
    val bitmap = drawable.toBitmap()
    return bitmap.asImageBitmap()
}
