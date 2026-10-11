package com.am.septimob.petcare.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Marcadores temporales hasta que Persona A suba Splash y Home.
// Cuando lleguen, se reemplazan en PetCareApp por SplashRoute/HomeRoute y se borra este archivo.

@Composable
internal fun SplashPendiente(onTimeout: () -> Unit) {
    val onTimeoutActual by rememberUpdatedState(onTimeout)

    LaunchedEffect(Unit) {
        delay(1_000)
        onTimeoutActual()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "PetCare", fontSize = 32.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun HomePendiente() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Inicio (pendiente de Persona A)")
    }
}
