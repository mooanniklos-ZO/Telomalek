package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY timestamp DESC")
    fun getAllJobs(): Flow<List<JobOffer>>

    @Query("SELECT * FROM jobs WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteJobs(): Flow<List<JobOffer>>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    suspend fun getJobById(id: String): JobOffer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobOffer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobOffer>)

    @Update
    suspend fun updateJob(job: JobOffer)

    @Query("DELETE FROM jobs WHERE id = :id")
    suspend fun deleteJobById(id: String)

    @Query("DELETE FROM jobs")
    suspend fun deleteAllJobs()

    @Query("SELECT COUNT(*) FROM jobs")
    fun getJobCount(): Flow<Int>
}
