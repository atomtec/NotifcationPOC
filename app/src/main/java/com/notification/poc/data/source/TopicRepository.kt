package com.notification.poc.data.source

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.services.sns.model.NotFoundException
import com.notification.poc.BuildConfig
import com.notification.poc.data.AppTopic
import com.notification.poc.data.EndPoint
import com.notification.poc.di.AppModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class TopicRepository @Inject constructor (
    @AppModule.TopicRemoteDataSource private val topicRemoteDataSource: TopicDataSource,
    @AppModule.TopicLocalDataSource private val topicLocalDataSource: TopicDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val status = MutableLiveData<Boolean>(false)



    fun observeTopics(): LiveData<List<AppTopic>> {
        return topicLocalDataSource.observeTopics()
    }

    fun observeStatus():LiveData<Boolean>{
        return status
    }


    private suspend fun getRemoteTopics(){
        withContext(ioDispatcher) {
            try {
                var appTopics = topicRemoteDataSource.getTopics()
                topicLocalDataSource.deleteAndInsertTopics(appTopics)
            }
            catch (ex:Exception){
                //Ignore for now 
                
            }
           
        }
    }

    suspend fun registerOrUpdateWithCloudProvider(token:String) {
        withContext(ioDispatcher) {
            var endPoint: EndPoint? = topicLocalDataSource.retrieveEndPoint()
            endPoint =
                topicRemoteDataSource.registerOrUpdateWithCloudProvider(endPoint, token)
            endPoint?.let {
                topicLocalDataSource.saveEndPoint(it)// if it not null then it is update
            }
        }
    }

    fun initProvider(application:Application){
        topicRemoteDataSource.initializeProvider(application)
    }


    suspend fun fetchTopicsOrUpdateSubscription() {
        withContext(ioDispatcher) {
            var appTopics = topicLocalDataSource.getTopics()
            var endPoint = topicLocalDataSource.retrieveEndPoint()
            //resubscribe on changed


            when (appTopics.size) {
                0 -> getRemoteTopics()//No DB topic so get from remote
                else -> {
                    try {
                        topicRemoteDataSource.updateTopicsSubscription(endPoint, appTopics)
                        topicLocalDataSource.updateTopicsSubscription(endPoint, appTopics)
                    } catch (ex: NotFoundException) {
                        //Update DB here
                        getRemoteTopics()
                    } catch (ex: Exception) {
                        //Ignore for now
                    }

                }

            }

        }
    }



    suspend fun updateTopics(appTopics: List<AppTopic>) = withContext(ioDispatcher){
        var endPoint:EndPoint? = topicLocalDataSource.retrieveEndPoint()
        status.postValue(true)
        try {
            topicRemoteDataSource.updateTopicsSubscription(endPoint,appTopics)
            topicLocalDataSource.updateTopicsSubscription(endPoint,appTopics)
        }
        catch (ex: NotFoundException){
            //Update DB here
            getRemoteTopics()
        }
        catch (ex:Exception){
            //Ignore for now
        }
        finally {
            status.postValue(false)
        }

    }


}