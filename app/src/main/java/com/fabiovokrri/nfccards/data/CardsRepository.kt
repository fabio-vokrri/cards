package com.fabiovokrri.nfccards.data

import com.fabiovokrri.nfccards.data.local.CardsDao
import com.fabiovokrri.nfccards.data.model.Card

class CardsRepository(private val cardsDao: CardsDao) {
    suspend fun getAll(): List<Card> = cardsDao.getAll()

    suspend fun getById(id: Int): Card = cardsDao.getById(id)

    suspend fun getByName(name: String): Card = cardsDao.getByName(name)

    suspend fun upsert(card: Card) = cardsDao.upsert(card)

    suspend fun delete(card: Card) = cardsDao.delete(card)
}