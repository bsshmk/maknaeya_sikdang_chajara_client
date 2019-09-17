package com.mksoft.maknaeya_sikdang_chajara.model

import androidx.room.Entity

@Entity(primaryKeys = ["id"])
data class RestaurantInfo(
    val id:String,
    val latitude:Double,
    val longitude:Double,
    val restaurantName:String,
    val imageSrc:String
)