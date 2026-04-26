package com.example.drinkmaster.data.model

import com.google.gson.annotations.SerializedName

data class CocktailResponse(
    @SerializedName("drinks") val drinks: List<CocktailDto>?
)

data class IngredientSearchResponse(
    @SerializedName("drinks") val drinks: List<CocktailPreviewDto>?
)

data class CocktailDto(
    @SerializedName("idDrink")          val id: String,
    @SerializedName("strDrink")         val name: String,
    @SerializedName("strCategory")      val category: String? = null,
    @SerializedName("strThumbnail")     val thumbnailUrl: String? = null,
    @SerializedName("strInstructions")  val instructions: String? = null,
    @SerializedName("strIngredient1")   val ingredient1: String? = null,
    @SerializedName("strIngredient2")   val ingredient2: String? = null,
    @SerializedName("strIngredient3")   val ingredient3: String? = null,
    @SerializedName("strIngredient4")   val ingredient4: String? = null,
    @SerializedName("strIngredient5")   val ingredient5: String? = null,
    @SerializedName("strMeasure1")      val measure1: String? = null,
    @SerializedName("strMeasure2")      val measure2: String? = null,
    @SerializedName("strMeasure3")      val measure3: String? = null,
    @SerializedName("strMeasure4")      val measure4: String? = null,
    @SerializedName("strMeasure5")      val measure5: String? = null,
) {
    fun ingredientList(): List<String> = listOfNotNull(
        ingredient1?.let { if (measure1 != null) "$it – $measure1" else it },
        ingredient2?.let { if (measure2 != null) "$it – $measure2" else it },
        ingredient3?.let { if (measure3 != null) "$it – $measure3" else it },
        ingredient4?.let { if (measure4 != null) "$it – $measure4" else it },
        ingredient5?.let { if (measure5 != null) "$it – $measure5" else it },
    )
}

data class CocktailPreviewDto(
    @SerializedName("idDrink")       val id: String,
    @SerializedName("strDrink")      val name: String,
    @SerializedName("strDrinkThumb") val thumbnailUrl: String?
)
