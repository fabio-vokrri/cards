package com.fabiovokrri.nfccards.ui.addmodify

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigateUp()
                        addModifyViewModel.save()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "back")
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val focusManager = LocalFocusManager.current

            Image(
                painter = painterResource(id = R.drawable.nfc_logo),
                contentDescription = stringResource(R.string.nfc_logo),
                modifier = Modifier
                    .aspectRatio(1f)
                    .height(128.dp)
                    .padding(16.dp)
            )
            Text(text = stringResource(R.string.nfc_instructions))

            Spacer(modifier = Modifier.height(64.dp))

            TextField(
                value = currentCard.name,
                onValueChange = { addModifyViewModel.updateName(it) },
                label = { Text(text = stringResource(R.string.card_name)) },
                keyboardActions = KeyboardActions(
                    onDone = {
                        addModifyViewModel.save()
                        focusManager.clearFocus()
                    }
                )
            )
        }
    }
}