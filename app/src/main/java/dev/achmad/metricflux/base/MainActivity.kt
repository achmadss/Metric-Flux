package dev.achmad.metricflux.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.ScreenTransition
import dev.achmad.metricflux.ui.home.HomeScreen
import dev.achmad.metricflux.ui.onboarding.OnBoardingScreen
import dev.achmad.metricflux.ui.theme.MetricFluxTheme
import dev.achmad.metricflux.util.isMobileNetworkPermissionAllowed
import dev.achmad.metricflux.util.isUsageStatsPermissionAllowed
import soup.compose.material.motion.animation.materialSharedAxisX
import soup.compose.material.motion.animation.rememberSlideDistance

class MainActivity : ComponentActivity() {

    private val hasRequiredPermissions: Boolean
        get() {
            return isUsageStatsPermissionAllowed(this) &&
                    isMobileNetworkPermissionAllowed(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(
            if (hasRequiredPermissions) savedInstanceState else null
        )
        enableEdgeToEdge()
        setContent {
            MetricFluxTheme {
                val slideDistance = rememberSlideDistance()
                Navigator(
                    screen = when {
                        hasRequiredPermissions -> HomeScreen
                        else -> OnBoardingScreen
                    },
                    disposeBehavior = NavigatorDisposeBehavior(
                        disposeNestedNavigators = false,
                        disposeSteps = true
                    )
                ) { navigator ->
                    ScreenTransition(
                        navigator = navigator,
                        transition = {
                            materialSharedAxisX(
                                forward = navigator.lastEvent != StackEvent.Pop,
                                slideDistance = slideDistance,
                            )
                        },
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (hasRequiredPermissions) {
            super.onSaveInstanceState(outState)
        }
    }
}