package dev.achmad.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a 3‑hour usage bucket for device or per app.
 */
@Entity(tableName = "network_usage")
data class NetworkUsage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** Transport type stored as NetworkType enum */
    val networkType: NetworkType,

    /** App UID (-1 for device‑wide usage) */
    val uid: Int = -1,

    /** Friendly app name (empty for device‑wide entries) */
    val appName: String = "",

    /** Start of 3‑hour window, inclusive */
    val startTimeMs: Long,

    /** End of 3‑hour window, inclusive */
    val endTimeMs: Long,

    val rxBytes: Long,
    val txBytes: Long
) {

}