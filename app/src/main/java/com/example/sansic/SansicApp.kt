package com.example.sansic

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sansic.navbar.BottomNavBar
import com.example.sansic.navbar.DrawerContent
import com.example.sansic.navbar.SanSicBottomAppBar
import com.example.sansic.navbar.TopNavBar
import com.example.sansic.navbar.getTopBarConfig
import com.example.sansic.navigation.SanSicNavHost
import com.example.sansic.navigation.SmallScreenNav
import com.example.sansic.player.NowPlayingNotification
import com.example.sansic.ui.viewmodel.AppViewModelProvider
import com.example.sansic.ui.viewmodel.AudioPlayerViewModel
import com.example.sansic.ui.viewmodel.FavoriteViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SanSicApp(){
    val navController = rememberNavController()
    val bottomNavBars = listOf(BottomNavBar.Home, BottomNavBar.Search, BottomNavBar.Favorite)
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentScreenRoute = navBackStackEntry.value?.destination?.route?:"home"
    var currentIndex = remember { mutableIntStateOf(0) }

    val audioPlayerViewModel: AudioPlayerViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val musicPlayerState = audioPlayerViewModel.musicPlayerState.collectAsState().value

    val favoriteViewModel: FavoriteViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val topBarConfig = getTopBarConfig(currentScreenRoute,
        {
            coroutineScope.launch {
                drawerState.open()
            }
        },
        navController)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent (
                onItemClicked = { route ->
                    coroutineScope.launch {
                        drawerState.close()
                        navController.navigate(route)
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopNavBar(topBarConfig)
            },
            bottomBar = {
                if(currentScreenRoute != SmallScreenNav.NowPlayingScreen.route){
                    BottomAppBar(
                        contentPadding = PaddingValues(0.dp, top = 4.dp),
                        contentColor = Color.DarkGray,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    ) {
                        SanSicBottomAppBar(
                            navController,
                            currentScreenRoute,
                            currentIndex,
                            bottomNavBars,
                            Modifier
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
//                .graphicsLayer {
//                    if (currentScreenRoute == SmallScreenNav.NowPlayingScreen.route) {
//                        renderEffect = RenderEffect
//                            .createBlurEffect(30f, 30f, Shader.TileMode.CLAMP)
//                            .asComposeRenderEffect()
//                    }
//                }

        ){innerPadding ->

            Box(modifier = Modifier
                .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter){
                Box(
                    modifier = Modifier
                        .background(color = Color.Black.copy(alpha = 0.5f))
                        //.padding(bottom = if (!isExpanded) 80.dp else 0.dp)
                ){
                    SanSicNavHost(
                        navController,
                        audioPlayerViewModel,
                        favoriteViewModel,
                        bottomNavBars,
                        currentIndex,
                        innerPadding,
                        Modifier
                    )
                }

                if(currentScreenRoute != SmallScreenNav.NowPlayingScreen.route){
//                    NowPlayingNotification(
//                        {
//                            navController.navigate(SmallScreenNav.NowPlayingScreen.route)
//                        },
//                        Modifier,
//                        innerPadding
//                    )
                }
            }
        }
    }
}

//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
//@Preview(showBackground = true)
//@Composable
//fun SanSicAppPreview(){
//    SanSicTheme {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(64.dp)
//                .padding(bottom = 400.dp) // leave space for BottomAppBar
//                .background(Color.DarkGray, shape = RoundedCornerShape(12.dp)),
//            contentAlignment = Alignment.Center
//        ) {
//            Text("Mini Player", color = Color.White)
//        }
//    }
//}



