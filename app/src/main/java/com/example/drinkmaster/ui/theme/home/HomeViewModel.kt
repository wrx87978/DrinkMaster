package com.example.drinkmaster.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkmaster.data.model.CocktailDto
import com.example.drinkmaster.data.repository.CocktailRepository
import com.example.drinkmaster.data.repository.RecentDrinksManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: CocktailRepository = CocktailRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState

    val recentDrinks: StateFlow<List<CocktailDto>> = RecentDrinksManager.recentDrinks

    init {
        loadAll()
    }

    fun search(query: String) {
        if (query.isBlank()) {
            loadAll()
            return
        }

        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val byName = repository.searchByName(query)
                if (byName.isNotEmpty()) {
                    _uiState.value = HomeUiState.Success(byName)
                } else {
                    val byIngredient = repository.searchByIngredient(query)
                    val mapped = byIngredient.map { preview ->
                        CocktailDto(
                            id           = preview.id,
                            name         = preview.name,
                            category     = null,
                            thumbnailUrl = preview.thumbnailUrl,
                            ingredient1  = query,
                            ingredient2  = null,
                            ingredient3  = null
                        )
                    }
                    _uiState.value = if (mapped.isEmpty()) {
                        HomeUiState.Error("Nie znaleziono drinkow dla: $query")
                    } else {
                        HomeUiState.Success(mapped)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Brak polaczenia z internetem.")
            }
        }
    }

    fun refresh(query: String) = search(query)

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val drinks = repository.getAllDrinks().map { preview ->
                    CocktailDto(
                        id           = preview.id,
                        name         = preview.name,
                        thumbnailUrl = preview.thumbnailUrl
                    )
                }
                _uiState.value = HomeUiState.Success(drinks)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Brak polaczenia z internetem.")
            }
        }
    }
}
