package dev.achmad.metricflux.ui.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.achmad.core.network.NetworkUsageManager
import dev.achmad.core.util.inject
import dev.achmad.data.model.NetworkType
import dev.achmad.data.repository.NetworkUsageRepository
import dev.achmad.metricflux.preference.AppPreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class HomeScreenType { LOADING, SUCCESS, ERROR }
data class HomeScreenApps(
    val name: String,
    val wifiReceivedUsage: String,
    val wifiTransferUsage: String,
    val mobileReceivedUsage: String,
    val mobileTransferUsage: String,
) {
    fun totalUsage() = wifiReceivedUsage + wifiTransferUsage + mobileReceivedUsage + mobileTransferUsage
}

data class HomeScreenState(
    val screenType: HomeScreenType = HomeScreenType.LOADING,
    val apps: List<HomeScreenApps> = emptyList()
)

@SuppressLint("MissingPermission")
class HomeScreenViewModel(
    private val networkUsageRepository: NetworkUsageRepository = inject(),
    private val networkUsageManager: NetworkUsageManager = inject(),
    private val appPreferences: AppPreference = inject(),
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState = _uiState.asStateFlow()

    private val appUIDs = networkUsageManager.getAllAppUIDs()

    init {
        getTodayNetworkUsage()
    }

    fun getTodayNetworkUsage() {
        viewModelScope.launch {
            bootstrap {
                appUIDs.forEach { (uid, name) ->
                    val networkUsage = networkUsageRepository.getOrCacheAppUsage(
                        networkType = NetworkType.WIFI,
                        uid = uid,
                        timestampMs = System.currentTimeMillis()
                    )
                }
            }
        }
    }

    private suspend fun bootstrap(
        onSuccess: suspend () -> Unit,
    ) {
        val shouldBootstrap = appPreferences.bootStrapInitialized().get().not()
        if (!shouldBootstrap) {
            onSuccess()
            return
        }
        networkUsageRepository.bootstrapAllAppUsage(NetworkType.WIFI)
        networkUsageRepository.bootstrapAllAppUsage(NetworkType.CELLULAR)
        onSuccess()
    }

}