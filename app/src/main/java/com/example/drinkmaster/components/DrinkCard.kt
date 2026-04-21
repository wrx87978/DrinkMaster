package com.example.drinkmaster.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.drinkmaster.model.Drink

@Composable
fun DrinkCard(
    drink: Drink,
    onAddClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = drink.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Kategoria: ${drink.category}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Glowny skladnik: ${drink.ingredient}",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(onClick = onAddClick) {
                Text("Dodaj do ulubionych")
            }
        }
    }
}