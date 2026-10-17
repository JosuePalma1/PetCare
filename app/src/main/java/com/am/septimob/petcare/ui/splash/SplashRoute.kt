package com.am.septimob.petcare.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.delay

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val onFinishedActual by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        // Mantiene visible el diseño de marca antes de abrir la pantalla principal.
        withFrameNanos { }
        delay(800)
        onFinishedActual()
    }

    SplashScreen()
}
