package com.bob.wakemethere.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.bob.wakemethere.data.model.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun toggleArmState() {
        _uiState.update { currentState ->
            currentState.copy(isArmed = !currentState.isArmed)
        }
    }

    fun toggleSilentNap(enabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(silentNapEnabled = enabled)
        }
    }
}
