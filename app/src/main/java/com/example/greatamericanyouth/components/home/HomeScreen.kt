package com.example.greatamericanyouth.components.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.greatamericanyouth.R

@Composable
fun HomeScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(2.dp)
        .shadow(4.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()) {
            val painter = painterResource(R.drawable.home_screen)
            Image(painter,
                contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .padding(12.dp)
                    .border(1.dp, MaterialTheme.colorScheme.onBackground, MaterialTheme.shapes.small)
            )
            Box(modifier = Modifier.padding(16.dp)){
                Text("A Society More Elitist Than MENSA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 28.sp,
                    color = Color(0xFFFFD700)
                )
            }
        }
        HomeParagraph("We are a team of filthy-rich philanthropists who wish to deliver credible, authentic news to the hands of the great American people.")
        HomeParagraph("Over the past few centuries, the American people have been inundated by news agencies run by feeble-minded pandering cowards")
        HomeParagraph("A few assumptions about the reader..")
        HomeParagraph("You consume Arby's (ice dweller behavior)")
        HomeParagraph("You have a GED (poor person mentality)")
        HomeParagraph("You love your family (repulsive and cringe, the family unit should be abolished for the benefit of the techno-capital system)")

    }
}
@Composable
fun HomeParagraph(text: String) {
    Row(horizontalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.padding(16.dp)){
            Text(text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }

}
