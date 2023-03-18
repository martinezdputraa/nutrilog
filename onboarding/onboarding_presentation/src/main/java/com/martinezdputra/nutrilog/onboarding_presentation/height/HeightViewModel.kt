package com.martinezdputra.nutrilog.onboarding_presentation.height

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.martinezdputra.nutrilog.core.R
import com.martinezdputra.nutrilog.core.domain.preferences.Preferences
import com.martinezdputra.nutrilog.core.domain.use_case.FilterOutDigits
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
class HeightViewModel @Inject constructor(
    private val preferences: Preferences,
    private val filterOutUseCase: FilterOutDigits,
) : ViewModel() {
    private val _height = MutableStateFlow("180")
    val height = _height.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onHeightEnter(height: String) {
        if(height.length <= 3) {
            _height.value = filterOutUseCase(height)
        }
    }

    fun onNextClick() {
        viewModelScope.launch {
            val heightNumber = height.value.toIntOrNull() ?: kotlin.run {
                _uiEvent.send(
                    UiEvent.ShowSnackbar(
                        UiText.StringResource(R.string.error_height_cant_be_empty)
                    )
                )
                return@launch
            }
            preferences.saveHeight(heightNumber)
            _uiEvent.send(UiEvent.Success)
        }
    }
}
