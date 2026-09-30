package com.bob.wakemethere.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.bob.wakemethere.data.model.PermissionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PermissionRepositoryImpl : PermissionRepository {

    override suspend fun checkPermissionStatus(
        context: Context,
        permissionType: PermissionType,
    ): Boolean = withContext(Dispatchers.IO) {
        when (permissionType) {
            PermissionType.FINE_LOCATION -> {
                isGranted(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                        isGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            PermissionType.BACKGROUND_LOCATION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    isGranted(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    true
                }
            }
            PermissionType.POST_NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    isGranted(context, Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    true
                }
            }
        }
    }

    private fun isGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
