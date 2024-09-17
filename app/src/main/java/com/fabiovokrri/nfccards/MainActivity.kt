package com.fabiovokrri.nfccards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fabiovokrri.nfccards.ui.NFCCardsNavHost
import com.fabiovokrri.nfccards.ui.theme.NFCCardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NFCCardsTheme {
                NFCCardsNavHost()
            }
        }
    }
}