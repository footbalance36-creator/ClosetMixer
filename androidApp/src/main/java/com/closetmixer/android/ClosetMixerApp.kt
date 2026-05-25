package com.closetmixer.android

import android.app.Application
import com.closetmixer.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ClosetMixerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ClosetMixerApp)
            modules(sharedModule)
        }
    }
}
