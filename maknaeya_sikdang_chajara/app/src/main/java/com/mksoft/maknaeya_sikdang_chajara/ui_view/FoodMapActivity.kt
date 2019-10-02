package com.mksoft.maknaeya_sikdang_chajara.ui_view

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
import com.mksoft.maknaeya_sikdang_chajara.model.FilterData
import com.mksoft.maknaeya_sikdang_chajara.viewmodel.FoodMapViewModel
import com.mksoft.maknaeya_sikdang_chajara.viewmodel.OptionViewModel
import com.naver.maps.map.MapFragment
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.food_map_activity.*


class FoodMapActivity : AppCompatActivity() {

    private lateinit var fragmentManager: FragmentManager
    lateinit var mapFragment: MapFragment
    private lateinit var foodMapViewModel: FoodMapViewModel
    private lateinit var optionViewModel: OptionViewModel

    private lateinit var binding: FoodMapActivityBinding
    private var reviewLayout: SlidingUpPanelLayout? = null

    private var errorSnackbar: Snackbar? = null


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)//키보드가 화면을 가릴 때
        binding = DataBindingUtil.setContentView(this, R.layout.food_map_activity)

        initMapFragment()
        initSlideLayout()
        initToolbar()
        initViewModel()
        initRangeButtonListener()
        initApplyButton()

    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }//키보드 숨기기
    @SuppressLint("WrongConstant")
    fun initViewModel(){
        foodMapViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(FoodMapViewModel::class.java)
        optionViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(OptionViewModel::class.java)

        binding.foodMapActivityDragLayoutReviewViewRecyclerView.layoutManager = LinearLayoutManager(this,  LinearLayoutManager.VERTICAL, false)
        binding.foodViewModel = foodMapViewModel
        binding.optionViewModel = optionViewModel
        foodMapViewModel.errorMessage.observe(this, Observer {
                errorMessage -> if(errorMessage != null) showError(errorMessage) else hideError()
        })
        foodMapViewModel.refreshMap()//엑티비티가 파괴될 때 그에 맞는 mapFragment에 marker를 다시 표현하도록...
        foodMapViewModel.initScrollView(food_map_activity_sliding_scrollView)
    }
    private fun initToolbar(){
        setSupportActionBar(food_map_activity_Toolbar)
        supportActionBar!!.title = null

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
            foodMapViewModel.halfHiddenSlideView()

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.food_map_activity_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if(item!!.itemId == R.id.food_map_activity_menu_filter_Button){
            foodMapViewModel.visibleOptionSlideView()

            true
        }else{
            super.onOptionsItemSelected(item)
        }

    }
    private fun initApplyButton(){
        food_map_activity_dragOptionLayoutApplyButton_TextView.setOnClickListener {
            var rateString = food_map_activity_dragOptionLayoutRateFilterInput_EditText.text.toString()
            if(rateString.isEmpty()){
                rateString = "0"
            }
            var reviewCount = food_map_activity_dragOptionLayoutReviewCountFilterInput_EditText.text.toString()
            if(reviewCount.isEmpty()){
                reviewCount = "0"
            }
            val currentFilterState = FilterData(
                optionViewModel.getRangeValue(),
                food_map_activity_dragOptionLayoutKeywordFilterInput_EditText.text.toString(),
                rateString.toDouble(),
                reviewCount.toInt()
            )
            if(rateString.toDouble() in 0.0..5.0){
                foodMapViewModel.hiddenSlideView()
                Toast.makeText(this, currentFilterState.toString(), Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "평점은 0~5점 사이로 입력하세요.", Toast.LENGTH_LONG).show()
            }

        }
    }
    private fun initRangeButtonListener(){
        food_map_activity_Range0_TextView.setOnClickListener {
            optionViewModel.clickRangeButton(0)
        }
        food_map_activity_Range1_TextView.setOnClickListener {
            optionViewModel.clickRangeButton(1)
        }
        food_map_activity_Range2_TextView.setOnClickListener {
            optionViewModel.clickRangeButton(2)
        }
        food_map_activity_Range3_TextView.setOnClickListener {
            optionViewModel.clickRangeButton(3)
        }
    }

}
