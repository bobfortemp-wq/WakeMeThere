package com.bob.wakemethere.data.repository

import android.content.Context
import com.bob.wakemethere.data.model.PermissionType

interface PermissionRepository {
    suspend fun checkPermissionStatus(context: Context, permissionType: PermissionType): Boolean
}
