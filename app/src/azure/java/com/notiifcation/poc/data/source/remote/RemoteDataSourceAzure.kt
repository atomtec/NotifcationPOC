package com.notiifcation.poc.data.source.remote

import android.app.Application
import androidx.lifecycle.LiveData
import com.microsoft.windowsazure.messaging.notificationhubs.FirebaseReceiver
import com.microsoft.windowsazure.messaging.notificationhubs.NotificationHub
import com.notification.poc.BuildConfig
import com.notification.poc.data.AppTopic
import com.notification.poc.data.EndPoint
import com.notification.poc.data.source.TopicDataSource


/**
 * The interface was actually modelled after SNS so lot of these functions are not going to be used
 * now . Need to redesign the interface.
 */
object RemoteDataSourceAzure:TopicDataSource {

    private  val connectionString = BuildConfig.AZURE_CONNECTION_STRING_CLIENT
    private  val hubName = BuildConfig.AZURE_NOTIFICATION_HUBNAME
    private lateinit var fBreceiver:FirebaseReceiver

    private val TAG = "RemoteDataSourceAzure"


    override fun observeTopics(): LiveData<List<AppTopic>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTopics(): List<AppTopic> {
        TODO("Not yet implemented")
    }

    override suspend fun updateTopicsSubscription(endPoint: EndPoint, topics: List<AppTopic>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAndInsertTopics(topics: List<AppTopic>) {
        TODO("Not yet implemented")
    }

    //TODO this function needs to be refactored for Azure
    override suspend fun registerOrUpdateWithCloudProvider(
        endPoint: EndPoint?,
        token: String
    ): EndPoint? {
        fBreceiver.onNewToken(token)
        return null //no need to save for Azure
    }

    override suspend fun saveEndPoint(endPoint: EndPoint) {
        TODO("Not yet implemented")
    }

    override suspend fun retrieveEndPoint(): EndPoint? {
        TODO("Not yet implemented")
    }

    override suspend fun getPlatformRegistration(): String {
        TODO("Not yet implemented")
    }

    override fun initializeProvider(application:Application) {
        NotificationHub.start(application, hubName,
            connectionString
        )
        NotificationHub.addTag("client123")
        fBreceiver = FirebaseReceiver(NotificationHub.getInstance())
    }
}