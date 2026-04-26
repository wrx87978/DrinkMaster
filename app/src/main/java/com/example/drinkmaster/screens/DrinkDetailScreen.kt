package com.example.drinkmaster.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.drinkmaster.data.model.CocktailDto
import com.example.drinkmaster.ui.detail.DrinkDetailUiState
import com.example.drinkmaster.ui.detail.DrinkDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreen(
    drinkId: String,
    onBack: () -> Unit,
    vm: DrinkDetailViewModel = viewModel()
) {
    LaunchedEffect(drinkId) { vm.load(drinkId) }

    val uiState    by vm.uiState.collectAsStateWithLifecycle()
    val isFavorite by vm.isFavorite.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = (uiState as? DrinkDetailUiState.Success)?.drink?.name ?: ""
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wstecz")
                    }
                },
                actions = {
                    if (uiState is DrinkDetailUiState.Success) {
                        IconButton(onClick = { vm.toggleFavorite() }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite
                                              else Icons.Filled.FavoriteBorder,
                                contentDescription = "Ulubione",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is DrinkDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            is DrinkDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is DrinkDetailUiState.Success -> {
                DrinkDetailContent(
                    drink = state.drink,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun DrinkDetailContent(
    drink: CocktailDto,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Zdjęcie
        drink.thumbnailUrl?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = drink.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Kategoria
            drink.category?.let {
                AssistChip(
                    onClick = {},
                    label = { Text(it) }
                )
            }

            // Składniki
            val ingredients = drink.ingredientList()
            if (ingredients.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Skladniki",
                            style = MaterialTheme.typography.titleMedium
                        )
                        ingredients.forEach { item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "• ",
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    item,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Instrukcje
            drink.instructions?.let { instructions ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Przygotowanie",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            instructions,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
