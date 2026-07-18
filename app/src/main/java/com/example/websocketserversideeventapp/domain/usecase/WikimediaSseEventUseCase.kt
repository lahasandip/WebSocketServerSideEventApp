package com.example.websocketserversideeventapp.domain.usecase

import com.example.websocketserversideeventapp.domain.model.SseConnectionStatus
import com.example.websocketserversideeventapp.domain.model.WikiEdit
import com.example.websocketserversideeventapp.domain.repository.WikiEditRepository
import kotlinx.coroutines.flow.Flow

class WikimediaSseEventUseCase(private val repository: WikiEditRepository) {
    fun getWikiEdits(): Flow<WikiEdit> = repository.getWikiEdits()
    fun getConnectionStatus(): Flow<SseConnectionStatus> = repository.getConnectionStatus()
    fun startListening() = repository.startListening()
    fun stopListening() = repository.stopListening()
}
