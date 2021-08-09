package com.notiifcation.poc.data.source.remote

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.*
import com.notification.poc.BuildConfig
import com.notification.poc.data.AppTopic
import com.notification.poc.data.EndPoint
import com.notification.poc.data.source.TopicDataSource
import java.util.regex.Matcher
import java.util.regex.Pattern

object TopicRemoteDataSourceSNS:TopicDataSource {
    private var ACCESS_KEY = BuildConfig.ACCESS_KEY
    private var SECRET_KEY = BuildConfig.SECRET_KEY
    private val applicationArn = BuildConfig.APP_ARN
    var cred: AWSCredentials = BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)

    private val TAG = "TopicRemoteDataSource"

    lateinit var client :AmazonSNSClient
    override fun observeTopics(): LiveData<List<AppTopic>> {
        TODO("This is for local")
    }

    override suspend fun getTopics(): List<AppTopic> {
        var appTopics:MutableList<AppTopic> = mutableListOf()
        var subs: MutableMap<String,String> = HashMap<String,String>()
        client.listSubscriptions().subscriptions.forEach {
            subs.put(it.topicArn,it.subscriptionArn)
        }
        client.listTopics().topics.forEach {
            if(subs.containsKey(it.topicArn)){
                appTopics.add(AppTopic(it.topicArn.substringAfterLast(":"),true,it.topicArn,subs.get(it.topicArn)))
            }
            else{
                appTopics.add(AppTopic(it.topicArn.substringAfterLast(":"),false,it.topicArn,null))
            }
        }

        return appTopics

    }

    override suspend fun updateTopicsSubscription(endPoint: EndPoint,topics: List<AppTopic>) {
        val (subscribed,unsubscribed) = topics.partition{apt->
            apt.isSubsribed
        }
        subscribeTopics(endPoint,subscribed)
        unSubscribeTopics(unsubscribed)
    }

    override suspend fun deleteAndInsertTopics(topics: List<AppTopic>) {
        TODO("Not yet implemented")
    }

    override suspend fun registerOrUpdateWithCloudProvider(
        endPoint: EndPoint?,
        token: String
    ): EndPoint? {
        var endpointArn: String? = endPoint?.appEndPoint
        var newendPoint:EndPoint? = null

        var updateNeeded = false
        var createNeeded = null == endpointArn
        if (createNeeded) {
            // No platform endpoint ARN is stored; need to call createEndpoint.
            newendPoint = createEndPoint(token)
            endpointArn = newendPoint.appEndPoint
            createNeeded = false
        }

        Log.d(TAG, "Retrieving platform endpoint data...")
        // Look up the platform endpoint and make sure the data in it is current, even if
        // it was just created.
        try {
            val geaReq = GetEndpointAttributesRequest()
                .withEndpointArn(endpointArn)
            val geaRes: GetEndpointAttributesResult = client.getEndpointAttributes(geaReq)
            updateNeeded = (geaRes.attributes["Token"] != token
                    || !geaRes.attributes["Enabled"].equals("true", ignoreCase = true))
        } catch (nfe: NotFoundException) {
            // We had a stored ARN, but the platform endpoint associated with it
            // disappeared. Recreate it.
            Log.d(TAG, "NotFoundException")
            createNeeded = true
        }
        if (createNeeded) {
            newendPoint = createEndPoint(token)
        }

        Log.d(TAG, "updateNeeded = $updateNeeded")
        if (updateNeeded) {
            // The platform endpoint is out of sync with the current data;
            // update the token and enable it.
            Log.d(TAG,"Updating platform endpoint $endpointArn")
            val attribs: MutableMap<String, String> = HashMap()
            attribs["Token"] = token
            attribs["Enabled"] = "true"
            val saeReq = SetEndpointAttributesRequest()
                .withEndpointArn(endpointArn)
                .withAttributes(attribs)
            client.setEndpointAttributes(saeReq)
        }

        return newendPoint
    }



    private suspend fun createEndPoint(token: String):EndPoint {
        var endpointArn: String?
        endpointArn = try {
            Log.d(TAG,"Creating platform endpoint with token $token")
            val cpeReq = CreatePlatformEndpointRequest()
                .withPlatformApplicationArn(applicationArn)
                .withToken(token)
            val cpeRes = client
                .createPlatformEndpoint(cpeReq)
            cpeRes.endpointArn
        } catch (ipe: InvalidParameterException) {
            val message: String = ipe.getErrorMessage()
            Log.d(TAG,"Exception message: $message")
            val p: Pattern = Pattern
                .compile(
                    ".*Endpoint (arn:aws:sns[^ ]+) already exists " +
                            "with the same [Tt]oken.*"
                )
            val m: Matcher = p.matcher(message)
            if (m.matches()) {
                // The platform endpoint already exists for this token, but with
                // additional custom data that
                // createEndpoint doesn't want to overwrite. Just use the
                // existing platform endpoint.
                m.group(1)
            } else {
                // Rethrow the exception, the input is actually bad.
                throw ipe
            }
        }

        return EndPoint(endpointArn!!)
    }

    override suspend fun saveEndPoint(endPoint: EndPoint) {
        TODO("This is for local")
    }

    override suspend fun retrieveEndPoint(): EndPoint? {
        TODO("This is for local")
    }


    override suspend fun getPlatformRegistration(): String {
        TODO("Not yet implemented")
    }

    override fun initializeProvider(application: Application) {
        client = AmazonSNSClient(cred).also{
            it.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2))
        }
    }

    private suspend fun subscribeTopics(endPoint: EndPoint, topics: List<AppTopic>) {
        topics.forEach{
            Log.d(TAG,"Subscribing to topic ${it.topicArn}")
            it.subscriptionArn =
                client.subscribe(SubscribeRequest(it.topicArn,"application",
                    endPoint.appEndPoint)).subscriptionArn
            it.isSubsribed = true
        }
    }

    private suspend fun unSubscribeTopics(topics: List<AppTopic>) {
        topics.forEach { apt ->
            apt.subscriptionArn?.let {
                Log.d(TAG,"UnSubscribing to topic ${apt.topicArn}")
                client.unsubscribe(it)
                apt.isSubsribed = false
                apt.subscriptionArn = null
            }
        }
    }
}