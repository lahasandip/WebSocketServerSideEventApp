package com.example.websocketserversideeventapp.domain.model

data class BitcoinPrice(
    val price: Double,
    val timestamp: Long = System.currentTimeMillis()
)
