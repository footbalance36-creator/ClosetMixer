package com.closetmixer.android.di

import app.cash.sqldelight.db.SqlDriver
import com.closetmixer.data.db.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<SqlDriver> { DatabaseDriverFactory(androidContext()).createDriver() }
}
