package com.jxd20072.automationapp.core.data.repository

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.jxd20072.automationapp.utils.associateWithIndexed
import jakarta.inject.Inject

class PermissionsRepository @Inject constructor(
    private val context: Context,
) {
    fun getAllPermissions(): Map<String, Boolean> {
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        )

        val requestedPermissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
        val requestedPermissionsFlags =
            packageInfo.requestedPermissionsFlags?.toList() ?: emptyList()

        return requestedPermissions.associateWithIndexed { _, index ->
            (requestedPermissionsFlags[index] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0
        }
    }

    fun checkPermission(permission: String): Boolean {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        return isGranted
    }
}