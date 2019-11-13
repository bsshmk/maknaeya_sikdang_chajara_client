package com.mksoft.maknaeya_sikdang_chajara.ui_view

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.mksoft.maknaeya_sikdang_chajara.App
import com.mksoft.maknaeya_sikdang_chajara.R
import kotlinx.android.synthetic.main.memory_leak_test_activity.*

class MemoryLeakTestActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.memory_leak_test_activity)
        testStartButton.setOnClickListener {
            val startIntent = Intent(App.applicationContext(), FoodMapActivity::class.java)
            startActivity(startIntent)
        }
    }
}