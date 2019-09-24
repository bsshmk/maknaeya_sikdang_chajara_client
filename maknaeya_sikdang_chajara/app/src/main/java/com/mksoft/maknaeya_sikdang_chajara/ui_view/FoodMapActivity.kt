package com.mksoft.maknaeya_sikdang_chajara.ui_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.mksoft.maknaeya_sikdang_chajara.R
import com.mksoft.maknaeya_sikdang_chajara.di.ViewModelFactory
import com.mksoft.maknaeya_sikdang_chajara.viewmodel.FoodMapViewModel
import com.naver.maps.map.MapFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class FoodMapActivity : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager
    lateinit var mapFragment: MapFragment
    private lateinit var foodMapViewModel: FoodMapViewModel

    private var reviewLayout: SlidingUpPanelLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.food_map_activity)
        initMapFragment()
        initSlideLayout()
        foodMapViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(FoodMapViewModel::class.java)
        foodMapViewModel.refreshMarker()//엑티비티가 파괴될 때 그에 맞는 mapFragment에 marker를 다시 표현하도록...
        return


    }

    init {
        instance = this
    }

    private fun initSlideLayout() {
        reviewLayout = findViewById(R.id.food_map_activity_sliding_layout)
        reviewLayout!!.setFadeOnClickListener { reviewLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED }
    }

    private fun initMapFragment() {
        fragmentManager = supportFragmentManager
        mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fragmentManager.beginTransaction().add(R.id.map, it).commit()
            }


    }

    companion object {
        private var instance: FoodMapActivity? = null
        fun getMapFragment(): MapFragment {
            return instance!!.mapFragment
        }

    }
    override fun onBackPressed() {
        if (reviewLayout != null && (reviewLayout!!.panelState == SlidingUpPanelLayout.PanelState.EXPANDED || reviewLayout!!.panelState == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            reviewLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        } else {
            super.onBackPressed()
        }
    }
    //mapFragment를 전역으로 만들어서 viewModel에서 받을 수 있게
    //viewModel에서 mapFragment에 접근할 때는 mapFragment의 존재를 보장?


}
