package com.notification.poc

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.notification.poc.data.AppTopic
import com.notification.poc.data.source.TopicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(private val repository: TopicRepository): ViewModel() {
    val topicObserver = repository.observeTopics()

    val status = repository.observeStatus()

    fun updateTopicSubscription (appTopics: List<AppTopic>){
        viewModelScope.launch {
            repository.updateTopics(appTopics)
        }

    }

}