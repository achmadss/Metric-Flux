package dev.achmad.data.model

import android.net.NetworkCapabilities

/**
 * Represents supported network transports, mapping to Android's NetworkCapabilities constants.
 */
enum class NetworkType(val type: Int) {
    WIFI(NetworkCapabilities.TRANSPORT_WIFI),
    CELLULAR(NetworkCapabilities.TRANSPORT_CELLULAR),
    ETHERNET(NetworkCapabilities.TRANSPORT_ETHERNET),
    VPN(NetworkCapabilities.TRANSPORT_VPN);

    companion object {
        operator fun invoke(type: Int): NetworkType {
            return entries.firstOrNull { it.type == type }
                ?: throw IllegalArgumentException("Unknown network type: \$type")
        }
    }
}