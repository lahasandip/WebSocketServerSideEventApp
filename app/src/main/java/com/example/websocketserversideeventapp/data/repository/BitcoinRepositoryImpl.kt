package com.example.websocketserversideeventapp.data.repository

import com.example.websocketserversideeventapp.data.remote.BitcoinWebSocketService
import com.example.websocketserversideeventapp.domain.model.BitcoinPrice
import com.example.websocketserversideeventapp.domain.model.SseConnectionStatus
import com.example.websocketserversideeventapp.domain.repository.BitcoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class BitcoinRepositoryImpl(
    private val webSocketService: BitcoinWebSocketService
) : BitcoinRepository {

    private var currentSymbol: String = "btcusdt"

    override fun getBitcoinPrices(): Flow<BitcoinPrice> {
        return webSocketService.prices.mapNotNull { dto ->
            dto.price?.toDoubleOrNull()?.let { price ->
                BitcoinPrice(price = price)
            }
        }
    }

    override fun getConnectionStatus(): Flow<SseConnectionStatus> {
        return webSocketService.status
    }

    override fun startListening() {
        webSocketService.startStreaming()
    }

    override fun stopListening() {
        webSocketService.stopStreaming()
    }

    override fun changeAsset(symbol: String) {
        if (symbol != currentSymbol) {
            webSocketService.unsubscribeFromAsset(currentSymbol)
            currentSymbol = symbol
            webSocketService.subscribeToAsset(symbol)
        }
    }
}
