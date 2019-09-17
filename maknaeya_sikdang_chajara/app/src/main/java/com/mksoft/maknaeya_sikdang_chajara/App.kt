package com.mksoft.maknaeya_sikdang_chajara

import android.app.Application
import android.content.Context
import com.mksoft.maknaeya_sikdang_chajara.utils.NCP_client_ID
import com.naver.maps.map.NaverMapSdk



class App : Application(){
    init{
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(NCP_client_ID)
    }
    companion object {
        private var instance: App? = null
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}