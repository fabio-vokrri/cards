package com.fabiovokrri.nfccards.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiovokrri.nfccards.R
import com.fabiovokrri.nfccards.data.model.Card
import java.util.logging.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onNavigateToAddModify: (Int?) -> Unit = {},
) {
    val cardsListState by homeViewModel.cardsListState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToAddModify(null) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_card))
            }
        }
    ) { _ ->
        when (cardsListState) {
            is CardsListState.Loading -> LoadingScreen()

            is CardsListState.Error -> ErrorScreen(
                (cardsListState as CardsListState.Error).message
            )

            is CardsListState.Success -> CardsScreen(
                onNavigateToAddModify = onNavigateToAddModify
            )
        }
    }
}

@Composable
private fun ErrorScreen(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.error_message))
        Text(text = errorMessage)
    }
}

@Composable
private fun LoadingScreen() {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CardsScreen(
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onNavigateToAddModify: (Int?) -> Unit = {},
) {
    val cardsListState by homeViewModel.cardsListState.collectAsState()
    val uiState by homeViewModel.uiState.collectAsState()

    val cards: List<Card> = (cardsListState as CardsListState.Success).cards

    val haptic = LocalHapticFeedback.current

    if (uiState.showDeleteDialog) {
        DeleteDialog(
            cardToBeDeleted = uiState.cardToBeDeleted!!,
            onDismiss = homeViewModel::closeDeleteDialog,
            onConfirm = {
                homeViewModel.deleteSelectedCard()
                homeViewModel.closeDeleteDialog()
            }
        )
    }

    // no cards found in the database
    if (cards.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.empty_list_message))
        }

        return
    }

    val pagerState = rememberPagerState(pageCount = { cards.size })
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { index ->
            Logger.getLogger("HomeScreen").info("Selected index: $index")
            homeViewModel.select(cards[index])
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 32.dp),
        pageSpacing = 16.dp,
    ) { index ->
        val card = cards[index]
        val currentIndex = pagerState.settledPage

        val color by animateColorAsState(
            targetValue = if (currentIndex == index) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.secondaryContainer,
            label = "color"
        )

        Surface(
            modifier = Modifier
                .aspectRatio(10 / 16f)
                .clip(MaterialTheme.shapes.large)
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        homeViewModel.showDeleteDialog(card)
                    }
                ),
            color = color,
            shape = MaterialTheme.shapes.large
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
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
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    modifier = Modifier
                        .rotate(90f)
                        .height(48.dp)
                        .aspectRatio(1f),
                    painter = painterResource(
                        if (isSystemInDarkTheme()) R.drawable.wifi_light
                        else R.drawable.wifi_light
                    ),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.ready_to_use))
                Spacer(modifier = Modifier.weight(1.5f))
            }
        }
    }
}

@Composable
private fun DeleteDialog(
    cardToBeDeleted: Card,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        icon = { Icon(Icons.Default.Delete, contentDescription = null) },
        title = { Text(text = stringResource(R.string.delete_dialog_title, cardToBeDeleted.name)) },
        text = { Text(text = stringResource(R.string.delete_dialog_message)) },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.delete),
                    modifier = Modifier.height(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.delete))
            }
        }
    )
}