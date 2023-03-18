package com.martinezdputra.nutrilog.tracker_presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martinezdputra.nutrilog.core.domain.use_case.FilterOutDigits
import com.martinezdputra.nutrilog.core.util.UiEvent
import com.martinezdputra.nutrilog.core.util.UiText
import com.martinezdputra.nutrilog.tracker_domain.use_case.TrackerUseCases
import com.martinezdputra.nutrilog.core.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val trackerUseCases: TrackerUseCases,
    private val filterOutDigits: FilterOutDigits,
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: SearchEvent) {
        when(event) {
            is SearchEvent.OnQueryChange -> {
                _state.value = state.value.copy(
                    query = event.query,
                )
            }
            is SearchEvent.OnAmountForFoodChange -> {
                _state.value = state.value.copy(
                    trackableFoodStates = state.value.trackableFoodStates.map {
                        if(it.food == event.food) {
                            it.copy(amount = filterOutDigits(event.amount))
                        } else it
                    }
                )
            }
            SearchEvent.OnSearch -> {
                executeSearch()
            }
            is SearchEvent.OnSearchFocusChange -> {
                _state.value = state.value.copy(
                    isHintVisible = !event.isFocused && state.value.query.isBlank()
                )
            }
            is SearchEvent.OnToggleTrackableFood -> {
                _state.value = state.value.copy(
                    trackableFoodStates = state.value.trackableFoodStates.map {
                        if(it.food == event.food) {
                            it.copy(isExpanded = !it.isExpanded)
                        } else it
                    }
                )
            }
            is SearchEvent.OnTrackFoodClick -> {
                trackFood(event)
            }
        }
    }

    private fun executeSearch() {
        viewModelScope.launch {
            _state.value = state.value.copy(
                isSearching = true,
                trackableFoodStates = emptyList(),
            )
            trackerUseCases
                .searchFood(state.value.query)
                .onSuccess { foods ->
                    _state.value = state.value.copy(
                        trackableFoodStates = foods.map {
                            TrackableFoodUiState(it)
                        },
                        isSearching = false,
                        query = "",
                    )
                }
                .onFailure {
                    _state.value = state.value.copy(isSearching = false)
                    _uiEvent.send(
                        UiEvent.ShowSnackbar(
                            UiText.StringResource(R.string.error_something_went_wrong)
                        )
                    )
                }
        }
    }

    private fun trackFood(event: SearchEvent.OnTrackFoodClick) {
        viewModelScope.launch {
            val uiState = state.value.trackableFoodStates.find { it.food == event.food }
            trackerUseCases.trackFood(
                food = uiState?.food ?: return@launch,
                amount = uiState.amount.toIntOrNull() ?: return@launch,
                mealType = event.mealType,
                date = event.date
            )
            _uiEvent.send(UiEvent.NavigateUp)
        }
    }
}
