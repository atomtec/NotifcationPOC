package com.notification.poc

import android.app.Application
import com.notification.poc.di.appModule
import com.notification.poc.di.repoModule
import com.notification.poc.di.viewModelModule
import com.notiifcation.poc.di.remoteDataSource
import org.koin.core.logger.Level
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin



class NotificationPOCApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@NotificationPOCApp)
            modules(listOf(appModule, repoModule, viewModelModule,remoteDataSource))
        }
    }


}