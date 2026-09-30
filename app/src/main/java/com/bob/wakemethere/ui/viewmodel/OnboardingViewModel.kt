package com.bob.wakemethere.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bob.wakemethere.R
import com.bob.wakemethere.data.model.OnboardingNavigationEvent
import com.bob.wakemethere.data.model.OnboardingUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<OnboardingNavigationEvent>()
    val navigationEvent: SharedFlow<OnboardingNavigationEvent> = _navigationEvent.asSharedFlow()

    fun onPageChanged(position: Int) {
        _uiState.update { currentState ->
            val total = currentState.totalPages
            val isFirst = position == 0
            val isLast = position == (total - 1)

            val chipTextRes = when (position) {
                1 -> R.string.top_chip_slide2
                2 -> R.string.top_chip_slide3
                else -> null
            }

            val buttonTextRes = when (position) {
                0 -> R.string.btn_slide1
                1 -> R.string.btn_slide2
                else -> R.string.btn_slide3
            }

            currentState.copy(
                currentPage = position,
                isFirstPage = isFirst,
                isLastPage = isLast,
                topChipTextRes = chipTextRes,
                actionButtonTextRes = buttonTextRes,
            )
        }
    }

    fun onMainActionClicked() {
        val current = _uiState.value.currentPage
        val total = _uiState.value.totalPages
        if (current < (total - 1)) {
            viewModelScope.launch {
                _navigationEvent.emit(OnboardingNavigationEvent.ScrollToPage(current + 1))
            }
        } else {
            onSkipClicked()
        }
    }

    fun onBackClicked() {
        val current = _uiState.value.currentPage
        if (current > 0) {
            viewModelScope.launch {
                _navigationEvent.emit(OnboardingNavigationEvent.ScrollToPage(current - 1))
            }
        }
    }

    fun onSkipClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(OnboardingNavigationEvent.NavigateToHome)
        }
    }
}
