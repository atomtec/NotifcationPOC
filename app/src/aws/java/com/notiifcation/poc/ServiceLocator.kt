
package com.notiifcation.poc

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.notification.poc.data.source.TopicDataSource
import com.notification.poc.data.source.TopicRepository
import com.notification.poc.data.source.local.TopicDataBase
import com.notification.poc.data.source.local.TopicLocalDataSource
import com.notiifcation.poc.data.source.remote.TopicRemoteDataSourceSNS
import kotlinx.coroutines.runBlocking

/**
 * A Service Locator for the [TopicsRepository]. This is the prod version, with a
 * the "real" [TapicRemoteDataSource].
 */
object ServiceLocator {

    private val lock = Any()
    private var database: TopicDataBase? = null
    @Volatile
    var topicRepository: TopicRepository? = null
        @VisibleForTesting set

    fun provideTopicRepository(context: Context): TopicRepository {
        synchronized(this) {
            return topicRepository ?: topicRepository ?: createTopicRepository(context)
        }
    }

    private fun createTopicRepository(context: Context): TopicRepository {
        val newRepo =
            TopicRepository(TopicRemoteDataSourceSNS, createTaskLocalDataSource(context))
        topicRepository = newRepo
        return newRepo
    }

    private fun createTaskLocalDataSource(context: Context): TopicDataSource {
        val database = database ?: createDataBase(context)
        return TopicLocalDataSource(database.topicDao(),database.endpointDao())
    }

    private fun createDataBase(context: Context): TopicDataBase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            TopicDataBase::class.java, DB_NAME
        ).build()
        database = result
        return result
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                //TasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            topicRepository = null
        }
    }
}

private const val DB_NAME = "Topics.db"
