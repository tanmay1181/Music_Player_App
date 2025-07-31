package com.example.sansic.navigation

sealed class SmallScreenNav(val route: String, val title: String) {
    data object RecentlyAdded: SmallScreenNav("recently_added", "Recently Added")
    data object NowPlayingScreen: SmallScreenNav("now_playing", "Now Playing")
}