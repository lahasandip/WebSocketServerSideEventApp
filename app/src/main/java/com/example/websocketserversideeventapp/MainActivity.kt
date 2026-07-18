package com.example.websocketserversideeventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.websocketserversideeventapp.presentation.ui.BitcoinPriceScreen
import com.example.websocketserversideeventapp.presentation.ui.WikiEditScreen
import com.example.websocketserversideeventapp.ui.theme.WebSocketServerSideEventAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WebSocketServerSideEventAppTheme {
                var currentTab by remember { mutableIntStateOf(0) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { currentTab = 0 },
                                icon = { Icon(Icons.Default.Public, contentDescription = "SSE") },
                                label = { Text("Wikimedia (SSE)") }
                            )
                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { currentTab = 1 },
                                icon = { Icon(Icons.Default.CurrencyBitcoin, contentDescription = "WS") },
                                label = { Text("Bitcoin (WS)") }
                            )
                        }
                    }
                ) { innerPadding ->
                    when (currentTab) {
                        0 -> BitcoinPriceScreen(modifier = Modifier.padding(innerPadding))
                        1 -> WikiEditScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}
