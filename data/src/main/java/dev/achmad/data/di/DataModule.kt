package dev.achmad.data.di

import androidx.room.Room
import dev.achmad.data.dao.NetworkUsageDao
import dev.achmad.data.database.MetricFluxDatabase
import dev.achmad.data.repository.NetworkUsageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<MetricFluxDatabase> {
        Room.databaseBuilder(
            context = androidContext(),
            klass = MetricFluxDatabase::class.java,
            name = "metric_flux_database.db"
        ).build()
    }
    single<NetworkUsageDao> {
        get<MetricFluxDatabase>().networkUsageDao()
    }
    single<NetworkUsageRepository> {
        NetworkUsageRepository(
            networkUsageManager = get(),
            dao = get(),
        )
    }
}