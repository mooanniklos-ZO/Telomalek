package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM monitored_channels ORDER BY addedTimestamp DESC")
    fun getAllChannels(): Flow<List<MonitoredChannel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: MonitoredChannel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<MonitoredChannel>)

    @Update
    suspend fun updateChannel(channel: MonitoredChannel)

    @Query("DELETE FROM monitored_channels WHERE id = :id")
    suspend fun deleteChannelById(id: Int)
}
