package com.am.septimob.petcare.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.delay

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val onFinishedActual by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        delay(900)
        onFinishedActual()
    }

    SplashScreen()
}
