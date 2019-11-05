package com.mksoft.maknaeya_sikdang_chajara.model

import androidx.room.Entity

@Entity(primaryKeys = ["restaurant_id"])
data class Restaurant(
    val restaurant_id: String,
    val restaurant_name: String,
    val web_link: String,
    val category: String,
    val phone_number: String,
    val rating: String,
    val location: String,
    val gps_N: String,
    val gps_E: String,
    val image_src: String,
    val main_menu: String,
    val main_menu_price: String,
    val review_count_number: String,
    val distance: Double
)