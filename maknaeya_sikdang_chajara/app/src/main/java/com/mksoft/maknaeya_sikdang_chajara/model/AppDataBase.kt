package com.mksoft.maknaeya_sikdang_chajara.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Restaurant::class, OptionState::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun restaurantInfoDao(): RestaurantInfoDao
    abstract fun optionStateDao(): OptionStateDao
}