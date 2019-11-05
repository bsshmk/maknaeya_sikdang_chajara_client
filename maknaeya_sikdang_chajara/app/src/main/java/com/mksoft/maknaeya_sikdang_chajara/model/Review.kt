package com.mksoft.maknaeya_sikdang_chajara.model

import androidx.room.Entity

@Entity(primaryKeys = ["review_id"])
data class Review(
    val review_id: String,
    val restaurant_id: String,
    val writer_id: String,
    val review_contents: String,
    val review_score: String
)