package com.example.drinkmaster.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.drinkmaster.data.local.FavoriteDrink
import com.example.drinkmaster.data.model.CocktailDto
import com.example.drinkmaster.ui.home.HomeUiState
import com.example.drinkmaster.ui.home.HomeViewModel

@Composable
fun HomeScreen(
    onDrinkClick: (String) -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val recentDrinks by vm.recentDrinks.collectAsStateWithLifecycle()
    val selectedFolder by vm.selectedFolder.collectAsStateWithLifecycle()
    val situationalDrinks by vm.situationalDrinks.collectAsStateWithLifecycle()
    
    var query by remember { mutableStateOf("") }
    val folders = listOf("Impreza", "Randka", "Chill w domu")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text("DrinkMaster", style = MaterialTheme.typography.headlineSmall)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        vm.search(it)
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Drink lub skladnik") },
                    singleLine = true
                )
                Button(onClick = { vm.search(query) }) {
                    Text("Szukaj")
                }
            }
        }

        // Sekcja: Ostatnio Przeglądane
        if (recentDrinks.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Ostatnio przeglądane",
                        style = MaterialTheme.typography.titleMedium
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(recentDrinks, key = { it.id }) { drink ->
                            RecentDrinkItem(drink = drink, onClick = { onDrinkClick(drink.id) })
                        }
                    }
                }
            }
        }

        // Sekcja: Drinki sytuacyjne
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Drinki sytuacyjne",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    folders.forEach { folder ->
                        FilterChip(
                            selected = selectedFolder == folder,
                            onClick = { vm.selectFolder(folder) },
                            label = { Text(folder) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Folder,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Lista drinków z wybranego folderu
        if (selectedFolder != null) {
            if (situationalDrinks.isEmpty()) {
                item {
                    Text(
                        text = "Brak drinków w folderze \"$selectedFolder\".",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(situationalDrinks, key = { "situational_${it.id}" }) { drink ->
                    FavoriteDrinkSmallCard(drink = drink, onClick = { onDrinkClick(drink.id) })
                }
            }
            item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
        }

        when (val state = uiState) {
            is HomeUiState.Idle -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Wyszukaj swojego ulubionego drinka!",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            is HomeUiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }

            is HomeUiState.Error -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                state.message,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { vm.loadAll() }) {
                                Text("Sprobuj ponownie")
                            }
                        }
                    }
                }
            }

            is HomeUiState.Success -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Znaleziono: ${state.drinks.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = { vm.loadAll() }) {
                            Text("Odswiez")
                        }
                    }
                }

                items(state.drinks, key = { it.id }) { drink ->
                    DrinkPreviewCard(drink = drink, onClick = { onDrinkClick(drink.id) })
                }
            }
        }
    }
}

@Composable
private fun FavoriteDrinkSmallCard(
    drink: FavoriteDrink,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = drink.thumbnailUrl,
                contentDescription = drink.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(50.dp)
            )
            Text(
                text = drink.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
private fun RecentDrinkItem(
    drink: CocktailDto,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = drink.thumbnailUrl,
            contentDescription = drink.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = drink.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun DrinkPreviewCard(
    drink: CocktailDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            drink.thumbnailUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = drink.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp)
                )
            }
            Text(
                text = drink.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
