package com.notification.poc.data.source.local

import android.app.Application
import androidx.lifecycle.LiveData
import com.notification.poc.data.AppTopic
import com.notification.poc.data.EndPoint
import com.notification.poc.data.source.TopicDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TopicLocalDataSource internal constructor(private val topicDao: TopicDao, private val endPontDao : EndPointDao,
                           private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : TopicDataSource {
    override fun observeTopics(): LiveData<List<AppTopic>> {
        return topicDao.observeTopics()
    }

    override suspend fun getTopics(): List<AppTopic> {
        return withContext(ioDispatcher){
            topicDao.getTopics()
        }
    }

    override suspend fun getPlatformRegistration(): String {
        TODO("Not yet implemented")
    }


    override suspend fun updateTopicsSubscription(endPoint: EndPoint,topics: List<AppTopic>) = withContext(ioDispatcher) {
        topicDao.insertOrUpdateTopic(topics)
    }

    override suspend fun deleteAndInsertTopics(topics: List<AppTopic>) {
        topicDao.deleteAndInsert(topics)
    }

    override suspend fun registerOrUpdateWithCloudProvider(
        endPoint: EndPoint?,
        token: String
    ): EndPoint? {
        TODO("This is with remote ")
    }




    override suspend fun saveEndPoint(endPoint: EndPoint) = withContext(ioDispatcher){
        try {
            //Have one end point at all time for one app
            endPontDao.deleteEndPoints()
        }
        catch (ex:Exception){

        }
        endPontDao.insertOrUpdateEndPoint(endPoint)
    }


    override suspend fun retrieveEndPoint(): EndPoint? {
        return withContext(ioDispatcher){
           when(endPontDao.getEndPoint().size){
               0->null
               else -> endPontDao.getEndPoint()[0]
           }
        }
    }

    override fun initializeProvider(application: Application) {
        TODO("Not yet implemented")
    }


}