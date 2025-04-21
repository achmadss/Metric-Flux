package dev.achmad.core.network

import android.Manifest
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import java.util.Locale

class NetworkUsageManager(private val context: Context) {

    /**
     * Gets the total device data usage for a specific network type and time period
     * @param type Network type (e.g., NetworkCapabilities.TRANSPORT_CELLULAR or NetworkCapabilities.TRANSPORT_WIFI)
     * @param startTime Start time in milliseconds
     * @param endTime End time in milliseconds
     * @return Pair of received bytes and transmitted bytes
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getDeviceDataUsage(
        type: Int,
        startTime: Long,
        endTime: Long,
    ): Pair<Long, Long> {
        val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

        var rxBytes: Long = 0
        var txBytes: Long = 0

        try {
            val summary = networkStatsManager.querySummaryForDevice(
                type,
                null,
                startTime,
                endTime
            )

            rxBytes = summary.rxBytes
            txBytes = summary.txBytes

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Pair(rxBytes, txBytes)
    }

    /**
     * Gets data usage for a specific app (identified by UID)
     * @param type Network type (e.g., NetworkCapabilities.TRANSPORT_CELLULAR or NetworkCapabilities.TRANSPORT_WIFI)
     * @param uid Application UID
     * @param startTime Start time in milliseconds
     * @param endTime End time in milliseconds
     * @return Pair of received bytes and transmitted bytes
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getAppDataUsageByUID(
        type: Int,
        uid: Int,
        startTime: Long,
        endTime: Long,
    ): Pair<Long, Long> {
        val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

        var rxBytes: Long = 0
        var txBytes: Long = 0

        try {
            val summary = networkStatsManager.queryDetailsForUid(
                type,
                null,
                startTime, endTime,
                uid
            )
            val bucket = NetworkStats.Bucket()
            while (summary.hasNextBucket()) {
                summary.getNextBucket(bucket)
                rxBytes += bucket.rxBytes
                txBytes += bucket.txBytes
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Pair(rxBytes, txBytes)
    }

    /**
     * Gets a map of all application UIDs to their friendly names
     * @return Map where key is UID and value is app name
     */
    fun getAllAppUIDs(): Map<Int, String> {
        val packageManager = context.packageManager
        val installedPackages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        val uidToPackageMap = mutableMapOf<Int, String>()

        for (appInfo in installedPackages) {
            uidToPackageMap[appInfo.uid] = getAppNameFromPackageName(appInfo.packageName)
        }

        return uidToPackageMap
    }

    /**
     * Gets the friendly app name from a package name
     * @param packageName The package name of the app
     * @return User-friendly app name
     */
    private fun getAppNameFromPackageName(packageName: String): String {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown App" // Default value if package name is not found
        }
    }

    fun getFirstInstallTimeForUid(uid: Int): Long {
        val packages = context.packageManager.getPackagesForUid(uid)
        val installTime = if (!packages.isNullOrEmpty()) {
            val pkgInfo = context.packageManager.getPackageInfo(packages[0], 0)
            pkgInfo.firstInstallTime
        } else 0L
        return installTime
    }

    companion object {
        /**
         * Formats bytes into a human-readable string (B, KB, MB, GB)
         * @return Formatted string representation of bytes
         */
        fun formatBytes(bytes: Long): String {
            val kilobyte: Long = 1000
            val megabyte = kilobyte * 1000
            val gigabyte = megabyte * 1000

            return when {
                bytes < kilobyte -> "$bytes B"
                bytes < megabyte -> String.format(Locale.getDefault(), "%.2f KB", bytes / kilobyte.toFloat())
                bytes < gigabyte -> String.format(Locale.getDefault(), "%.2f MB", bytes / megabyte.toFloat())
                else -> String.format(Locale.getDefault(), "%.2f GB", bytes / gigabyte.toFloat())
            }
        }
    }
}