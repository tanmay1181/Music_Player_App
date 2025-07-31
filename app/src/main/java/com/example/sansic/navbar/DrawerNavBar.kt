package com.example.sansic.navbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sansic.R

sealed class DrawerNavBar(val route: String,
                          @DrawableRes
                          val icon: Int,
                          val title: String) {
    data object Playlists: DrawerNavBar("playlists", R.drawable.baseline_audio_file_24, "Playlists")
    data object PlaylistView: DrawerNavBar("playlist_view", R.drawable.baseline_audio_file_24, "Playlist Songs")
    data object Settings: DrawerNavBar("settings", R.drawable.baseline_settings_24, "Settings")
}

@Composable
fun DrawerContent(onItemClicked: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF2B004B),
                    Color(0xFF9A0175),
                    Color(0xFFFF0050)
                ),
            )
        )
            .padding(16.dp, top = 72.dp)
    ) {
        Text("Firebase & Acc",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        DrawerItem(DrawerNavBar.Playlists.title,
            DrawerNavBar.Playlists.route,
            DrawerNavBar.Playlists.icon,
            onItemClicked)
        DrawerItem(DrawerNavBar.Settings.title,
            DrawerNavBar.Settings.route,
            DrawerNavBar.Settings.icon,
            onItemClicked)
    }
}

@Composable
fun DrawerItem(label: String,
               route: String,
               icon: Int,
               onClick: (String) -> Unit) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Color.White
        )
        Text(
            text = label,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick(route) }
                .padding(12.dp),
            fontSize = 16.sp,
            color = Color.White
        )
    }

}
