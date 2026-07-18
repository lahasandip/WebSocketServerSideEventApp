package com.example.websocketserversideeventapp.data.remote

import com.example.websocketserversideeventapp.data.model.BitcoinPriceDto
import com.example.websocketserversideeventapp.domain.model.SseConnectionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class BitcoinWebSocketService(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _prices = MutableSharedFlow<BitcoinPriceDto>(
        replay = 1,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val prices: Flow<BitcoinPriceDto> = _prices.asSharedFlow()

    private val _status = MutableStateFlow(SseConnectionStatus.DISCONNECTED)
    val status: Flow<SseConnectionStatus> = _status.asStateFlow()

    private var webSocket: WebSocket? = null

    fun startStreaming() {
        if (webSocket != null) {
            android.util.Log.d("BitcoinWS", "WebSocket already exists, skipping start")
            return
        }
        
        android.util.Log.d("BitcoinWS", "Starting connection to Binance...")
        _status.value = SseConnectionStatus.CONNECTING

        try {
            // Use base WebSocket URL
            val request = Request.Builder()
                .url("wss://stream.binance.com:9443/ws")
                .build()

            webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    android.util.Log.i("BitcoinWS", "Binance WebSocket Opened successfully")
                    _status.value = SseConnectionStatus.CONNECTED
                    
                    // Initial subscription
                    subscribeToAsset("btcusdt")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    serviceScope.launch {
                        try {
                            val dto = json.decodeFromString<BitcoinPriceDto>(text)
                            if (dto.price != null) {
                                _prices.emit(dto)
                            }
                        } catch (e: Exception) {
                            // Ignore subscription confirmation messages
                        }
                    }
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    android.util.Log.w("BitcoinWS", "Server is closing: $code / $reason")
                    webSocket.close(1000, null)
                    _status.value = SseConnectionStatus.DISCONNECTED
                    this@BitcoinWebSocketService.webSocket = null
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    android.util.Log.e("BitcoinWS", "Failure: ${t.message}")
                    _status.value = SseConnectionStatus.ERROR
                    this@BitcoinWebSocketService.webSocket = null
                }
            })
        } catch (e: Exception) {
            android.util.Log.e("BitcoinWS", "Error creating WebSocket: ${e.message}")
            _status.value = SseConnectionStatus.ERROR
        }
    }

    /**
     * TWO-WAY: Sending a command to the server
     */
    fun subscribeToAsset(symbol: String) {
        val jsonCommand = """
            {
              "method": "SUBSCRIBE",
              "params": ["${symbol.lowercase()}@trade"],
              "id": 1
            }
        """.trimIndent()
        android.util.Log.d("BitcoinWS", "Sending: $jsonCommand")
        webSocket?.send(jsonCommand)
    }

    /**
     * TWO-WAY: Sending a command to the server
     */
    fun unsubscribeFromAsset(symbol: String) {
        val jsonCommand = """
            {
              "method": "UNSUBSCRIBE",
              "params": ["${symbol.lowercase()}@trade"],
              "id": 1
            }
        """.trimIndent()
        android.util.Log.d("BitcoinWS", "Sending: $jsonCommand")
        webSocket?.send(jsonCommand)
    }

    fun stopStreaming() {
        webSocket?.close(1000, "User stopped")
        webSocket = null
        _status.value = SseConnectionStatus.DISCONNECTED
    }
}
