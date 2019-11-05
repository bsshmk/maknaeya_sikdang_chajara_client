package com.mksoft.maknaeya_sikdang_chajara.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface OptionStateDao {
    @Query("SELECT * FROM optionstate")
    fun getOptionState(): List<OptionState>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOptionState(vararg optionStates: OptionState)
}