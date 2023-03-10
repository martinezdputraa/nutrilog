package com.martinezdputra.nutrilog.navigation

import androidx.navigation.NavController
import com.martinezdputra.nutrilog.core.util.UiEvent

fun NavController.navigate(event: UiEvent.Navigate) {
    this.navigate(event.route)
}
