package com.example.lbwatch.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO {
    @Insert
    fun insert(movie: Movie)

    @Query("SELECT * FROM movies")
    fun getAll(): Flow<List<Movie>>

    @Delete
    suspend fun delete(movie: Movie)
}