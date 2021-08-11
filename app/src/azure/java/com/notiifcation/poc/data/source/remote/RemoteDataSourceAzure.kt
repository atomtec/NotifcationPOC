package com.notiifcation.poc.data.source.remote

import android.app.Application
import android.util.Log
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

    private const val TAG = "RemoteDataSourceAzure"


    override fun observeTopics(): LiveData<List<AppTopic>> {
        TODO("For Local ")
    }

    override suspend fun getTopics(): List<AppTopic> {
        var appTopics:MutableList<AppTopic> = mutableListOf()
        NotificationHub.getTags().forEach{
            appTopics.add(AppTopic(it,true,it,null))
        }
        if(appTopics.size == 0){
            //Azure portal does not really have remotely created topic this needs to be created by app backend
            //Simulating here
            appTopics.add(AppTopic("AzureTopic 1",false,"AzureTopic1",null))
            appTopics.add(AppTopic("AzureTopic 2",false,"AzureTopic2",null))
        }
        return appTopics
    }

    override suspend fun updateTopicsSubscription(endPoint: EndPoint?, topics: List<AppTopic>) {
        val (subscribed,unsubscribed) = topics.partition{apt->
            apt.isSubsribed
        }
        subscribeTopics(subscribed)
        unSubscribeTopics(unsubscribed)
    }

    private fun unSubscribeTopics(unsubscribed: List<AppTopic>) {
        unsubscribed.forEach{
            Log.d(TAG,"Unsubscribing From" + it.topicArn)
            Log.d(TAG,"Is subscriber " + it.isSubsribed)
            var upDated = NotificationHub.removeTag(it.topicArn)
            Log.d(TAG, "Remove tag $upDated")
        }

    }

    private fun subscribeTopics(subscribed: List<AppTopic>) {
        subscribed.forEach {
            Log.d(TAG,"Subscribing To" + it.topicArn)
            Log.d(TAG,"Is subscriber " + it.isSubsribed)
            var upDated = NotificationHub.addTag(it.topicArn)
            Log.d(TAG, "Add tag $upDated")
        }
    }

    override suspend fun deleteAndInsertTopics(topics: List<AppTopic>) {
        TODO("For Local")
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
        TODO("For Local")
    }

    override suspend fun retrieveEndPoint(): EndPoint? {
        TODO("For Local")
    }

    override suspend fun getPlatformRegistration(): String {
        TODO("For Local")
    }

    override fun initializeProvider(application:Application) {
        Log.d(TAG,"ProviderIntialised")
        NotificationHub.start(application, hubName,
            connectionString
        )
        fBreceiver = FirebaseReceiver(NotificationHub.getInstance())
        NotificationHub.setEnabled(true)
    }
}