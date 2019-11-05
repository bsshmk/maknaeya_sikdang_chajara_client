package com.mksoft.maknaeya_sikdang_chajara.model

import androidx.room.Entity

@Entity(primaryKeys = ["optionID"])
data class OptionState(
    val optionID: Int? = 0,
    val rangeButtonState: Int? = 0
)
//rangeButtonState는 0이면 0번 버튼, 1번이면 1번 버튼의 의미