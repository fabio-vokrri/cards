package com.fabiovokrri.nfccards.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fabiovokrri.nfccards.data.model.Card

@Database(
    entities = [Card::class],
    version = 1,
    exportSchema = false
)
abstract class CardsDatabase : RoomDatabase() {
    abstract fun getDao(): CardsDao

    companion object {
        private const val DB_NAME = "cards_db"

        @Volatile
        private var Instance: CardsDatabase? = null

        fun getDatabase(context: Context): CardsDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    CardsDatabase::class.java,
                    DB_NAME
                ).build().also { Instance = it }
            }
        }
    }
}