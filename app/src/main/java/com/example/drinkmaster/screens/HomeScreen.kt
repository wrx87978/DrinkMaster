package com.example.drinkmaster.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.drinkmaster.ui.home.HomeUiState
import com.example.drinkmaster.ui.home.HomeViewModel

@Composable
fun HomeScreen(
    onDrinkClick: (String) -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

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