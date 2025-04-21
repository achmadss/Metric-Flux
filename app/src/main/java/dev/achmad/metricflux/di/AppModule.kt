package dev.achmad.metricflux.di

import dev.achmad.core.di.coreModule
import dev.achmad.data.di.dataModule
import dev.achmad.metricflux.preference.AppPreference
import org.koin.dsl.module

val appModule = module {
    includes(coreModule)
    includes(dataModule)
    single<AppPreference> { AppPreference(get()) }
}