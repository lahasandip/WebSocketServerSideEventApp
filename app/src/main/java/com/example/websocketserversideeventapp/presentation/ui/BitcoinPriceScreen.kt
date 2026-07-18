package com.example.websocketserversideeventapp.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.websocketserversideeventapp.domain.model.BitcoinPrice
import com.example.websocketserversideeventapp.domain.model.SseConnectionStatus
import com.example.websocketserversideeventapp.presentation.viewmodel.BitcoinUiState
import com.example.websocketserversideeventapp.presentation.viewmodel.BitcoinViewModel
import com.example.websocketserversideeventapp.ui.theme.WebSocketServerSideEventAppTheme
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitcoinPriceScreen(
    viewModel: BitcoinViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()

    BitcoinPriceScreenContent(
        uiState = uiState,
        connectionStatus = connectionStatus,
        onAssetSelected = { viewModel.selectAsset(it) },
        onToggleConnection = { viewModel.toggleConnection() },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitcoinPriceScreenContent(
    uiState: BitcoinUiState,
    connectionStatus: SseConnectionStatus,
    onAssetSelected: (String) -> Unit,
    onToggleConnection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bitcoin WebSocket Tracker", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Connection Status
            StatusIndicator(status = connectionStatus)
            
            Spacer(modifier = Modifier.height(16.dp))

            // TWO-WAY INTERACTION: Asset Selection Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val assets = listOf("BTCUSDT", "ETHUSDT", "SOLUSDT", "DOGEUSDT")
                assets.forEach { asset ->
                    FilterChip(
                        selected = uiState.selectedAsset == asset,
                        onClick = { onAssetSelected(asset) },
                        label = { Text(asset.replace("USDT", "")) },
                        enabled = connectionStatus == SseConnectionStatus.CONNECTED
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Price Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Current ${uiState.selectedAsset.replace("USDT", "")} Price",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = uiState.currentPrice?.let { "$${String.format("%,.2f", it.price)}" } ?: "$0.00",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Toggle Button
            Button(
                onClick = onToggleConnection,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (connectionStatus == SseConnectionStatus.CONNECTED) "Stop Tracking" else "Start Tracking")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Price History
            Text(
                text = "Price History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.priceHistory) { priceUpdate ->
                    PriceHistoryItem(priceUpdate)
                }
            }
        }
    }
}

@Composable
fun PriceHistoryItem(priceUpdate: BitcoinPrice) {
    val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    val timeString = sdf.format(Date(priceUpdate.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = timeString, style = MaterialTheme.typography.bodySmall)
            Text(
                text = "$${String.format("%,.2f", priceUpdate.price)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BitcoinPriceScreenPreview() {
    val samplePriceHistory = listOf(
        BitcoinPrice(price = 65432.10, timestamp = System.currentTimeMillis()),
        BitcoinPrice(price = 65420.50, timestamp = System.currentTimeMillis() - 10000),
        BitcoinPrice(price = 65415.00, timestamp = System.currentTimeMillis() - 20000)
    )
    val sampleUiState = BitcoinUiState(
        selectedAsset = "BTCUSDT",
        currentPrice = samplePriceHistory[0],
        priceHistory = samplePriceHistory
    )

    WebSocketServerSideEventAppTheme {
        BitcoinPriceScreenContent(
            uiState = sampleUiState,
            connectionStatus = SseConnectionStatus.CONNECTED,
            onAssetSelected = {},
            onToggleConnection = {}
        )
    }
}
