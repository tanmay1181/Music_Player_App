package com.example.sansic.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.sansic.navigation.SmallScreenNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(config: TopBarConfig) {
    CenterAlignedTopAppBar(
        title = { Text(text = config.title, textAlign = TextAlign.Center) },
        navigationIcon = config.icon.let { icon ->
            {
                IconButton(onClick = { config.onIconClick.invoke() }) {
                    Icon(imageVector = icon, contentDescription = null)
                }
            }
        },
        colors = TopAppBarColors(
            containerColor = Color(0xFF2B004B),
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

fun getTopBarConfig(
    route: String?,
    onDrawerOpen: () -> Unit,
    navController: NavHostController
): TopBarConfig {
    return when (route) {
         BottomNavBar.Home.route -> TopBarConfig(
            title = BottomNavBar.Home.title,
            icon = Icons.Filled.Menu,
            onIconClick = {
                onDrawerOpen()
            }
        )
        BottomNavBar.Favorite.route -> TopBarConfig(
            title = BottomNavBar.Favorite.title,
            icon = Icons.Filled.ArrowBack,
            onIconClick = {
                navController.navigate(BottomNavBar.Home.route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                        saveState = true
                    }
                }
            }
        )
        BottomNavBar.Search.route -> TopBarConfig(
            title = BottomNavBar.Search.title,
            icon = Icons.Filled.ArrowBack,
            onIconClick = {
                navController.navigate(BottomNavBar.Home.route) {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = false
                        saveState = true
                    }
                }
            }
        )
        SmallScreenNav.RecentlyAdded.route -> TopBarConfig(
            title = SmallScreenNav.RecentlyAdded.title,
            icon = Icons.Filled.ArrowBack,
            onIconClick = {navController.popBackStack()}
        )
        DrawerNavBar.Playlists.route -> TopBarConfig(
            title = DrawerNavBar.Playlists.title,
            icon = Icons.Filled.ArrowBack,
            onIconClick = {navController.popBackStack()}
        )
        DrawerNavBar.PlaylistView.route -> TopBarConfig(
            title = DrawerNavBar.PlaylistView.title,
            icon = Icons.Filled.ArrowBack,
            onIconClick = {navController.popBackStack()}
        )
        SmallScreenNav.NowPlayingScreen.route -> TopBarConfig(
            title = SmallScreenNav.NowPlayingScreen.title,
            icon = Icons.Filled.KeyboardArrowDown,
            onIconClick = {navController.popBackStack()}
        )
        else -> TopBarConfig(
            title = "App",
            icon = Icons.Filled.Close,
            onIconClick = {}
        )
    }
}

data class TopBarConfig(
    val title: String,
    val icon: ImageVector,
    val onIconClick: (() -> Unit)
)
