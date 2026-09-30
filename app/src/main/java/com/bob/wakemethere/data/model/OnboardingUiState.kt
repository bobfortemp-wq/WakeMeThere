package com.bob.wakemethere.data.model

import com.bob.wakemethere.R

data class OnboardingUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 3,
    val isFirstPage: Boolean = true,
    val isLastPage: Boolean = false,
    val topChipTextRes: Int? = null,
    val actionButtonTextRes: Int = R.string.btn_slide1,
)

sealed interface OnboardingNavigationEvent {
    data object NavigateToHome : OnboardingNavigationEvent
    data class ScrollToPage(val page: Int) : OnboardingNavigationEvent
}
