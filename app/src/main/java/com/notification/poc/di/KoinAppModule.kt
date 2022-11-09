package com.notification.poc.di

import android.app.Application
import androidx.room.Room
import com.notification.poc.NotificationPOCApp
import com.notification.poc.TopicViewModel
import com.notification.poc.data.source.TopicDataSource
import com.notification.poc.data.source.TopicRepository
import com.notification.poc.data.source.local.EndPointDao
import com.notification.poc.data.source.local.TopicDao
import com.notification.poc.data.source.local.TopicDataBase
import com.notification.poc.data.source.local.TopicLocalDataSource
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DB_NAME = "Topics.db"

val appModule = module {

    fun provideDataBase(application: Application): TopicDataBase {
        return Room.databaseBuilder(
            application,
            TopicDataBase::class.java, DB_NAME,
        ).build()
    }



    fun provideTopicDao(dataBase: TopicDataBase): TopicDao {
        return dataBase.topicDao()
    }

    fun provideEndPointDao(dataBase: TopicDataBase): EndPointDao {
        return dataBase.endpointDao()
    }


    single { provideDataBase(androidApplication()) }
    single { Dispatchers.IO }
    single <TopicDataSource>(named("TopicLocalDataSource"))
    { TopicLocalDataSource(provideTopicDao(get()),
        provideEndPointDao(get()),get())
    }

}

val repoModule = module {

    single { TopicRepository(get(named("TopicRemoteDataSource")),
        get(named("TopicLocalDataSource")), get(), androidApplication() as NotificationPOCApp) }
}

val viewModelModule = module {
    viewModel {
        TopicViewModel(get())
    }
}

