package dev.achmad.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.achmad.data.converter.MetricFluxConverter
import dev.achmad.data.dao.NetworkUsageDao
import dev.achmad.data.model.NetworkUsage

@Database(
    entities = [NetworkUsage::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MetricFluxConverter::class)
abstract class MetricFluxDatabase : RoomDatabase() {

    abstract fun networkUsageDao(): NetworkUsageDao

}