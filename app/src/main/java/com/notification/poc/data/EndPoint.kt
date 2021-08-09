package com.notification.poc.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "endpoint")
data class EndPoint (
    @PrimaryKey
    var appEndPoint:String
)

