package com.wm.example

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

object PermissionUtil {

    private const val REQUEST_PERMISSION = 100

    private val permissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    fun requestPermissions(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                val permissionResult = ActivityCompat.checkSelfPermission(activity, permission)
                if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                    return false
                } else {
                    ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION)
                    return false
                }
            }
        }
        return true
    }
}