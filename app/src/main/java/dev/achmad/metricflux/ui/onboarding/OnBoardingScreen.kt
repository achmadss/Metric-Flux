package dev.achmad.metricflux.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.achmad.metricflux.R
import dev.achmad.metricflux.ui.home.HomeScreen
import dev.achmad.metricflux.util.PermissionState
import dev.achmad.metricflux.util.rememberPermissionState
import dev.achmad.metricflux.util.rememberUsageStatsPermissionState

object OnBoardingScreen: Screen {
    private fun readResolve(): Any = OnBoardingScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val dataUsagePermission = rememberUsageStatsPermissionState()
        val mobileNetworkPermission = rememberPermissionState(Manifest.permission.READ_PHONE_STATE)
        val notificationPermission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
            }
            else -> {
                PermissionState(
                    permission = "android.permission.POST_NOTIFICATIONS",
                    isGranted = remember { mutableStateOf(true) },
                    requestPermission = {}
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .systemBarsPadding()
                    .padding(
                        top = 64.dp,
                        start = 32.dp,
                        end = 32.dp
                    ),
            ) {
                Text(
                    text = stringResource(R.string.onboarding_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.onboarding_subtitle),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(36.dp))
                Text(
                    text = stringResource(R.string.onboarding_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.onboarding_note),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(36.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(
                                    alpha = 0.2f
                                )
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp,
                        ),
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_label_required),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_permission_access_data_usage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (dataUsagePermission.isGranted.value) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ButtonDefaults.textButtonColors().contentColor
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .clickable {
                                        dataUsagePermission.requestPermission()
                                    },
                                text = stringResource(R.string.onboarding_button_grant),
                                color = ButtonDefaults.textButtonColors().contentColor,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_permission_mobile_network),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (mobileNetworkPermission.isGranted.value) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ButtonDefaults.textButtonColors().contentColor
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .clickable {
                                        mobileNetworkPermission.requestPermission()
                                    },
                                text = stringResource(R.string.onboarding_button_grant),
                                color = ButtonDefaults.textButtonColors().contentColor,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.onboarding_label_optional),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.5f
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_permission_notification),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (notificationPermission.isGranted.value) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ButtonDefaults.textButtonColors().contentColor
                            )
                        } else {
                            Text(
                                modifier = Modifier
                                    .clickable {
                                        notificationPermission.requestPermission()
                                    },
                                text = stringResource(R.string.onboarding_button_grant),
                                color = ButtonDefaults.textButtonColors().contentColor,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.align(Alignment.End),
                    enabled = dataUsagePermission.isGranted.value && mobileNetworkPermission.isGranted.value,
                    onClick = {
                        navigator.replace(HomeScreen)
                    },
                    contentPadding = PaddingValues(
                        start = 64.dp,
                        end = 48.dp
                    )
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_button_next),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }

    }
}