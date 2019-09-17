package com.mksoft.maknaeya_sikdang_chajara.base

import android.app.Application
import androidx.lifecycle.ViewModel
import com.mksoft.maknaeya_sikdang_chajara.App
import com.mksoft.maknaeya_sikdang_chajara.di.AppComponent
import com.mksoft.maknaeya_sikdang_chajara.di.DaggerAppComponent
import com.mksoft.maknaeya_sikdang_chajara.viewmodel.FoodMapViewModel
import com.mksoft.mkjw_second_project.di.module.DataBaseModule
import com.mksoft.mkjw_second_project.di.module.NetworkModule

abstract class BaseViewModel: ViewModel(){
    private val injector: AppComponent =
        DaggerAppComponent.builder()
            .dataBaseModule(DataBaseModule(App.applicationContext() as Application))
            .networkModule(NetworkModule())
            .build()//여기서 한번 초기화시켜주자

    init{
        inject()
    }
    private fun inject(){
        when(this){
            is FoodMapViewModel -> injector.inject(this)

        }
    }
}
//주입을 위한 베이스 뷰모델