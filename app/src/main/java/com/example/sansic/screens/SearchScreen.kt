package com.example.sansic.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sansic.R
import com.example.sansic.data.Mp3File
import com.example.sansic.ui.theme.SanSicTheme
import com.example.sansic.ui.viewmodel.AppViewModelProvider
import com.example.sansic.ui.viewmodel.HomeViewModel
import com.example.sansic.ui.viewmodel.SearchViewModel

@Composable
fun SearchScreen(modifier: Modifier){
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    val viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val mp3Files = viewModel.mp3Files.collectAsState().value
    var searchQuery by remember { mutableStateOf("") }

    val filteredSongs = remember(searchQuery, mp3Files) {
        if(searchQuery.isBlank()) mp3Files
        else {
            mp3Files.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ){
                focusManager.clearFocus()
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    textStyle = TextStyle(color = Color.White),
                    singleLine = true,
                    placeholder = {
                        Text(text = "Type here or use Mic",
                            color = Color.White)
                    },
                    leadingIcon = if(isFocused){
                        @Composable {
                            Icon(imageVector = Icons.Filled.Search,
                                contentDescription = "search",
                                tint = Color.White)
                        }
                    } else null,
                    trailingIcon = {
                        if(searchQuery == ""){
                            IconButton(onClick = {
                                //Search by voice/audio
                            }){
                                Icon(painter = painterResource(R.drawable.baseline_mic_none_24),
                                    tint = Color.White,
                                    contentDescription = "mic")
                            }
                        }
                        else{
                            IconButton(onClick = {
                                searchQuery = ""
                            }){
                                Icon(imageVector = Icons.Filled.Clear,
                                    tint = Color.White,
                                    contentDescription = "clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = modifier
                        .fillMaxWidth()
                        .onFocusChanged{focusState ->
                            isFocused = focusState.isFocused
                        }
                )
            }
            if(isFocused){
                LazyColumn {
                    //Show all search results
                    items(filteredSongs) { song ->
                        SearchedSongDisplay(song, modifier)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchedSongDisplay(mp3File: Mp3File, modifier: Modifier){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(60.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(R.drawable.baseline_music_note_24),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(40.dp)
                .clip(shape = RoundedCornerShape(32.dp))
                .background(Color.White))
        Text(text = mp3File.title,
            color = Color.White,
            style = TextStyle(
                textDecoration = TextDecoration.Underline
            ),
            modifier = modifier
                .clickable(
                    onClick = {
                        //play audio player
                    }
                ))

    }
}

//@Preview(showBackground = true)
//@Composable
//fun SearchedSongPreview(){
//    SanSicTheme {
//        SearchedSongDisplay(Modifier)
//    }
//}
