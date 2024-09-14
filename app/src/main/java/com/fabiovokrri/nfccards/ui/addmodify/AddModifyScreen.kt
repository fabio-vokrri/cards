package com.fabiovokrri.nfccards.ui.addmodify

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiovokrri.nfccards.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddModifyScreen(
    modifier: Modifier = Modifier,
    addModifyViewModel: AddModifyViewModel = viewModel(factory = AddModifyViewModel.Factory),
    onNavigateUp: () -> Unit = {},
) {
    val currentCard by addModifyViewModel.currentCard.collectAsState()
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigateUp()
                        addModifyViewModel.save()
                        focusManager.clearFocus()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = stringResource(R.string.back))
                    }
                },
                title = {
                    Text(
                        if (currentCard.id == null)
                            stringResource(R.string.new_card)
                        else stringResource(R.string.edit_card)
                    )
                }
            )
        },
    ) { _ ->
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier
                    .padding(32.dp)
                    .aspectRatio(10 / 16f),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        value = currentCard.name,
                        onValueChange = { addModifyViewModel.updateName(it) },
                        label = { Text(text = stringResource(R.string.card_name)) },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        modifier = Modifier
                            .height(72.dp)
                            .aspectRatio(1f)
                            .padding(16.dp),
                        painter = painterResource(
                            if (isSystemInDarkTheme()) R.drawable.nfc_logo_light
                            else R.drawable.nfc_logo_dark
                        ),
                        contentDescription = stringResource(R.string.nfc_logo),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.nfc_instructions),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.weight(1.5f))
                }

            }
        }

    }
}