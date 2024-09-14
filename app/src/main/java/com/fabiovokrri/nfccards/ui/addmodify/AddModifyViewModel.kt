package com.fabiovokrri.nfccards.ui.addmodify

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import com.fabiovokrri.nfccards.CardsApplication
import com.fabiovokrri.nfccards.data.CardsRepository
import com.fabiovokrri.nfccards.data.model.Card
import com.fabiovokrri.nfccards.ui.AddModify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddModifyViewModel(
    private val cardsRepository: CardsRepository,
    savedStateHandle: SavedStateHandle = SavedStateHandle(),
) : ViewModel() {

    private val _currentCard = MutableStateFlow(CardState())
    val currentCard: StateFlow<CardState> = _currentCard.asStateFlow()

    private val cardId = savedStateHandle.toRoute<AddModify>().cardId

    init {
        if (cardId != null) {
            viewModelScope.launch {
                val card = cardsRepository.getById(cardId)

                _currentCard.value = CardState(
                    card.id,
                    card.name,
                    card.data
                )
            }
        }
    }

    fun updateName(name: String) = _currentCard.update {
        it.copy(name = name)
    }

    fun updateData(data: ByteArray) = _currentCard.update {
        it.copy(data = data)
    }

    fun save() {
        if (_currentCard.value.name.isBlank()) return

        viewModelScope.launch {
            cardsRepository.upsert(_currentCard.value.toCard())
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val application = (this[APPLICATION_KEY] as CardsApplication)
                AddModifyViewModel(application.container.cardsRepository, savedStateHandle)
            }
        }
    }
}

data class CardState(
    val id: Int? = null,
    val name: String = "",
    val data: ByteArray = byteArrayOf(),
)

private fun CardState.toCard() = Card(
    id = id ?: 0, // TODO: verify if it works
    name = name,
    data = data,
)