package com.closetmixer.di

import com.closetmixer.data.db.DatabaseDriverFactory
import org.koin.dsl.module

val iosModule = module {
    single { DatabaseDriverFactory().createDriver() }
}
