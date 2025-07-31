package com.example.sansic.navbar

import androidx.annotation.DrawableRes
import com.example.sansic.R

sealed class BottomNavBar(val route: String,
                          @DrawableRes
                          val icon: Int,
                          val title: String) {
    data object Home: BottomNavBar("home", R.drawable.outline_home_24 , "Home")
    data object Search: BottomNavBar("search", R.drawable.baseline_search_24, "Search")
    data object Favorite: BottomNavBar("favorites", R.drawable.outline_favorite_border_24, "Favorite")
}