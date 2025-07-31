package com.example.sansic.navbar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.Outline
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.example.sansic.SanSicApp
import com.example.sansic.ui.theme.SanSicTheme

@Composable
fun SanSicBottomAppBar(
    navController: NavHostController,
    currentRoute: String,
    currentIndex: MutableState<Int>,
    bottomNavBars: List<BottomNavBar>,
    modifier: Modifier = Modifier
) {
    // Update currentIndex based on currentRoute
    LaunchedEffect(currentRoute) {
        bottomNavBars.forEachIndexed { index, item ->
            if (item.route == currentRoute) {
                currentIndex.value = index
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.SpaceAround,
        modifier = modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(Color(0xFFFF0050))
    ) {
        bottomNavBars.forEachIndexed { index, bottomNavbar ->
            val selected = currentIndex.value == index
            val navBarIconTint = if (selected) Color.Cyan else Color.LightGray

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxHeight()
            ) {
                Icon(
                    painter = painterResource(bottomNavbar.icon),
                    contentDescription = bottomNavbar.title,
                    tint = navBarIconTint,
                    modifier = modifier
                        .size(36.dp)
                        .clickable {
                            if (currentIndex.value != index) {
                                currentIndex.value = index
                                navController.navigate(bottomNavbar.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = false
                                        saveState = true
                                    }
                                }
                            }
                        }
                        .padding(4.dp)
                )
                Text(text = bottomNavbar.title, color = navBarIconTint)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun SanSicAppPreview(){
    SanSicTheme {
        SanSicApp()
    }
}

