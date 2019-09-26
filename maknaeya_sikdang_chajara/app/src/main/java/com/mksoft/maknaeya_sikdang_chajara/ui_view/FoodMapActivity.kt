package com.mksoft.maknaeya_sikdang_chajara.ui_view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mksoft.maknaeya_sikdang_chajara.R
import com.mksoft.maknaeya_sikdang_chajara.databinding.FoodMapActivityBinding
import com.mksoft.maknaeya_sikdang_chajara.di.ViewModelFactory
import com.mksoft.maknaeya_sikdang_chajara.viewmodel.FoodMapViewModel
import com.naver.maps.map.MapFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout



class FoodMapActivity : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager
    lateinit var mapFragment: MapFragment
    private lateinit var foodMapViewModel: FoodMapViewModel

    private lateinit var binding: FoodMapActivityBinding

    private var reviewLayout: SlidingUpPanelLayout? = null

    private var errorSnackbar: Snackbar? = null


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(com.mksoft.maknaeya_sikdang_chajara.R.layout.food_map_activity)//setContentView를 2번 선언했기 때문이다.
        binding = DataBindingUtil.setContentView(this, com.mksoft.maknaeya_sikdang_chajara.R.layout.food_map_activity)
        initMapFragment()
        initSlideLayout()
        foodMapViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(FoodMapViewModel::class.java)
        binding.foodMapActivityDragLayoutReviewViewRecyclerView.layoutManager = LinearLayoutManager(this,  LinearLayoutManager.VERTICAL, false)
        binding.viewModel = foodMapViewModel
        foodMapViewModel.errorMessage.observe(this, Observer {
                errorMessage -> if(errorMessage != null) showError(errorMessage) else hideError()
        })
        foodMapViewModel.refreshMap()//엑티비티가 파괴될 때 그에 맞는 mapFragment에 marker를 다시 표현하도록...



    }

    init {
        instance = this
    }

    private fun initSlideLayout() {
        reviewLayout = findViewById(com.mksoft.maknaeya_sikdang_chajara.R.id.food_map_activity_sliding_layout)
        reviewLayout!!.setFadeOnClickListener { reviewLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED }
    }

    private fun initMapFragment() {
        fragmentManager = supportFragmentManager
        mapFragment = fragmentManager.findFragmentById(com.mksoft.maknaeya_sikdang_chajara.R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fragmentManager.beginTransaction().add(com.mksoft.maknaeya_sikdang_chajara.R.id.map, it).commit()
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
        }
    }//슬라이딩한 페이지 숨기기
    private fun showError(@StringRes errorMessage:Int){
        errorSnackbar = Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
        if(errorMessage == R.string.fail_receive){
            errorSnackbar?.setAction(R.string.retry, foodMapViewModel.errorClickListerFailReceive)
        }else if(errorMessage == R.string.deny_permission){
            errorSnackbar?.setAction(R.string.retry, foodMapViewModel.errorClickListerDenyPermission)
        }
        errorSnackbar?.show()
    }

    private fun hideError(){
        errorSnackbar?.dismiss()
    }

}
