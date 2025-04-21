package dev.achmad.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.achmad.data.model.NetworkType
import dev.achmad.data.model.NetworkUsage

@Dao
interface NetworkUsageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usage: NetworkUsage)

    @Query("""
      SELECT *
        FROM network_usage
       WHERE networkType = :networkType
         AND uid         = :uid
         AND startTimeMs = :startTimeMs
         AND endTimeMs   = :endTimeMs
       LIMIT 1
    """)
    suspend fun findByWindow(
        networkType: NetworkType,
        uid: Int,
        startTimeMs: Long,
        endTimeMs: Long
    ): NetworkUsage?

    @Query("SELECT * FROM network_usage ORDER BY startTimeMs DESC")
    suspend fun getAllUsage(): List<NetworkUsage>

    @Query("DELETE FROM network_usage")
    suspend fun clearAll()
}