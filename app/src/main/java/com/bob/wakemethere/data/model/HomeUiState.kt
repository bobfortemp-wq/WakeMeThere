package com.bob.wakemethere.data.model

data class HomeUiState(
    val isArmed: Boolean = true,
    val destinationName: String = "Grand Central Terminal",
    val distanceKm: String = "3.8 km",
    val etaMin: String = "~11 min",
    val wakeTriggerRadius: String = "450 m",
    val silentNapEnabled: Boolean = true,
)
