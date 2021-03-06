package com.notification.poc

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notification.poc.data.AppTopic
import com.notification.poc.data.source.TopicRepository
import kotlinx.coroutines.launch
//TODO inject using DI
class TopicViewModel (private val repository: TopicRepository) : ViewModel() {
    val topicObserver = repository.observeTopics()

    val status = repository.observeStatus()



    fun updateTopicSubscription (appTopics: List<AppTopic>){
        viewModelScope.launch {
            repository.updateTopics(appTopics)
        }

    }

    class Factory(val repository: TopicRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TopicViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TopicViewModel(repository) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}