package com.mksoft.maknaeya_sikdang_chajara.utils


import android.Manifest
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.mksoft.maknaeya_sikdang_chajara.App
import java.util.ArrayList

class PermisionMethod {
    var isPermission = true


    fun tedPermission() {

        val permissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                // 권한 요청 성공
                isPermission = true

            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                // 권한 요청 실패
                isPermission = false

            }
        }

        TedPermission.with(App.applicationContext())
            .setPermissionListener(permissionListener)
            .setRationaleMessage("위치 권한 필요해요.")
            .setDeniedMessage("위치 권한을 거부하셨습니다. 설정에 가서 변경해주세요.")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .check()

    }
    fun getPermission(): Boolean? {
        return isPermission
    }
}
