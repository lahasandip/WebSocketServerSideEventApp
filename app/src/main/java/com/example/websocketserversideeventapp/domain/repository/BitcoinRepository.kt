package com.example.websocketserversideeventapp.domain.repository

import com.example.websocketserversideeventapp.domain.model.BitcoinPrice
import com.example.websocketserversideeventapp.domain.model.SseConnectionStatus
import kotlinx.coroutines.flow.Flow

interface BitcoinRepository {
    fun getBitcoinPrices(): Flow<BitcoinPrice>
    fun getConnectionStatus(): Flow<SseConnectionStatus>
    fun startListening()
    fun stopListening()
    fun changeAsset(symbol: String)
}
