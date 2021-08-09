package com.notification.poc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class AppTopic(
    var topicName:String,
    var isSubsribed:Boolean = false,
    @PrimaryKey
    var topicArn:String,
    var subscriptionArn:String?

)
{}