package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "keyword_filters")
data class KeywordFilter(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val keyword: String,
    val isEnabled: Boolean = true
)
