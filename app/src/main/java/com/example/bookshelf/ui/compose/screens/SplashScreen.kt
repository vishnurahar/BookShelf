package com.example.bookshelf.ui.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color.LightGray),

        ) {
        Icon(
            modifier = Modifier
                .fillMaxSize(.5f)
                .align(Alignment.Center),
            imageVector = Icons.Default.ThumbUp,
            contentDescription = null,
            tint = Color.White
        )
    }
}