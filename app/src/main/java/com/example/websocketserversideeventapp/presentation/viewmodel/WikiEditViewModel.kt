package com.example.websocketserversideeventapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.websocketserversideeventapp.domain.model.SseConnectionStatus
import com.example.websocketserversideeventapp.domain.model.WikiEdit
import com.example.websocketserversideeventapp.domain.usecase.WikimediaSseEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WikiEditViewModel(
    private val wikimediaSseEventUseCase: WikimediaSseEventUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WikiEditUiState())
    val uiState: StateFlow<WikiEditUiState> = _uiState.asStateFlow()

    // Keep the source-of-truth list of edits in memory
    private var rawEditsList = listOf<WikiEdit>()

    val connectionStatus: StateFlow<SseConnectionStatus> = wikimediaSseEventUseCase.getConnectionStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SseConnectionStatus.DISCONNECTED
        )

    init {
        // Observe connection status changes and sync with UI state
        viewModelScope.launch {
            connectionStatus.collect { status ->
                _uiState.update { it.copy(connectionStatus = status) }
            }
        }

        // Collect incoming wiki edits
        viewModelScope.launch {
            wikimediaSseEventUseCase.getWikiEdits().collect { edit ->
                // Add to raw list
                val newRawList = rawEditsList.toMutableList()
                newRawList.add(0, edit)
                if (newRawList.size > 200) {
                    newRawList.removeAt(newRawList.lastIndex)
                }
                rawEditsList = newRawList

                // Update UI state with new edit and updated stats
                _uiState.update { current ->
                    val newTotal = current.totalCount + 1
                    val newBotCount = current.botCount + if (edit.bot) 1 else 0
                    val newCharChanges = current.totalCharChanges + edit.changeLength

                    current.copy(
                        totalCount = newTotal,
                        botCount = newBotCount,
                        totalCharChanges = newCharChanges,
                        filteredEdits = applyFilters(
                            rawList = rawEditsList,
                            query = current.searchQuery,
                            wiki = current.searchWiki,
                            excludeBots = current.excludeBots
                        )
                    )
                }
            }
        }
    }

    fun toggleConnection() {
        if (connectionStatus.value == SseConnectionStatus.CONNECTED) {
            wikimediaSseEventUseCase.stopListening()
        } else {
            wikimediaSseEventUseCase.startListening()
        }
    }

    fun setExcludeBots(exclude: Boolean) {
        _uiState.update { current ->
            current.copy(
                excludeBots = exclude,
                filteredEdits = applyFilters(
                    rawList = rawEditsList,
                    query = current.searchQuery,
                    wiki = current.searchWiki,
                    excludeBots = exclude
                )
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredEdits = applyFilters(
                    rawList = rawEditsList,
                    query = query,
                    wiki = current.searchWiki,
                    excludeBots = current.excludeBots
                )
            )
        }
    }

    fun setSearchWiki(wiki: String) {
        _uiState.update { current ->
            current.copy(
                searchWiki = wiki,
                filteredEdits = applyFilters(
                    rawList = rawEditsList,
                    query = current.searchQuery,
                    wiki = wiki,
                    excludeBots = current.excludeBots
                )
            )
        }
    }

    fun clearHistory() {
        rawEditsList = emptyList()
        _uiState.update {
            WikiEditUiState(
                connectionStatus = connectionStatus.value,
                filteredEdits = emptyList(),
                totalCount = 0,
                botCount = 0,
                totalCharChanges = 0L,
                searchQuery = it.searchQuery,
                searchWiki = it.searchWiki,
                excludeBots = it.excludeBots
            )
        }
    }

    private fun applyFilters(
        rawList: List<WikiEdit>,
        query: String,
        wiki: String,
        excludeBots: Boolean
    ): List<WikiEdit> {
        return rawList.filter { edit ->
            val matchesQuery = query.isEmpty() ||
                    edit.title.contains(query, ignoreCase = true) ||
                    edit.user.contains(query, ignoreCase = true) ||
                    edit.comment.contains(query, ignoreCase = true)

            val matchesWiki = wiki.isEmpty() ||
                    edit.wiki.contains(wiki, ignoreCase = true)

            val matchesBot = !excludeBots || !edit.bot

            matchesQuery && matchesWiki && matchesBot
        }
    }

    override fun onCleared() {
        super.onCleared()
        wikimediaSseEventUseCase.stopListening()
    }
}

data class WikiEditUiState(
    val connectionStatus: SseConnectionStatus = SseConnectionStatus.DISCONNECTED,
    val filteredEdits: List<WikiEdit> = emptyList(),
    val totalCount: Int = 0,
    val botCount: Int = 0,
    val totalCharChanges: Long = 0L,
    val searchQuery: String = "",
    val searchWiki: String = "",
    val excludeBots: Boolean = false
)
