package com.example.drinkmaster.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.drinkmaster.components.RatingBar
import com.example.drinkmaster.data.local.FavoriteDrink
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

    val uiState      by vm.uiState.collectAsStateWithLifecycle()
    val isFavorite   by vm.isFavorite.collectAsStateWithLifecycle()
    val favoriteData by vm.favoriteDrink.collectAsStateWithLifecycle()

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
                    favoriteData = favoriteData,
                    onRatingChange = { vm.updateRating(it) },
                    onNoteChange = { vm.updateNote(it) },
                    onFolderChange = { vm.updateFolder(it) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun DrinkDetailContent(
    drink: CocktailDto,
    favoriteData: FavoriteDrink?,
    onRatingChange: (Int) -> Unit,
    onNoteChange: (String) -> Unit,
    onFolderChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val folders = listOf("Impreza", "Randka", "Chill w domu")
    var showFolderMenu by remember { mutableStateOf(false) }

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
            // Kategoria i Ocena
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                drink.category?.let {
                    AssistChip(
                        onClick = {},
                        label = { Text(it) }
                    )
                }
                
                RatingBar(
                    rating = favoriteData?.rating ?: 0,
                    onRatingChange = onRatingChange,
                    starSize = 24.dp
                )
            }

            // Notatka
            var noteText by remember(favoriteData?.id) { mutableStateOf(favoriteData?.note ?: "") }
            
            OutlinedTextField(
                value = noteText,
                onValueChange = { 
                    noteText = it
                    onNoteChange(it)
                },
                label = { Text("Moja notatka") },
                placeholder = { Text("Dodaj własną notatkę...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Wybór Folderu
            Box {
                OutlinedButton(
                    onClick = { showFolderMenu = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Folder, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(favoriteData?.folder?.let { "Folder: $it" } ?: "Dodaj do folderu")
                }
                DropdownMenu(
                    expanded = showFolderMenu,
                    onDismissRequest = { showFolderMenu = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    folders.forEach { folderName ->
                        DropdownMenuItem(
                            text = { Text(folderName) },
                            onClick = {
                                onFolderChange(folderName)
                                showFolderMenu = false
                            }
                        )
                    }
                    if (favoriteData?.folder != null) {
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Usuń z folderu", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                onFolderChange(null)
                                showFolderMenu = false
                            }
                        )
                    }
                }
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
