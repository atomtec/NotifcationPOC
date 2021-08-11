package com.notiifcation.poc.di

import android.content.Context
import com.notification.poc.data.source.TopicDataSource
import com.notification.poc.di.AppModule
import com.notiifcation.poc.data.source.remote.RemoteDataSourceAzure
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
object RemoteProvider {
    @Provides
    @Singleton
    @AppModule.TopicRemoteDataSource
    fun provideTopicRemoteDataSource(): TopicDataSource {
        return RemoteDataSourceAzure
    }

}