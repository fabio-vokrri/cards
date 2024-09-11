package com.fabiovokrri.nfccards.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiovokrri.nfccards.R
import com.fabiovokrri.nfccards.data.model.Card

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onNavigateToAddModify: (Int?) -> Unit = {},
) {
    val cardsListState by homeViewModel.cardsListState.collectAsState()
    val uiState by homeViewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToAddModify(null) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_card))
            }
        }

    ) { innerPadding ->

        when (cardsListState) {
            is CardsListState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = stringResource(R.string.loading_message))
                }
            }

            is CardsListState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.error_message))
                    Text(text = (cardsListState as CardsListState.Error).message)
                }
            }

            is CardsListState.Success -> {
                val cards: List<Card> = (cardsListState as CardsListState.Success).cards

                if (uiState.showDeleteDialog) {
                    AlertDialog(
                        icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                        title = { Text(text = stringResource(R.string.delete_dialog_title)) },
                        text = { Text(text = stringResource(R.string.delete_dialog_message)) },
                        onDismissRequest = homeViewModel::closeDeleteDialog,
                        dismissButton = {
                            TextButton(onClick = homeViewModel::closeDeleteDialog) {
                                Text(text = stringResource(R.string.cancel))
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                homeViewModel.delete()
                                homeViewModel.closeDeleteDialog()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                                Text(text = stringResource(id = R.string.delete))
                            }
                        },
                    )
                }

                // no cards found in the database
                if (cards.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(R.string.empty_list_message))
                    }

                    return@Scaffold
                }

                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),

                    ) {
                    items(cards) { card ->
                        Surface(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .aspectRatio(16 / 9f)
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {},
                                    onLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        homeViewModel.showDeleteDialog(card)
                                    }
                                ),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.large
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = card.name, Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                IconButton(onClick = { onNavigateToAddModify(card.id) }) {
                                    Icon(Icons.Default.Create, contentDescription = stringResource(R.string.edit_card))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}