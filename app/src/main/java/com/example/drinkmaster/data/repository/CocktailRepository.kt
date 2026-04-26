package com.example.drinkmaster.data.repository

import com.example.drinkmaster.data.api.RetrofitInstance
import com.example.drinkmaster.data.model.CocktailDto
import com.example.drinkmaster.data.model.CocktailPreviewDto

class CocktailRepository {

    private val api = RetrofitInstance.api

    suspend fun getAllDrinks(): List<CocktailPreviewDto> =
        api.getAllByCategory("Cocktail").drinks ?: emptyList()

    suspend fun searchByName(query: String): List<CocktailDto> =
        api.searchByName(query).drinks ?: emptyList()

    suspend fun searchByIngredient(ingredient: String): List<CocktailPreviewDto> =
        api.searchByIngredient(ingredient).drinks ?: emptyList()

    suspend fun getDrinkById(id: String): CocktailDto? =
        api.lookupById(id).drinks?.firstOrNull()
}