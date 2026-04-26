package com.example.drinkmaster.ui.home

import com.example.drinkmaster.data.model.CocktailDto

sealed class HomeUiState {
    data object Idle    : HomeUiState()
    data object Loading : HomeUiState()
    data class  Success(val drinks: List<CocktailDto>) : HomeUiState()
    data class  Error(val message: String)             : HomeUiState()
}
