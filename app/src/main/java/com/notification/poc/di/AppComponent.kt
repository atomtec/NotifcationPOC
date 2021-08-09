package com.notification.poc.di

import com.notification.poc.data.source.TopicRepository
import com.notiifcation.poc.di.RemoteProvider

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        RemoteProvider::class,
        ContextModule::class
    ]
)
interface AppComponent {
    val topicRepository: TopicRepository
}