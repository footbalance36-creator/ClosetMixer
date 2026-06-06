package com.closetmixer.android.di

import app.cash.sqldelight.db.SqlDriver
import com.closetmixer.android.data.AndroidSettingsStorage
import com.closetmixer.data.db.DatabaseDriverFactory
import com.closetmixer.data.storage.SettingsStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<SqlDriver> { DatabaseDriverFactory(androidContext()).createDriver() }
    single<SettingsStorage> { AndroidSettingsStorage(androidContext()) }
}
