package com.martinezdputra.nutrilog.onboarding_presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martinezdputra.nutrilog.core.domain.model.ActivityLevel
import com.martinezdputra.nutrilog.core.domain.preferences.Preferences
import com.martinezdputra.nutrilog.core.navigation.Route
import com.martinezdputra.nutrilog.core.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityLevelViewModel @Inject constructor(
    private val preferences: Preferences,
) : ViewModel() {

    private val _selectedActivityLevel = MutableStateFlow<ActivityLevel>(ActivityLevel.Medium)
    val selectedActivityLevel = _selectedActivityLevel.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onActivityLevelSelect(activityLevel: ActivityLevel) {
        _selectedActivityLevel.value = activityLevel
    }

    fun onNextClick() {
        viewModelScope.launch {
            preferences.saveActivityLevel(selectedActivityLevel.value)
            _uiEvent.send(UiEvent.Navigate(Route.GOAL))
        }
    }
}
