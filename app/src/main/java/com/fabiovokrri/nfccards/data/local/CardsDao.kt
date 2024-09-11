package com.fabiovokrri.nfccards.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.fabiovokrri.nfccards.data.model.Card

@Dao
interface CardsDao {
    @Query("SELECT * FROM cards")
    suspend fun getAll(): List<Card>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getById(id: Int): Card

    @Query("SELECT * FROM cards WHERE name = :name")
    suspend fun getByName(name: String): Card

    @Upsert
    suspend fun upsert(card: Card)

    @Delete
    suspend fun delete(card: Card)
}