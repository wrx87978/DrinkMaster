package com.example.drinkmaster.screens

import com.example.drinkmaster.components.DrinkCard
import com.example.drinkmaster.model.Drink
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val sampleDrinks = listOf(
    Drink(
        id = "1",
        name = "Mojito",
        category = "Classic",
        ingredient = "Mint"
    ),
    Drink(
        id = "2",
        name = "Margarita",
        category = "Sour",
        ingredient = "Tequila"
    ),
    Drink(
        id = "3",
        name = "Old Fashioned",
        category = "Classic",
        ingredient = "Whiskey"
    ),
    Drink(
        id = "4",
        name = "Pina Colada",
        category = "Tropical",
        ingredient = "Pineapple"
    ),
    Drink(
        id = "5",
        name = "Espresso Martini",
        category = "Coffee",
        ingredient = "Espresso"
    )
)

@Composable
fun HomeScreen() {
    var query by remember { mutableStateOf("") }

    val filteredDrinks = sampleDrinks.filter {
        it.name.contains(query.trim(), ignoreCase = true) ||
                it.ingredient.contains(query.trim(), ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Home",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Wyszukaj drink lub skladnik") },
                singleLine = true
            )
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Miejsce na API",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Tutaj pozniej bedzie podlaczone The Cocktail DB.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(onClick = { }) {
                        Text("Odswiez")
                    }
                }
            }
        }

        if (filteredDrinks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Brak wynikow",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Nie znaleziono drinkow pasujacych do wyszukiwania.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            items(filteredDrinks) { drink ->
                DrinkCard(drink = drink)
            }
        }
    }
}

