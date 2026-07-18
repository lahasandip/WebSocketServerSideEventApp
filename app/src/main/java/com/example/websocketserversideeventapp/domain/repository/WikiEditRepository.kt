package com.example.websocketserversideeventapp.domain.repository

import com.example.websocketserversideeventapp.domain.model.SseConnectionStatus
import com.example.websocketserversideeventapp.domain.model.WikiEdit
import kotlinx.coroutines.flow.Flow

interface WikiEditRepository {
    fun getWikiEdits(): Flow<WikiEdit>
    fun getConnectionStatus(): Flow<SseConnectionStatus>
    fun startListening()
    fun stopListening()
}
