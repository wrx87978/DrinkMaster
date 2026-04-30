package com.example.drinkmaster.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkmaster.data.local.DrinkDatabase
import com.example.drinkmaster.data.local.FavoriteDrink
import com.example.drinkmaster.data.repository.CocktailRepository
import com.example.drinkmaster.data.repository.RecentDrinksManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DrinkDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CocktailRepository()
    private val dao = DrinkDatabase.getInstance(application).favoriteDao()

    private val _uiState = MutableStateFlow<DrinkDetailUiState>(DrinkDetailUiState.Loading)
    val uiState: StateFlow<DrinkDetailUiState> = _uiState

    private val _drinkId = MutableStateFlow("")
    
    val favoriteDrink: StateFlow<FavoriteDrink?> = _drinkId.flatMapLatest { id ->
        if (id.isEmpty()) MutableStateFlow(null)
        else dao.getById(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val isFavorite: StateFlow<Boolean> = _drinkId.flatMapLatest { id ->
        if (id.isEmpty()) MutableStateFlow(false)
        else dao.isFavorite(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun load(id: String) {
        _drinkId.value = id
        viewModelScope.launch {
            _uiState.value = DrinkDetailUiState.Loading
            try {
                val drink = repository.getDrinkById(id)
                if (drink != null) {
                    _uiState.value = DrinkDetailUiState.Success(drink)
                    RecentDrinksManager.addDrink(drink)
                } else {
                    _uiState.value = DrinkDetailUiState.Error("Nie znaleziono drinka.")
                }
            } catch (e: Exception) {
                _uiState.value = DrinkDetailUiState.Error("Brak polaczenia z internetem.")
            }
        }
    }

    fun toggleFavorite() {
        val drinkId = _drinkId.value
        val state = _uiState.value as? DrinkDetailUiState.Success ?: return
        viewModelScope.launch {
            if (isFavorite.value) {
                dao.deleteById(drinkId)
            } else {
                dao.insert(
                    FavoriteDrink(
                        id           = state.drink.id,
                        name         = state.drink.name,
                        category     = state.drink.category,
                        thumbnailUrl = state.drink.thumbnailUrl
                    )
                )
            }
        }
    }

    fun updateRating(rating: Int) {
        val drinkId = _drinkId.value
        if (drinkId.isEmpty()) return
        
        viewModelScope.launch {
            if (!isFavorite.value) {
                val state = _uiState.value as? DrinkDetailUiState.Success ?: return@launch
                dao.insert(
                    FavoriteDrink(
                        id           = state.drink.id,
                        name         = state.drink.name,
                        category     = state.drink.category,
                        thumbnailUrl = state.drink.thumbnailUrl,
                        rating       = rating
                    )
                )
            } else {
                dao.updateRating(drinkId, rating)
            }
        }
    }

    fun updateNote(note: String) {
        val drinkId = _drinkId.value
        if (drinkId.isEmpty()) return

        viewModelScope.launch {
            if (!isFavorite.value) {
                val state = _uiState.value as? DrinkDetailUiState.Success ?: return@launch
                dao.insert(
                    FavoriteDrink(
                        id           = state.drink.id,
                        name         = state.drink.name,
                        category     = state.drink.category,
                        thumbnailUrl = state.drink.thumbnailUrl,
                        note         = note
                    )
                )
            } else {
                dao.updateNote(drinkId, note)
            }
        }
    }

    fun updateFolder(folder: String?) {
        val drinkId = _drinkId.value
        if (drinkId.isEmpty()) return

        viewModelScope.launch {
            if (!isFavorite.value) {
                val state = _uiState.value as? DrinkDetailUiState.Success ?: return@launch
                dao.insert(
                    FavoriteDrink(
                        id           = state.drink.id,
                        name         = state.drink.name,
                        category     = state.drink.category,
                        thumbnailUrl = state.drink.thumbnailUrl,
                        folder       = folder
                    )
                )
            } else {
                dao.updateFolder(drinkId, folder)
            }
        }
    }
}
