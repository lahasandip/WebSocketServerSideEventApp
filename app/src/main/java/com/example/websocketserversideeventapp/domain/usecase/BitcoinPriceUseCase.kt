package com.example.websocketserversideeventapp.domain.usecase

import com.example.websocketserversideeventapp.domain.model.BitcoinPrice
import com.example.websocketserversideeventapp.domain.model.SseConnectionStatus
import com.example.websocketserversideeventapp.domain.repository.BitcoinRepository
import kotlinx.coroutines.flow.Flow

class BitcoinPriceUseCase(private val repository: BitcoinRepository) {
    fun getBitcoinPrices(): Flow<BitcoinPrice> = repository.getBitcoinPrices()
    fun getConnectionStatus(): Flow<SseConnectionStatus> = repository.getConnectionStatus()
    fun startListening() = repository.startListening()
    fun stopListening() = repository.stopListening()
    fun changeAsset(symbol: String) = repository.changeAsset(symbol)
}
