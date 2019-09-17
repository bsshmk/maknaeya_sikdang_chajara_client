package com.mksoft.mkjw_second_project.di.module

import android.app.Application
import androidx.room.Room
import com.mksoft.maknaeya_sikdang_chajara.model.AppDataBase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
@Suppress("unused")
class DataBaseModule(private val applicationContext: Application){

    @Provides
    @Singleton
    fun provideApplication(): Application = applicationContext

    @Provides
    @Singleton
    internal  fun provideAppDataBase(applicationContext: Application): AppDataBase {
        return Room.databaseBuilder(applicationContext, AppDataBase::class.java, "r.db").build()
    }

}