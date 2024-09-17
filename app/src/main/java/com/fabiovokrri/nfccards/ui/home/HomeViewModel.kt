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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val cardsRepository: CardsRepository) : ViewModel() {

    private val _cards: Flow<List<Card>> = cardsRepository.getAll()
    val cardsListState: StateFlow<CardsListState> = _cards
        .map<List<Card>, CardsListState> { CardsListState.Success(it) }
        .catch { emit(CardsListState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = CardsListState.Loading
        )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun showDeleteDialog(card: Card) {
        _uiState.value = UiState(showDeleteDialog = true, cardToBeDeleted = card)
    }

    fun closeDeleteDialog() {
        _uiState.value = UiState(showDeleteDialog = false, cardToBeDeleted = null)
    }

    fun deleteSelectedCard() {
        val card = uiState.value.cardToBeDeleted ?: return
        _uiState.value = UiState() // reset ui state

        viewModelScope.launch {
            cardsRepository.delete(card)
        }
    }

    fun select(card: Card?) {
        _uiState.update {
            it.copy(currentSelectedCard = card)
        }
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

data class UiState(
    val showDeleteDialog: Boolean = false,
    val cardToBeDeleted: Card? = null,
    val currentSelectedCard: Card? = null,
)

sealed class CardsListState {
    data object Loading : CardsListState()
    data class Success(val cards: List<Card>) : CardsListState()
    data class Error(val message: String) : CardsListState()
}