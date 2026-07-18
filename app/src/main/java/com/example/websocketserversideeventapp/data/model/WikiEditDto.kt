package com.example.websocketserversideeventapp.data.model

import com.example.websocketserversideeventapp.domain.model.WikiEdit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WikiEditDto(
    val id: Long? = null,
    val type: String,
    val title: String,
    val user: String,
    val bot: Boolean,
    val wiki: String,
    val comment: String? = null,
    val timestamp: Long,
    @SerialName("server_url") val serverUrl: String? = null,
    val length: LengthDto? = null
) {
    fun toDomain(fallbackId: Long): WikiEdit {
        return WikiEdit(
            id = id ?: fallbackId,
            type = type,
            title = title,
            user = user,
            bot = bot,
            wiki = wiki,
            comment = comment.orEmpty(),
            timestamp = timestamp,
            serverUrl = serverUrl.orEmpty(),
            changeLength = (length?.newLength ?: 0) - (length?.oldLength ?: 0)
        )
    }
}

@Serializable
data class LengthDto(
    @SerialName("old") val oldLength: Int? = null,
    @SerialName("new") val newLength: Int? = null
)
