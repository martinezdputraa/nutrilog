package com.martinezdputra.nutrilog.onboarding_presentation.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martinezdputra.nutrilog.core.domain.model.ActivityLevel
import com.martinezdputra.nutrilog.core.domain.model.GoalType
import com.martinezdputra.nutrilog.core.domain.preferences.Preferences
import com.martinezdputra.nutrilog.core.navigation.Route
import com.martinezdputra.nutrilog.core.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    private val _selectedGoal = MutableStateFlow<GoalType>(GoalType.KeepWeight)
    val selectedGoal = _selectedGoal.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onGoalTypeSelect(goalType: GoalType) {
        _selectedGoal.value = goalType
    }

    fun onNextClick() {
        viewModelScope.launch {
            preferences.saveGoalType(selectedGoal.value)
            _uiEvent.send(UiEvent.Navigate(Route.NUTRIENT_GOAL))
        }
    }
}
