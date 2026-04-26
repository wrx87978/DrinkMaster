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
import com.example.drinkmaster.data.local.FavoriteDrink
import com.example.drinkmaster.ui.mymenu.MyMenuViewModel

@Composable
fun MyMenuScreen(
    onDrinkClick: (String) -> Unit,
    vm: MyMenuViewModel = viewModel()
) {
    val favorites by vm.favorites.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Moje Menu (Ulubione)",
            style = MaterialTheme.typography.headlineSmall
        )

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Brak ulubionych drinków.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(favorites, key = { it.id }) { drink ->
                    FavoriteDrinkCard(drink = drink, onClick = { onDrinkClick(drink.id) })
                }
            }
        }
    }
}

@Composable
private fun FavoriteDrinkCard(
    drink: FavoriteDrink,
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
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = drink.name,
                    style = MaterialTheme.typography.titleMedium
                )
                drink.category?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
