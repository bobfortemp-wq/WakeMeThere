package com.bob.wakemethere.data.model

data class PermissionUiState(
    val permissionType: PermissionType,
    val isGranted: Boolean = false,
)
