package com.example.lbwatch.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Movie::class], version = 1)
abstract class MovieDB : RoomDatabase() {
    abstract fun getDao(): DAO
    companion object{
        fun getDb(context: Context): MovieDB {
            return Room.databaseBuilder(
                context.applicationContext,
                MovieDB::class.java,
                "movies.db"
            ).build()
        }
    }
}