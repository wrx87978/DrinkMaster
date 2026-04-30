package com.example.drinkmaster.data.repository

import com.example.drinkmaster.data.model.CocktailDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RecentDrinksManager {
    private const val MAX_RECENT_ITEMS = 10

    private val _recentDrinks = MutableStateFlow<List<CocktailDto>>(emptyList())
    val recentDrinks: StateFlow<List<CocktailDto>> = _recentDrinks.asStateFlow()

    fun addDrink(drink: CocktailDto) {
        val currentList = _recentDrinks.value.toMutableList()
        
        // Remove if already exists to move it to the top
        currentList.removeAll { it.id == drink.id }
        
        // Add to the beginning
        currentList.add(0, drink)
        
        // Limit to MAX_RECENT_ITEMS
        if (currentList.size > MAX_RECENT_ITEMS) {
            _recentDrinks.value = currentList.take(MAX_RECENT_ITEMS)
        } else {
            _recentDrinks.value = currentList
        }
    }
}
