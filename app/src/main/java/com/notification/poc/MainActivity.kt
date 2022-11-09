package com.notification.poc

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var recyclerView:RecyclerView

    private val viewModel:TopicViewModel by viewModels()

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