package com.aca.people.presentation.main

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.aca.people.presentation.navigation.AppNavigation
import com.aca.people.presentation.util.resource.theme.AppTheme
import com.aca.people.presentation.util.resource.theme.UserTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserTheme(appTheme = AppTheme.Light) {
                // Se extrae la lógica a un Composable limpio y reutilizable.
                val activity = (LocalContext.current as? Activity)
                val onExitApp: () -> Unit = { activity?.finish() }
                // Aquí se renderiza la navegación principal de tu aplicación.
                AppNavigation(onExitApp = onExitApp)            }
        }
    }
}
