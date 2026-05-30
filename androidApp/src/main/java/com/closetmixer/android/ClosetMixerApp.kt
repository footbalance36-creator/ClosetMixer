package com.closetmixer.android

import android.app.Application
import com.closetmixer.android.data.DataSeeder
import com.closetmixer.android.di.androidModule
import com.closetmixer.db.ClosetDatabase
import com.closetmixer.di.sharedModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

class ClosetMixerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ClosetMixerApp)
            modules(androidModule, sharedModule)
        }
        CoroutineScope(Dispatchers.IO).launch {
            val db: ClosetDatabase = getKoin().get()
            DataSeeder.seedIfEmpty(db)
        }
    }
}
