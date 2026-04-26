package com.example.drinkmaster.data.api

import com.example.drinkmaster.data.model.CocktailResponse
import com.example.drinkmaster.data.model.IngredientSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CocktailApiService {

    @GET("search.php")
    suspend fun searchByName(@Query("s") name: String): CocktailResponse

    @GET("filter.php")
    suspend fun searchByIngredient(@Query("i") ingredient: String): IngredientSearchResponse

    @GET("filter.php")
    suspend fun getAllByCategory(@Query("c") category: String): IngredientSearchResponse

    @GET("lookup.php")
    suspend fun lookupById(@Query("i") id: String): CocktailResponse
}
