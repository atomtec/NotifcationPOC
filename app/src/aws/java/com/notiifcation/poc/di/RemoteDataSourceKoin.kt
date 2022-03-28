package com.notiifcation.poc.di

import com.notification.poc.data.source.TopicDataSource
import com.notiifcation.poc.data.source.remote.TopicRemoteDataSourceSNS
import org.koin.core.qualifier.named
import org.koin.dsl.module

val remoteDataSource = module {
    single<TopicDataSource>(named("TopicRemoteDataSource")) { TopicRemoteDataSourceSNS }
}