package com.fabiovokrri.nfccards.data

import com.fabiovokrri.nfccards.data.local.CardsDao
import com.fabiovokrri.nfccards.data.model.Card
import kotlinx.coroutines.flow.Flow

class CardsRepository(private val cardsDao: CardsDao) {
    fun getAll(): Flow<List<Card>> = cardsDao.getAll()

    fun getByName(name: String): Flow<List<Card>> = cardsDao.getByName(name)

    suspend fun getById(id: Int): Card = cardsDao.getById(id)

    suspend fun upsert(card: Card) = cardsDao.upsert(card)

    suspend fun delete(card: Card) = cardsDao.delete(card)
}