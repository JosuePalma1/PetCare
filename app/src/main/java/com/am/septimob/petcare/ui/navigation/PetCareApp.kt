package com.am.septimob.petcare.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.am.septimob.petcare.ui.configuracion.ConfiguracionRoute
import com.am.septimob.petcare.ui.cuidadoform.CuidadoFormRoute
import com.am.septimob.petcare.ui.detallemascota.DetalleMascotaRoute
import com.am.septimob.petcare.ui.home.HomeRoute
import com.am.septimob.petcare.ui.mascotaform.MascotaFormRoute
import com.am.septimob.petcare.ui.mascotas.MascotasRoute
import com.am.septimob.petcare.ui.splash.SplashRoute
import kotlin.reflect.KClass

private data class PestanaInferior(
    val ruta: PetCareRoute,
    val claseRuta: KClass<out PetCareRoute>,
    val etiqueta: String,
    val icono: ImageVector
)

private val pestanas = listOf(
    PestanaInferior(PetCareRoute.Home, PetCareRoute.Home::class, "Inicio", Icons.Default.Home),
    PestanaInferior(PetCareRoute.Mascotas, PetCareRoute.Mascotas::class, "Mascotas", Icons.Default.Pets),
    PestanaInferior(
        PetCareRoute.Configuracion,
        PetCareRoute.Configuracion::class,
        "Ajustes",
        Icons.Default.Settings
    )
)

@Composable
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun PetCareApp(
    windowSizeClass: WindowSizeClass,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destinoActual = backStackEntry?.destination
    // La barra se basa en lo que el NavHost está dibujando, no solo en la ruta actual: así no aparece
    // encima de una pantalla que aún se está yendo (p. ej. el último frame del Splash)
    val entradasVisibles by navController.visibleEntries.collectAsStateWithLifecycle()
    val mostrarNavegacion = entradasVisibles.isNotEmpty() && entradasVisibles.all { entrada ->
        pestanas.any { entrada.destination.esDe(it.claseRuta) }
    }
    val usarRail = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    Scaffold(
        bottomBar = {
            if (mostrarNavegacion && !usarRail) {
                PetCareBottomBar(navController = navController, destinoActual = destinoActual)
            }
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            if (mostrarNavegacion && usarRail) {
                PetCareNavigationRail(
                    navController = navController,
                    destinoActual = destinoActual
                )
            }

            NavHost(
                navController = navController,
                startDestination = PetCareRoute.Splash,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .imePadding()
            ) {
                // Splash → Inicio sin fundido: si no, el Splash se ve un instante con la barra inferior encima
                composable<PetCareRoute.Splash>(exitTransition = { ExitTransition.None }) {
                    SplashRoute(
                        onFinished = {
                            navController.navigate(PetCareRoute.Home) {
                                popUpTo<PetCareRoute.Splash> { inclusive = true }
                            }
                        }
                    )
                }

                composable<PetCareRoute.Home>(
                    enterTransition = {
                        if (initialState.destination.hasRoute(PetCareRoute.Splash::class)) {
                            EnterTransition.None
                        } else {
                            null
                        }
                    }
                ) {
                    HomeRoute(
                        onVerMascotasClick = { navController.seleccionar(pestanas[1]) },
                        onRegistrarMascotaClick = {
                            navController.navigate(PetCareRoute.MascotaForm())
                        },
                        isExpanded = usarRail
                    )
                }

                composable<PetCareRoute.Mascotas> {
                    MascotasRoute(
                        onAgregarMascota = { navController.navigate(PetCareRoute.MascotaForm()) },
                        onMascotaClick = { id ->
                            navController.navigate(PetCareRoute.DetalleMascota(id))
                        },
                        isExpanded = usarRail
                    )
                }

                composable<PetCareRoute.MascotaForm> { entry ->
                    MascotaFormRoute(
                        onCancelar = { navController.volverDesde(entry) },
                        onGuardadoExitoso = { navController.volverDesde(entry) }
                    )
                }

                composable<PetCareRoute.DetalleMascota> { entry ->
                    val mascotaId = entry.toRoute<PetCareRoute.DetalleMascota>().mascotaId

                    DetalleMascotaRoute(
                        onEditarMascota = {
                            navController.navigate(PetCareRoute.MascotaForm(mascotaId))
                        },
                        onRegistrarCuidado = {
                            navController.navigate(PetCareRoute.CuidadoForm(mascotaId))
                        },
                        onCuidadoClick = { cuidadoId ->
                            navController.navigate(PetCareRoute.CuidadoForm(mascotaId, cuidadoId))
                        },
                        // La mascota ya no existe: se vuelve directo a la lista
                        onMascotaEliminada = {
                            navController.popBackStack<PetCareRoute.Mascotas>(inclusive = false)
                        },
                        onVolver = { navController.volverDesde(entry) },
                        isExpanded = usarRail
                    )
                }

                composable<PetCareRoute.CuidadoForm> { entry ->
                    CuidadoFormRoute(
                        onCancelar = { navController.volverDesde(entry) },
                        onListo = { navController.volverDesde(entry) }
                    )
                }

                composable<PetCareRoute.Configuracion> {
                    ConfiguracionRoute()
                }
            }
        }
    }
}

@Composable
private fun PetCareBottomBar(
    navController: NavHostController,
    destinoActual: NavDestination?
) {
    NavigationBar {
        pestanas.forEach { pestana ->
            NavigationBarItem(
                selected = destinoActual.esDe(pestana.claseRuta),
                onClick = { navController.seleccionar(pestana) },
                icon = { Icon(imageVector = pestana.icono, contentDescription = null) },
                label = { Text(pestana.etiqueta) }
            )
        }
    }
}

@Composable
private fun PetCareNavigationRail(
    navController: NavHostController,
    destinoActual: NavDestination?
) {
    NavigationRail(modifier = Modifier.fillMaxHeight()) {
        pestanas.forEach { pestana ->
            NavigationRailItem(
                selected = destinoActual.esDe(pestana.claseRuta),
                onClick = { navController.seleccionar(pestana) },
                icon = { Icon(imageVector = pestana.icono, contentDescription = null) },
                label = { Text(pestana.etiqueta) }
            )
        }
    }
}

private fun NavHostController.seleccionar(pestana: PestanaInferior) {
    navigate(pestana.ruta) {
        // Una sola copia de cada pestaña, conservando su estado al cambiar
        popUpTo<PetCareRoute.Home> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavDestination?.esDe(claseRuta: KClass<out PetCareRoute>): Boolean =
    this?.hierarchy?.any { it.hasRoute(claseRuta) } == true

// Evita un doble "atrás" si se pulsa dos veces seguidas: solo vuelve si la pantalla está activa
private fun NavHostController.volverDesde(entry: NavBackStackEntry) {
    if (entry.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
        popBackStack()
    }
}
