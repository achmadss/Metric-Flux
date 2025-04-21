package dev.achmad.metricflux.util

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

class PermissionState internal constructor(
    val permission: String,
    val isGranted: State<Boolean>,
    val requestPermission: () -> Unit
)

class MultiplePermissionsState internal constructor(
    val permissions: Map<String, Boolean>,
    val requestPermissions: () -> Unit
)

class UsageStatsPermissionState internal constructor(
    val isGranted: State<Boolean>,
    val requestPermission: () -> Unit
)

@Composable
fun rememberPermissionState(
    permission: String
): PermissionState {
    val context = LocalContext.current
    val activity = LocalActivity.current

    val permissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted.value = isGranted
    }

    return remember(permission) {
        PermissionState(
            permission = permission,
            isGranted = permissionGranted,
            requestPermission = {
                if (activity != null && !permissionGranted.value) {
                    launcher.launch(permission)
                }
            }
        )
    }
}

@Composable
fun rememberMultiplePermissionsState(
    permissions: List<String>
): MultiplePermissionsState {
    val context = LocalContext.current
    val permissionResults = remember {
        mutableStateMapOf<String, Boolean>().apply {
            permissions.forEach { permission ->
                val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                put(permission, granted)
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultMap ->
        resultMap.forEach { (permission, granted) ->
            permissionResults[permission] = granted
        }
    }

    return remember(permissions) {
        MultiplePermissionsState(
            permissions = permissionResults,
            requestPermissions = {
                launcher.launch(permissions.toTypedArray())
            }
        )
    }
}

@Composable
fun rememberUsageStatsPermissionState(): UsageStatsPermissionState {
    val context = LocalContext.current
    val permissionGranted = remember {
        mutableStateOf(isUsageStatsPermissionAllowed(context))
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    // Recheck permission when coming back to foreground
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionGranted.value = isUsageStatsPermissionAllowed(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val openSettings = {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    return remember {
        UsageStatsPermissionState(
            isGranted = permissionGranted,
            requestPermission = openSettings
        )
    }
}


fun isUsageStatsPermissionAllowed(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        context.applicationInfo.uid,
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

fun isMobileNetworkPermissionAllowed(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_PHONE_STATE
    ) == PackageManager.PERMISSION_GRANTED
}

fun isNotificationPermissionAllowed(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}