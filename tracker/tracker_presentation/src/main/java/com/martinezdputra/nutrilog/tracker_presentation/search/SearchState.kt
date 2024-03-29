package com.martinezdputra.nutrilog.tracker_presentation.search

data class SearchState(
    val query: String = "",
    val isHintVisible: Boolean = false,
    val isSearching: Boolean = false,
    val trackableFoodStates: List<TrackableFoodUiState> = emptyList()
)
