package dev.achmad.metricflux.preference

import dev.achmad.core.preference.PreferenceStore
import dev.achmad.core.preference.getEnum
import dev.achmad.metricflux.ui.theme.AppTheme

class AppPreference(
    private val preferenceStore: PreferenceStore,
) {
    fun appTheme() = preferenceStore.getEnum("app_theme", AppTheme.SYSTEM)
    fun bootStrapInitialized() = preferenceStore.getBoolean("bootstrap_initialized", false)
}