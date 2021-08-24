package com.notification.poc

import android.app.Application
import com.notification.poc.data.source.TopicRepository
import com.notification.poc.di.AppComponent
import com.notification.poc.di.ContextModule
import com.notification.poc.di.DaggerAppComponent


class NotificationPOCApp : Application() {

    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    private fun initializeComponent(): AppComponent {
        return DaggerAppComponent.builder().
        contextModule(ContextModule(applicationContext)).build().also {
            it.topicRepository.initProvider(this)
        }
    }


}