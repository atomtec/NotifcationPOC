package com.notiifcation.poc.di

import com.notification.poc.data.source.TopicDataSource
import com.notification.poc.di.AppModule
import com.notiifcation.poc.data.source.remote.TopicRemoteDataSourceSNS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemoteProvider{
    @Provides
    @Singleton
    @AppModule.TopicRemoteDataSource
    fun provideTopicRemoteDataSource(): TopicDataSource {
        return TopicRemoteDataSourceSNS
    }

}