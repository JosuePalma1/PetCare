package com.am.septimob.petcare.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val onFinishedActual by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        // Deja que el Splash se pinte al menos un frame, sin una espera artificial.
        withFrameNanos { }
        onFinishedActual()
    }

    SplashScreen()
}
