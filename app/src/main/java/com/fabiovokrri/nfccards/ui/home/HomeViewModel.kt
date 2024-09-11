package com.fabiovokrri.nfccards.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fabiovokrri.nfccards.CardsApplication
import com.fabiovokrri.nfccards.data.CardsRepository
import com.fabiovokrri.nfccards.data.model.Card
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val cardsRepository: CardsRepository) : ViewModel() {

    private val _cards: Flow<List<Card>> = cardsRepository.getAll()
    val uiState: StateFlow<HomeUiState> = try {
        _cards.map { HomeUiState.Success(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState.Loading
        )
    } catch (error: Error) {
        MutableStateFlow<HomeUiState>(
            HomeUiState.Error(error.message ?: "Unknown error")
        )
    }

    fun upsert(card: Card) = viewModelScope.launch {
        cardsRepository.upsert(card)
    }

    fun delete(card: Card) = viewModelScope.launch {
        cardsRepository.delete(card)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as CardsApplication)
                HomeViewModel(application.container.cardsRepository)
            }
        }

    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val cards: List<Card>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}