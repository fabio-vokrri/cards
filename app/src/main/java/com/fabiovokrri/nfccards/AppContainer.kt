package com.fabiovokrri.nfccards

import android.content.Context
import com.fabiovokrri.nfccards.data.CardsRepository
import com.fabiovokrri.nfccards.data.local.CardsDatabase

class AppContainer(private val context: Context) {
    val cardsRepository: CardsRepository by lazy {
        CardsRepository(CardsDatabase.getDatabase(context).getDao())
    }
}