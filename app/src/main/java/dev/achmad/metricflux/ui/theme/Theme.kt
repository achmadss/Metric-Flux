package dev.achmad.metricflux.ui.theme

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import dev.achmad.core.util.inject
import dev.achmad.metricflux.preference.AppPreference

enum class AppTheme { LIGHT, DARK, SYSTEM }

@Composable
fun MetricFluxTheme(
    content: @Composable () -> Unit
) {
    val appPreference = remember { inject<AppPreference>() }
    val appTheme = appPreference.appTheme().get()
    val darkMode = when(appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        darkMode -> darkColorScheme()
        else -> lightColorScheme()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}

@Suppress("DEPRECATION")
@Composable
fun SystemBarColor(color: Color?, darkTheme: Boolean = false) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            color?.let {
                val argb = it.toArgb()
                window.navigationBarColor = argb
                window.statusBarColor = argb
            }
            val insetsController = window.decorView.let(ViewCompat::getWindowInsetsController)
            insetsController?.let {
                it.apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
}

@Suppress("DEPRECATION")
@Composable
fun NavigationBarColor(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = color.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
}

@Suppress("DEPRECATION")
@Composable
fun StatusBarColor(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = color.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
}