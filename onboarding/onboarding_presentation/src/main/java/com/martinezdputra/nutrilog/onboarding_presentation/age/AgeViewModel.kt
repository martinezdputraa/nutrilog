package com.martinezdputra.nutrilog.onboarding_presentation.age

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martinezdputra.nutrilog.core.R
import com.martinezdputra.nutrilog.core.domain.preferences.Preferences
import com.martinezdputra.nutrilog.core.domain.use_case.FilterOutDigits
import com.martinezdputra.nutrilog.core.navigation.Route
import com.martinezdputra.nutrilog.core.util.UiEvent
import com.martinezdputra.nutrilog.core.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgeViewModel @Inject constructor(
    private val preferences: Preferences,
    private val filterOutUseCase: FilterOutDigits,
) : ViewModel() {
    private val _age = MutableStateFlow("20")
    val age = _age.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onAgeEnter(age: String) {
        if(age.length <= 3) {
            _age.value = filterOutUseCase(age)
        }
    }

    fun onNextClick() {
        viewModelScope.launch {
            val ageNumber = age.value.toIntOrNull() ?: kotlin.run {
                _uiEvent.send(
                    UiEvent.ShowSnackbar(
                        UiText.StringResource(R.string.error_age_cant_be_empty)
                    )
                )
                return@launch
            }
            preferences.saveAge(ageNumber)
            _uiEvent.send(UiEvent.Navigate(Route.HEIGHT))
        }
    }
}
