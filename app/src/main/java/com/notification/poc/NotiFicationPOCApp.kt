package com.notification.poc

import android.app.Application
import com.notification.poc.data.source.TopicRepository
import com.notification.poc.di.AppComponent
import com.notification.poc.di.ContextModule
import com.notification.poc.di.DaggerAppComponent


class NotiFicationPOCApp : Application() {

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    fun initializeComponent(): AppComponent {
        return DaggerAppComponent.builder().contextModule(ContextModule(applicationContext)).build()
    }


}