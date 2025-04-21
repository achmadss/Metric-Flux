package dev.achmad.data.converter

import androidx.room.TypeConverter
import dev.achmad.data.model.NetworkType

class MetricFluxConverter {
    @TypeConverter
    fun fromNetworkType(networkType: NetworkType): Int = networkType.type

    @TypeConverter
    fun toNetworkType(type: Int): NetworkType = NetworkType(type)
}