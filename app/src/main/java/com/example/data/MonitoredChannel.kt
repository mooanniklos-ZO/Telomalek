package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitored_channels")
data class MonitoredChannel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val channelName: String,
    val usernameOrId: String,
    val isListening: Boolean = true,
    val memberCount: String = "غير معروف",
    val addedTimestamp: Long = System.currentTimeMillis()
)
