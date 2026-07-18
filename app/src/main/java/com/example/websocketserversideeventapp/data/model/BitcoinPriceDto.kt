package com.example.websocketserversideeventapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BitcoinPriceDto(
    @SerialName("p") val price: String? = null,
    @SerialName("s") val symbol: String? = null
)
