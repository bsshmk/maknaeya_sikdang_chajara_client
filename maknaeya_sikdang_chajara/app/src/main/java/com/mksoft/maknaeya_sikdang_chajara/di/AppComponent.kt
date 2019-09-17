package com.mksoft.maknaeya_sikdang_chajara.di

import com.mksoft.maknaeya_sikdang_chajara.viewmodel.FoodMapViewModel
import com.mksoft.mkjw_second_project.di.module.DataBaseModule
import com.mksoft.mkjw_second_project.di.module.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [NetworkModule::class,
        DataBaseModule::class]
)
interface AppComponent {
    fun inject(foodMapViewModel: FoodMapViewModel)
    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        fun networkModule(networkModule: NetworkModule): Builder
        fun dataBaseModule(dataBaseModule: DataBaseModule): Builder
        //App에서 미리 선언할 친구들을 여기서 선언하자
    }

}