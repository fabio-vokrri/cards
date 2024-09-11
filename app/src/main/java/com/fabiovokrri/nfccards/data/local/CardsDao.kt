package com.fabiovokrri.nfccards.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fabiovokrri.nfccards.data.model.Card
import kotlinx.coroutines.flow.Flow

@Dao
interface CardsDao {
    @Query("SELECT * FROM cards")
    fun getAll(): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE name = :name")
    fun getByName(name: String): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getById(id: Int): Card

    @Upsert
    suspend fun upsert(card: Card)

    @Delete
    suspend fun delete(card: Card)
}