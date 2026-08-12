package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordDao {
    @Query("SELECT * FROM keyword_filters ORDER BY id ASC")
    fun getAllKeywords(): Flow<List<KeywordFilter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword: KeywordFilter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeywords(keywords: List<KeywordFilter>)

    @Update
    suspend fun updateKeyword(keyword: KeywordFilter)

    @Query("DELETE FROM keyword_filters WHERE id = :id")
    suspend fun deleteKeywordById(id: Int)
}
