package com.notification.poc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest
//TODO inject viewmodels
class MainActivity : AppCompatActivity() {
    lateinit var recyclerView:RecyclerView

    private val viewModel: TopicsViewModel by lazy {

        val repository = (applicationContext as NotiFicationPOCApp).appComponent.topicRepository
        ViewModelProvider(this, TopicsViewModel.Factory(repository))
            .get(TopicsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var adapter = TopicListAdapter()
        var progressBar = findViewById<ProgressBar>(R.id.progressBar)
        recyclerView = findViewById(R.id.topic_list)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel.topicObserver.observe(this, Observer {
            adapter.submitList(it)
        })
        viewModel.status.observe(this, Observer { loading->
            when(loading){
                true->progressBar.visibility = View.VISIBLE
                else -> progressBar.visibility = View.GONE
            }
        })
        var submitButton = findViewById<Button>(R.id.submitButton)
        submitButton.setOnClickListener {
            viewModel.updateTopicSubscription(adapter.currentList)
        }

    }

}