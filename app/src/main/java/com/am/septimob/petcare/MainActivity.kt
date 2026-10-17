package com.am.septimob.petcare

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.am.septimob.petcare.data.repository.PreferenciasRepository
import com.am.septimob.petcare.domain.model.Tema
import com.am.septimob.petcare.domain.model.UserPreferences
import com.am.septimob.petcare.ui.navigation.PetCareApp
import com.am.septimob.petcare.ui.theme.PetCareTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferenciasRepository: PreferenciasRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this@MainActivity)
            // null hasta leer DataStore: así no se pinta un instante en claro si el usuario eligió oscuro
            val preferencias by preferenciasRepository.preferencias
                .collectAsStateWithLifecycle<UserPreferences?>(initialValue = null)

            preferencias?.let { actuales ->
                val oscuro = actuales.tema == Tema.OSCURO

                // enableEdgeToEdge() por defecto sigue el tema del sistema; aquí los iconos de las barras
                // del sistema siguen el tema elegido en Ajustes (si no, quedan oscuros sobre fondo oscuro)
                LaunchedEffect(oscuro) {
                    val estilo = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { oscuro }
                    enableEdgeToEdge(statusBarStyle = estilo, navigationBarStyle = estilo)
                }

                PetCareTheme(darkTheme = oscuro) {
                    PetCareApp(windowSizeClass = windowSizeClass)
                }
            }
        }
    }
}
