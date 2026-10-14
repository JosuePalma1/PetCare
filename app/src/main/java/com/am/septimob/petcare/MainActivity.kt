package com.am.septimob.petcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
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
            val preferencias by preferenciasRepository.preferencias.collectAsStateWithLifecycle(
                initialValue = UserPreferences()
            )

            PetCareTheme(darkTheme = preferencias.tema == Tema.OSCURO) {
                PetCareApp(windowSizeClass = windowSizeClass)
            }
        }
    }
}
