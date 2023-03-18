package com.martinezdputra.nutrilog.onboarding_presentation.nutrient_goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martinezdputra.nutrilog.core.domain.preferences.Preferences
import com.martinezdputra.nutrilog.core.domain.use_case.FilterOutDigits
import com.martinezdputra.nutrilog.core.util.UiEvent
import com.martinezdputra.nutrilog.onboarding_domain.use_case.ValidateNutrients
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutrientGoalViewModel @Inject constructor(
    private val preferences: Preferences,
    private val filterOutDigits: FilterOutDigits,
    private val validateNutrients: ValidateNutrients,
) : ViewModel() {
    private val _state = MutableStateFlow(NutrientGoalState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: NutrientGoalEvent) {
        when(event) {
            is NutrientGoalEvent.OnCarbRatioEnter -> {
                _state.value = _state.value.copy(
                    carbsRatio = filterOutDigits(event.ratio)
                )
            }
            is NutrientGoalEvent.OnProteinRatioEnter -> {
                _state.value = _state.value.copy(
                    proteinRatio = filterOutDigits(event.ratio)
                )
            }
            is NutrientGoalEvent.OnFatRatioEnter -> {
                _state.value = _state.value.copy(
                    fatRatio = filterOutDigits(event.ratio)
                )
            }
            is NutrientGoalEvent.OnNextClick -> {
                val validationResult = validateNutrients(
                    carbsRatioText = state.value.carbsRatio,
                    proteinRatioText = state.value.proteinRatio,
                    fatRatioText = state.value.fatRatio,
                )
                when(validationResult) {
                    is ValidateNutrients.Result.Success -> {
                        preferences.saveCarbRatio(validationResult.carbsRatio)
                        preferences.saveProteinRatio(validationResult.proteinRatio)
                        preferences.saveFatRatio(validationResult.fatRatio)

                        viewModelScope.launch {
                            _uiEvent.send(
                                UiEvent.Success
                            )
                        }
                    }
                    is ValidateNutrients.Result.Error -> {
                        viewModelScope.launch {
                            _uiEvent.send(
                                UiEvent.ShowSnackbar(validationResult.message)
                            )
                        }
                    }
                }
            }
        }
    }
}
