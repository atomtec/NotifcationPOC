package com.notification.poc

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.notification.poc.data.AppTopic

class TopicListAdapter(): ListAdapter<AppTopic, TopicListAdapter.TopicItemViewHolder>(DiffCallback) {


    class TopicItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(topicName : String, isChecked:Boolean){
            checkBox.text = topicName
            checkBox.isChecked = isChecked
            Log.e("adapter", "text ${topicName} checked ${isChecked}")
        }

        fun getCheckBox():CheckBox{
            return checkBox
        }

    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopicItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        Log.e("adapter", "onCreateViewHolder")
        return TopicItemViewHolder(view).apply {
            getCheckBox().setOnClickListener {
                getItem(adapterPosition).isSubsribed = getCheckBox().isChecked
            }
        }
    }

    override fun onBindViewHolder(holder: TopicItemViewHolder, position: Int) {
      holder.bind(getItem(position).topicName,getItem(position).isSubsribed )
    }


    companion object DiffCallback : DiffUtil.ItemCallback<AppTopic>() {
        override fun areItemsTheSame(oldItem: AppTopic, newItem: AppTopic): Boolean {
            Log.e("adapter", "Are Item same old item ${oldItem} + new item ${newItem}")
            return oldItem.topicArn.equals(newItem.topicArn)
        }

        override fun areContentsTheSame(oldItem: AppTopic, newItem: AppTopic): Boolean {
            Log.e("adapter", "Are Content Same old item ${oldItem} + new item ${newItem}")
            return oldItem == newItem  //Auto generated equality check from data classes
        }
    }


}