package com.example.sansic.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sansic.R

@Composable
fun NonMp3ListCard(cardHeader: String,
                   onCardClick: (typeName: String) -> Unit,
                   modifier: Modifier){

    Column(modifier = modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable(
            onClick = {
                onCardClick(cardHeader)
            }
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth(),) {
            Image(painter = painterResource(R.drawable.baseline_music_note_24),
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
                    Text(text = cardHeader,
                        color = Color.White,
                        softWrap = false,
                        maxLines = 1,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = modifier
                            .basicMarquee())
                }
            }
            Spacer(modifier.width(12.dp))
        }

        HorizontalDivider(thickness = 1.dp,
            color = Color.Gray,)
    }
}