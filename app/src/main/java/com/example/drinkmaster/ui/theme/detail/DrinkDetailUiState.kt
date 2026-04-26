package com.example.drinkmaster.ui.detail

import com.example.drinkmaster.data.model.CocktailDto

sealed class DrinkDetailUiState {
    data object Loading                        : DrinkDetailUiState()
    data class  Success(val drink: CocktailDto): DrinkDetailUiState()
    data class  Error(val message: String)     : DrinkDetailUiState()
}
