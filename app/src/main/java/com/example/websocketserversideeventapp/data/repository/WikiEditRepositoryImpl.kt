package com.example.websocketserversideeventapp.data.repository

import com.example.websocketserversideeventapp.data.remote.WikimediaSseService
import com.example.websocketserversideeventapp.domain.model.SseConnectionStatus
import com.example.websocketserversideeventapp.domain.model.WikiEdit
import com.example.websocketserversideeventapp.domain.repository.WikiEditRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.atomic.AtomicLong

class WikiEditRepositoryImpl(
    private val sseService: WikimediaSseService
) : WikiEditRepository {

    private val fallbackIdGenerator = AtomicLong(1)

    override fun getWikiEdits(): Flow<WikiEdit> {
        return sseService.rawEdits.map { dto ->
            dto.toDomain(fallbackId = fallbackIdGenerator.getAndIncrement())
        }
    }

    override fun getConnectionStatus(): Flow<SseConnectionStatus> {
        return sseService.status
    }

    override fun startListening() {
        sseService.startStreaming()
    }

    override fun stopListening() {
        sseService.stopStreaming()
    }
}
