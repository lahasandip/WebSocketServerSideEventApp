package com.example.websocketserversideeventapp.data.remote

import com.example.websocketserversideeventapp.data.model.WikiEditDto
import com.example.websocketserversideeventapp.domain.model.SseConnectionStatus
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

class WikimediaSseService(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
    private val wikimediaApi: WikimediaApi
) {
    private val _rawEdits = MutableSharedFlow<WikiEditDto>(
        replay = 0,
        extraBufferCapacity = 500,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val rawEdits: Flow<WikiEditDto> = _rawEdits.asSharedFlow()

    private val _status = MutableStateFlow(SseConnectionStatus.DISCONNECTED)
    val status: Flow<SseConnectionStatus> = _status.asStateFlow()

    private var eventSource: EventSource? = null

    fun startStreaming() {
        if (eventSource != null) return // Already active

        _status.value = SseConnectionStatus.CONNECTING

        val request = wikimediaApi.getRecentChangesRequest()

        val listener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                android.util.Log.d("WikimediaSseService", "SSE Connection Opened: ${response.code}")
                _status.value = SseConnectionStatus.CONNECTED
            }

            override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                try {
                    val dto = json.decodeFromString<WikiEditDto>(data)
                    _rawEdits.tryEmit(dto)
                } catch (e: Exception) {
                    android.util.Log.e("WikimediaSseService", "Error parsing event: ${e.message}")
                    e.printStackTrace()
                }
            }

            override fun onClosed(eventSource: EventSource) {
                _status.value = SseConnectionStatus.DISCONNECTED
                this@WikimediaSseService.eventSource = null
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                _status.value = SseConnectionStatus.ERROR
                this@WikimediaSseService.eventSource = null
                
                // Enhanced logging to help diagnose the issue
                val errorMessage = t?.message ?: "Unknown error"
                val responseCode = response?.code ?: "No response"
                android.util.Log.e("WikimediaSseService", "SSE Failure: $errorMessage, Code: $responseCode")

                t?.printStackTrace()
            }
        }

        val factory = EventSources.createFactory(okHttpClient)
        eventSource = factory.newEventSource(request, listener)
    }

    fun stopStreaming() {
        eventSource?.cancel()
        eventSource = null
        _status.value = SseConnectionStatus.DISCONNECTED
    }
}
