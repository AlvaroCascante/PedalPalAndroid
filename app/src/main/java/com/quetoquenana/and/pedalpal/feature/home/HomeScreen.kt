package com.quetoquenana.and.pedalpal.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quetoquenana.and.pedalpal.R
import com.quetoquenana.and.pedalpal.core.ui.theme.PedalPalTheme


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,

) {
    HomeScreenContent(
        modifier = modifier
    )
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Welcome to PedalPal",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
        )

        Spacer(Modifier.height(12.dp))

        Image(
            painter = painterResource(R.drawable.mobi_bike_logo),
            contentDescription = "MobiBike logo",
            modifier = Modifier.size(160.dp),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenContentPreview() {
    PedalPalTheme {
        HomeScreenContent(
            modifier = Modifier.padding(vertical = 24.dp)
        )
    }
}