package com.example.drinkmaster.ui.mymenu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drinkmaster.data.local.DrinkDatabase
import com.example.drinkmaster.data.local.FavoriteDrink
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MyMenuViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DrinkDatabase.getInstance(application).favoriteDao()

    val favorites: StateFlow<List<FavoriteDrink>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}
