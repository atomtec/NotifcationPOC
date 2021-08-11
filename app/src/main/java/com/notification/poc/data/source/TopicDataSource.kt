package com.notification.poc.data.source

import android.app.Application
import androidx.lifecycle.LiveData
import com.notification.poc.data.AppTopic
import com.notification.poc.data.EndPoint


interface TopicDataSource {

    fun observeTopics():LiveData<List<AppTopic>>

    suspend fun getTopics():List<AppTopic>

    suspend fun updateTopicsSubscription(endPoint: EndPoint?,topics:List<AppTopic>)

    suspend fun deleteAndInsertTopics(topics:List<AppTopic>)

    suspend fun registerOrUpdateWithCloudProvider(endPoint: EndPoint?, token:String): EndPoint?

    suspend fun saveEndPoint(endPoint: EndPoint)

    suspend fun retrieveEndPoint():EndPoint?

    suspend fun getPlatformRegistration():String

    fun initializeProvider(application:Application)


}