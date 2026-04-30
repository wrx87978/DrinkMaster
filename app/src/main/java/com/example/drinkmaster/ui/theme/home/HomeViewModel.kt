package com.example.drinkmaster.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkmaster.data.local.DrinkDatabase
import com.example.drinkmaster.data.local.FavoriteDrink
import com.example.drinkmaster.data.model.CocktailDto
import com.example.drinkmaster.data.repository.CocktailRepository
import com.example.drinkmaster.data.repository.RecentDrinksManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CocktailRepository()
    private val dao = DrinkDatabase.getInstance(application).favoriteDao()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState

    val recentDrinks: StateFlow<List<CocktailDto>> = RecentDrinksManager.recentDrinks

    val favorites: StateFlow<List<FavoriteDrink>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _selectedFolder = MutableStateFlow<String?>(null)
    val selectedFolder: StateFlow<String?> = _selectedFolder

    val situationalDrinks: StateFlow<List<FavoriteDrink>> = combine(favorites, _selectedFolder) { favs, folder ->
        if (folder == null) emptyList()
        else favs.filter { it.folder == folder }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        loadAll()
    }

    fun selectFolder(folder: String?) {
        _selectedFolder.value = if (_selectedFolder.value == folder) null else folder
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
