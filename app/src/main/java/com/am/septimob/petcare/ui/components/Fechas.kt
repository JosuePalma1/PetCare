package com.am.septimob.petcare.ui.components

import java.util.TimeZone

// Las fechas se guardan como "días desde 1970" (fechaNacimiento de Mascota y fecha de Cuidado)
internal const val MILIS_POR_DIA = 86_400_000L

/** Fecha de hoy, en la zona horaria del teléfono, como días desde 1970. */
internal fun hoyEnDias(): Long {
    val ahora = System.currentTimeMillis()
    val local = ahora + TimeZone.getDefault().getOffset(ahora)
    return Math.floorDiv(local, MILIS_POR_DIA)
}
