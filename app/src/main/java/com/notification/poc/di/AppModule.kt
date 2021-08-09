package com.notification.poc.di

import android.content.Context
import androidx.room.Room
import com.notification.poc.data.source.TopicDataSource
import com.notification.poc.data.source.local.TopicLocalDataSource
import com.notification.poc.data.source.local.TopicDataBase
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.annotation.AnnotationRetention.RUNTIME

@Module
object AppModule {

    @Qualifier
    @Retention(RUNTIME)
    annotation class TopicRemoteDataSource

    @Qualifier
    @Retention(RUNTIME)
    annotation class TopicLocalDataSource



    @JvmStatic
    @Singleton
    @TopicLocalDataSource
    @Provides
    fun provideTopicLocalDataSource(
        database: TopicDataBase,
        ioDispatcher: CoroutineDispatcher
    ): TopicDataSource {
        return TopicLocalDataSource(
            database.topicDao(),database.endpointDao(), ioDispatcher
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDataBase(context: Context): TopicDataBase {
        return Room.databaseBuilder(
            context.applicationContext,
            TopicDataBase::class.java, DB_NAME,
        ).build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

private const val DB_NAME = "Topics.db"