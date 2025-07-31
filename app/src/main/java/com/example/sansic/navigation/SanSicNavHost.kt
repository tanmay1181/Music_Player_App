package com.example.sansic.navigation


import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sansic.navbar.BottomNavBar
import com.example.sansic.navbar.DrawerNavBar
import com.example.sansic.player.NowPlayingScreen
import com.example.sansic.screens.FavoriteScreen
import com.example.sansic.screens.HomeScreen
import com.example.sansic.screens.PlaylistScreen
import com.example.sansic.screens.PlaylistsSongView
import com.example.sansic.screens.RecentlyAddedMp3Screen
import com.example.sansic.screens.SearchScreen
import com.example.sansic.ui.viewmodel.AppViewModelProvider
import com.example.sansic.ui.viewmodel.AudioPlayerViewModel
import com.example.sansic.ui.viewmodel.FavoriteViewModel
import com.example.sansic.ui.viewmodel.HomeViewModel
import com.example.sansic.ui.viewmodel.PlaylistViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SanSicNavHost(
    navController: NavHostController,
    audioPlayerViewModel: AudioPlayerViewModel,
    favoriteViewModel: FavoriteViewModel,
    bottomNavBars: List<BottomNavBar>,
    currentIndex: MutableState<Int>,
    innerPadding: PaddingValues,
    modifier: Modifier
){

    var offsetX = remember { Animatable(0f) }
    val screenWidth = LocalDensity.current.run { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val coroutine = rememberCoroutineScope()
    val playlistViewModel: PlaylistViewModel = viewModel(factory = AppViewModelProvider.Factory)

    //need an update

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            val canSwipeLeft = currentIndex.value < bottomNavBars.lastIndex
                            val canSwipeRight = currentIndex.value > 0

                            val shouldDrag =
                                (dragAmount < 0 && canSwipeLeft) || (dragAmount > 0 && canSwipeRight)
                            if (shouldDrag) {
                                coroutine.launch {
                                    offsetX.snapTo(offsetX.value + dragAmount)
                                }
                            }

                        },
                        onDragEnd = {
                            val threshold = screenWidth / 4
                            when {
                                offsetX.value < -threshold && currentIndex.value < bottomNavBars.lastIndex -> {
                                    currentIndex.value++
                                    navController.navigate(bottomNavBars[currentIndex.value].route) {
                                        popUpTo(bottomNavBars.first().route) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                }

                                offsetX.value > threshold && currentIndex.value > 0 -> {
                                    currentIndex.value--
                                    navController.navigate(bottomNavBars[currentIndex.value].route) {
                                        popUpTo(bottomNavBars.first().route) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                }
                            }
                            coroutine.launch {
                                offsetX.animateTo(0f, tween(300))
                            }
                        }
                    )
                }
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2B004B),
                            Color(0xFF9A0175),
                            Color(0xFFFF0050)
                        ),
                    )
                )
        ){
//            bottomNavBars.forEachIndexed { index, screen ->
//                val relativeOffset = (index - currentIndex.value) * screenWidth + offsetX.value
//                Box(
//                    modifier
//                        .offset { IntOffset(relativeOffset.roundToInt(), 0) }
//                        .fillMaxSize()
//                ) {
//                    when (screen) {
//                        BottomNavBar.Favorite -> FavoriteScreen(audioPlayerViewModel = audioPlayerViewModel,
//                            modifier = Modifier)
//                        BottomNavBar.Home ->  HomeScreen (
//                            onSongClick = { mp3File ->
//                                audioPlayerViewModel.onMusicClick(mp3File)
//                            },
//                            viewModel = homeViewModel,
//                            audioPlayerViewModel = audioPlayerViewModel,
//                            favoriteViewModel = favoriteViewModel,
//                            gotoRecentlyAddedMp3Screen = {
//                                navController.navigate(SmallScreenNav.RecentlyAdded.route)
//                            },
//                            modifier = Modifier
//                        )
//                        BottomNavBar.Search -> SearchScreen(modifier)
//                    }
//                }
//            }

            HideKeyboardOnNavigation(navController)
            NavHost(navController = navController,
                startDestination = BottomNavBar.Home.route,
                modifier = modifier
            ) {
                composable(route = BottomNavBar.Home.route,){
                    HomeScreen (
                        onSongClick = { mp3File ->
                            audioPlayerViewModel.onMusicClick(mp3File)
                        },
                        audioPlayerViewModel = audioPlayerViewModel,
                        favoriteViewModel = favoriteViewModel,
                        gotoRecentlyAddedMp3Screen = {
                            navController.navigate(SmallScreenNav.RecentlyAdded.route){
                                popUpTo(BottomNavBar.Home.route)
                            }
                        },
                        goToNowPlayingScreen = {
                            navController.navigate(SmallScreenNav.NowPlayingScreen.route)
                        },
                        modifier = Modifier
                    )
                }
                composable(route = BottomNavBar.Search.route,){
                    SearchScreen(modifier)
                }
                composable(route = BottomNavBar.Favorite.route){
                    FavoriteScreen(audioPlayerViewModel = audioPlayerViewModel,
                        favoriteViewModel = favoriteViewModel,
                        modifier = Modifier)
                }
                composable(route = SmallScreenNav.RecentlyAdded.route) {
                    RecentlyAddedMp3Screen(
                        audioPlayerViewModel = audioPlayerViewModel,
                        favoriteViewModel = favoriteViewModel,
                        modifier = modifier
                    )
                }
                composable(route = DrawerNavBar.Playlists.route) {
                    PlaylistScreen(
                        playlistViewModel = playlistViewModel,
                        onNavigateToCurrentPlaylist = {
                            navController.navigate(DrawerNavBar.PlaylistView.route)
                        },
                        modifier = Modifier
                    )
                }
                composable(route = DrawerNavBar.PlaylistView.route) {
                    PlaylistsSongView(
                        playlistViewModel = playlistViewModel,
                        audioPlayerViewModel = audioPlayerViewModel,
                        modifier = modifier
                    )
                }
                composable(route = SmallScreenNav.NowPlayingScreen.route) {
                    NowPlayingScreen(
                        audioPlayerViewModel = audioPlayerViewModel,
                        favoriteViewModel = favoriteViewModel,
                        modifier = modifier
                        )
                }
            }
        }
    }
}

@Composable
fun HideKeyboardOnNavigation(navController: NavHostController) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val currentBackStackEntry = navController.currentBackStackEntryAsState()

    LaunchedEffect(currentBackStackEntry.value?.destination?.route) {
        keyboardController?.hide()
        focusManager.clearFocus()
    }
}

