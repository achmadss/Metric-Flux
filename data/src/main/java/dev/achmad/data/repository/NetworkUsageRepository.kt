package dev.achmad.data.repository

import android.Manifest
import android.content.Context
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import dev.achmad.core.network.NetworkUsageManager
import dev.achmad.data.dao.NetworkUsageDao
import dev.achmad.data.model.NetworkType
import dev.achmad.data.model.NetworkUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkUsageRepository(
    private val networkUsageManager: NetworkUsageManager,
    private val dao: NetworkUsageDao,
) {
    private val bucketIntervalMs = 3 * 60 * 60 * 1000L // 3 hours in milliseconds

    /**
     * Aligns any timestamp to its 3‑hour bucket window.
     */
    private fun getBucketWindow(timestampMs: Long): Pair<Long, Long> {
        val start = (timestampMs / bucketIntervalMs) * bucketIntervalMs
        val end = start + bucketIntervalMs - 1
        return start to end
    }

    private fun isWindowOngoing(startTimeMs: Long, endTimeMs: Long): Boolean {
        val now = System.currentTimeMillis()
        return now in startTimeMs..endTimeMs
    }

    /**
     * Fetches or caches device‑wide usage for the 3‑hour bucket containing `timestampMs`.
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    suspend fun getOrCacheDeviceUsage(
        networkType: NetworkType,
        timestampMs: Long
    ): NetworkUsage = withContext(Dispatchers.IO) {
        val uid = -1
        val (startTimeMs, endTimeMs) = getBucketWindow(timestampMs)
        val ongoing = isWindowOngoing(startTimeMs, endTimeMs)

        if (!ongoing) {
            dao.findByWindow(networkType, uid, startTimeMs, endTimeMs)
                ?.let { return@withContext it }
        }

        val (rx, tx) = networkUsageManager.getDeviceDataUsage(
            networkType.type,
            startTimeMs,
            endTimeMs
        )

        val networkUsage = NetworkUsage(
            networkType = networkType,
            uid         = uid,
            startTimeMs = startTimeMs,
            endTimeMs   = endTimeMs,
            rxBytes     = rx,
            txBytes     = tx
        )

        if (!ongoing) {
            dao.insert(networkUsage)
        }

        networkUsage
    }

    /**
     * Fetches or caches app usage for the 3‑hour bucket containing `timestampMs`.
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    suspend fun getOrCacheAppUsage(
        networkType: NetworkType,
        uid: Int,
        timestampMs: Long
    ): NetworkUsage = withContext(Dispatchers.IO) {
        val (startTimeMs, endTimeMs) = getBucketWindow(timestampMs)
        val ongoing = isWindowOngoing(startTimeMs, endTimeMs)

        if (!ongoing) {
            dao.findByWindow(networkType, uid, startTimeMs, endTimeMs)
                ?.let { return@withContext it }
        }

        val (rx, tx) = networkUsageManager.getAppDataUsageByUID(
            networkType.type,
            uid,
            startTimeMs,
            endTimeMs
        )

        val appName = networkUsageManager.getAllAppUIDs()[uid] ?: "Unknown App"
        val networkUsage = NetworkUsage(
            networkType = networkType,
            uid         = uid,
            appName     = appName,
            startTimeMs = startTimeMs,
            endTimeMs   = endTimeMs,
            rxBytes     = rx,
            txBytes     = tx
        )

        if (!ongoing) {
            dao.insert(networkUsage)
        }

        networkUsage
    }

    /**
     * Bootstraps historical usage for all apps, querying backwards in 3‑hour buckets until no data remains.
     * Skips the current incomplete bucket.
     */
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    suspend fun bootstrapAllAppUsage(
        networkType: NetworkType
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val (currentStart, _) = getBucketWindow(now)
        val lastCompletedEnd = currentStart - 1L

        // Fetch mapping of UID to app name
        val appMap = networkUsageManager.getAllAppUIDs()

        for ((uid, name) in appMap) {
            // Resolve install time to know how far back to go
            val installTime = networkUsageManager.getFirstInstallTimeForUid(uid)

            var bucketEnd = lastCompletedEnd
            while (bucketEnd >= installTime) {
                val bucketStart = (bucketEnd / bucketIntervalMs) * bucketIntervalMs
                val bucketWindowEnd = bucketStart + bucketIntervalMs - 1L

                // Skip if already cached
                if (dao.findByWindow(networkType, uid, bucketStart, bucketWindowEnd) != null) {
                    bucketEnd = bucketStart - 1L
                    continue
                }

                // Fetch usage for this bucket
                val (rx, tx) = networkUsageManager.getAppDataUsageByUID(
                    networkType.type,
                    uid,
                    bucketStart,
                    bucketWindowEnd
                )

                // Persist only if there was any usage
                if (rx + tx > 0) {
                    dao.insert(
                        NetworkUsage(
                            networkType = networkType,
                            uid         = uid,
                            appName     = name,
                            startTimeMs = bucketStart,
                            endTimeMs   = bucketWindowEnd,
                            rxBytes     = rx,
                            txBytes     = tx
                        )
                    )
                }

                // Move to the previous bucket
                bucketEnd = bucketStart - 1L
            }
        }
    }

    /**
     * Returns all cached buckets in descending order of their start time.
     */
    suspend fun getHistory() = withContext(Dispatchers.IO) {
        dao.getAllUsage()
    }
}