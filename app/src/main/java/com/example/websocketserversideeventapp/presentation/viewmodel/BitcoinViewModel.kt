package com.example.websocketserversideeventapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.websocketserversideeventapp.domain.model.BitcoinPrice
import com.example.websocketserversideeventapp.domain.model.SseConnectionStatus
import com.example.websocketserversideeventapp.domain.usecase.BitcoinPriceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BitcoinViewModel(
    private val bitcoinPriceUseCase: BitcoinPriceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BitcoinUiState())
    val uiState: StateFlow<BitcoinUiState> = _uiState.asStateFlow()

    val connectionStatus: StateFlow<SseConnectionStatus> = bitcoinPriceUseCase.getConnectionStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SseConnectionStatus.DISCONNECTED
        )

    init {
        viewModelScope.launch {
            bitcoinPriceUseCase.getBitcoinPrices().collect { newPrice ->
                _uiState.update { current ->
                    val newList = current.priceHistory.toMutableList()
                    newList.add(0, newPrice)
                    if (newList.size > 50) newList.removeAt(newList.lastIndex)
                    
                    current.copy(
                        currentPrice = newPrice,
                        priceHistory = newList
                    )
                }
            }
        }
    }

    fun toggleConnection() {
        if (connectionStatus.value == SseConnectionStatus.CONNECTED) {
            bitcoinPriceUseCase.stopListening()
        } else {
            bitcoinPriceUseCase.startListening()
        }
    }

    /**
     * TWO-WAY: User interaction to change the stream content
     */
    fun selectAsset(symbol: String) {
        _uiState.update { 
            it.copy(
                selectedAsset = symbol,
                priceHistory = emptyList(),
                currentPrice = null
            ) 
        }
        bitcoinPriceUseCase.changeAsset(symbol)
    }

    override fun onCleared() {
        super.onCleared()
        bitcoinPriceUseCase.stopListening()
    }
}

data class BitcoinUiState(
    val selectedAsset: String = "BTCUSDT",
    val currentPrice: BitcoinPrice? = null,
    val priceHistory: List<BitcoinPrice> = emptyList()
)
