package com.notification.poc.data.source.local

import android.provider.SyncStateContract.Helpers.insert
import androidx.lifecycle.LiveData
import androidx.room.*
import com.notification.poc.data.AppTopic
import com.notification.poc.data.EndPoint


@Dao
interface TopicDao {

    @Query("SELECT * FROM topics")
    fun observeTopics(): LiveData<List<AppTopic>>

    @Query("SELECT * FROM topics")
    fun getTopics(): List<AppTopic>

    @Query("DELETE FROM topics")
    suspend fun deleteTopics()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTopic(topics:List<AppTopic>)

    @Transaction
    suspend fun deleteAndInsert(topics:List<AppTopic>) {
        deleteTopics()
        insertOrUpdateTopic(topics)
    }
}


@Dao
interface EndPointDao {

    @Query("SELECT * FROM endpoint")
    fun observeEndPoint(): LiveData<List<EndPoint>>

    @Query("SELECT * FROM endpoint")
    fun getEndPoint(): List<EndPoint>

    @Query("DELETE FROM endpoint")
    suspend fun deleteEndPoints()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateEndPoint(vararg endPoint: EndPoint)

    @Transaction
    suspend fun deleteAndInsert(vararg endPoint: EndPoint) {
        deleteEndPoints()
        insertOrUpdateEndPoint(*endPoint)
    }
}



@Database(entities = [AppTopic::class,EndPoint::class], version = 1, exportSchema = false)
abstract class TopicDataBase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun endpointDao():EndPointDao
}