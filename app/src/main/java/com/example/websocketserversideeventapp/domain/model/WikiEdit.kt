package com.example.websocketserversideeventapp.domain.model

data class WikiEdit(
    val id: Long,
    val type: String,
    val title: String,
    val user: String,
    val bot: Boolean,
    val wiki: String,
    val comment: String,
    val timestamp: Long,
    val serverUrl: String,
    val changeLength: Int
)
