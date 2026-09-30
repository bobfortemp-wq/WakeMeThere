package com.bob.wakemethere.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bob.wakemethere.data.model.PermissionType
import com.bob.wakemethere.data.model.PermissionUiState
import com.bob.wakemethere.data.repository.PermissionRepository
import com.bob.wakemethere.data.repository.PermissionRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PermissionSlideViewModel(
    private val repository: PermissionRepository = PermissionRepositoryImpl(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(PermissionUiState(PermissionType.FINE_LOCATION))
    val uiState: StateFlow<PermissionUiState> = _uiState.asStateFlow()

    fun initPermissionType(permissionType: PermissionType) {
        _uiState.update { it.copy(permissionType = permissionType) }
    }

    fun checkPermissionStatus(context: Context) {
        val currentType = _uiState.value.permissionType
        viewModelScope.launch {
            val isGranted = repository.checkPermissionStatus(context, currentType)
            _uiState.update { it.copy(isGranted = isGranted) }
        }
    }
}
