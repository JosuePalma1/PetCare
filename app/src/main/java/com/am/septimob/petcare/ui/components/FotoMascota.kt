package com.am.septimob.petcare.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Carga la foto de la mascota reducida al tamaño en que se muestra y fuera del hilo principal.
 * Devuelve null mientras carga, si no hay foto o si no se puede leer (p. ej. se borró el archivo).
 */
@Composable
fun rememberFotoMascota(fotoUri: String?, tamano: Dp): ImageBitmap? {
    val context = LocalContext.current
    val tamanoPx = with(LocalDensity.current) { tamano.roundToPx() }

    val foto by produceState<ImageBitmap?>(initialValue = null, fotoUri, tamanoPx) {
        value = fotoUri?.let { direccion ->
            withContext(Dispatchers.IO) {
                try {
                    decodificarReducida(context, direccion.toUri(), tamanoPx)?.asImageBitmap()
                } catch (_: Exception) {
                    null
                }
            }
        }
    }
    return foto
}

// Una foto de cámara (4000 px) ocupa ~48 MB en memoria; se submuestrea en potencias de 2
// hasta quedar justo por encima del tamaño pedido.
private fun decodificarReducida(context: Context, uri: Uri, tamanoPx: Int): Bitmap? {
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }
    if (limites.outWidth <= 0 || limites.outHeight <= 0) return null

    var muestra = 1
    while (limites.outWidth / (muestra * 2) >= tamanoPx &&
        limites.outHeight / (muestra * 2) >= tamanoPx
    ) {
        muestra *= 2
    }

    val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
    return context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, opciones)
    }
}
