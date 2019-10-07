package com.mksoft.maknaeya_sikdang_chajara.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import com.mksoft.maknaeya_sikdang_chajara.App
import com.mksoft.maknaeya_sikdang_chajara.R.*
import com.mksoft.maknaeya_sikdang_chajara.api.FoodMapAPI
import com.mksoft.maknaeya_sikdang_chajara.base.BaseViewModel
import com.mksoft.maknaeya_sikdang_chajara.model.Review
import com.mksoft.maknaeya_sikdang_chajara.ui_view.FoodMapActivity
import com.mksoft.maknaeya_sikdang_chajara.ui_view.ReviewListAdapter
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Overlay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import com.tedpark.tedpermission.rx2.TedRx2Permission
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.mksoft.maknaeya_sikdang_chajara.model.Restaurant
import com.naver.maps.map.CameraUpdate


class FoodMapViewModel : BaseViewModel(), OnMapReadyCallback {

    @Inject
    lateinit var foodMapAPI: FoodMapAPI


    private val currentMarkerRestaurantIdList = mutableListOf<String>()//현제 관리되고 있는 레스토랑 id

    private val restaurantIdAndRestaurant: HashMap<String, Restaurant> = HashMap()
    private val restaurantIdAndMarker: HashMap<String, Marker> = HashMap()
    private val restaurantIdAndReview: HashMap<String, MutableList<Review>> = HashMap()
    private val restaurantIdAndInfoWindow: HashMap<String, InfoWindow> = HashMap()
    val restaurantIdAndSimpleView: HashMap<String, View> = HashMap()//한번 클릭시 보이는 뷰
    val slideViewState: MutableLiveData<String> = MutableLiveData()
    private var currentOpenInfoWindowRestaurantId: String = ""
    private lateinit var subscription: Disposable



    val reviewVisible:MutableLiveData<Int> = MutableLiveData()
    val optionViewVisible:MutableLiveData<Int> = MutableLiveData()
    val slideViewHeight:MutableLiveData<Int> = MutableLiveData()

    val slideViewRestaurantName: MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantRate: MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantDetailContents: MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantMainMenuPrice: MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantImageSrc: MutableLiveData<String> = MutableLiveData()
    val reviewListAdapter: ReviewListAdapter = ReviewListAdapter()
    val errorMessage: MutableLiveData<Int> = MutableLiveData()
    val errorClickListerFailReceive = View.OnClickListener { getRestaurant(location!!) }
    val errorClickListerDenyPermission = View.OnClickListener { checkPermission() }
    lateinit var locationManager: LocationManager
    private var location: Location? = null
    var scrollView:ScrollView? = null
    init {
        checkPermission()
        hiddenSlideView()
        

    }

    private fun getReviewList(restaurantId: String){
        subscription = foodMapAPI.getReview(restaurantId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { reviewList ->
                    restaurantIdAndReview[restaurantId] = reviewList

                },
                {  }
            )
    }
    private fun getRestaurant(location:Location) {
        subscription = foodMapAPI.getRestaurant(location!!.latitude, location!!.longitude, 1.0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { restaurantList ->
                    for (item in restaurantList) {
                        restaurantIdAndRestaurant[item.restaurant_id] = item
                        getReviewList(item.restaurant_id)
                        currentMarkerRestaurantIdList.add(item.restaurant_id)
                    }
                    refreshMap()

                },
                { failLoadRestaurant() }
            )
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    fun refreshMap() {
        FoodMapActivity.getMapFragment().getMapAsync(this)
        //엑티비티가 파괴되고 다시 엑티비티를 만들었을 때 싱크를 맞춰주는 용도
    }

    private fun initInfoWind(id: String) {
        restaurantIdAndInfoWindow[id] = InfoWindow()
        restaurantIdAndInfoWindow[id]!!.onClickListener = infoWindowListener()


        restaurantIdAndInfoWindow[id]!!.adapter = object : InfoWindow.DefaultViewAdapter(App.applicationContext()) {

            override fun getContentView(p0: InfoWindow): View {

                return restaurantIdAndSimpleView[id]!!
            }

        }
    }


    private fun makeSimpleInfoView(context: Context, restaurantId: String) {
        val simpleInfoView = View.inflate(context, layout.simple_info_window_view, null)
        val simpleInfoTitle = simpleInfoView.findViewById<TextView>(id.simple_info_window_view_restaurantName_TextView)
        val simpleInfoRate = simpleInfoView.findViewById<TextView>(id.simple_info_window_view_restaurantRate_TextView)
        val simpleInfoReviewCountNumber =
            simpleInfoView.findViewById<TextView>(id.simple_info_window_view_restaurantReviewCount_TextView)
        val simpleInfoMainMenu =
            simpleInfoView.findViewById<TextView>(id.simple_info_window_view_restaurantMainMenu_TextView)

        simpleInfoTitle.text = restaurantIdAndRestaurant[restaurantId]!!.restaurant_name
        simpleInfoRate.text = restaurantIdAndRestaurant[restaurantId]!!.rating + "점"
        simpleInfoReviewCountNumber.text =
            "(" + restaurantIdAndRestaurant[restaurantId]!!.review_count_number + "개" + ")"

        simpleInfoMainMenu.text = restaurantIdAndRestaurant[restaurantId]!!.category


        restaurantIdAndSimpleView[restaurantId] = simpleInfoView
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        val locationOverlay = naverMap.locationOverlay
        if(location != null){
            locationOverlay.position = LatLng(location!!.latitude, location!!.longitude)
            val cameraUpdate = CameraUpdate.scrollTo(LatLng(location!!.latitude, location!!.longitude))
            naverMap.moveCamera(cameraUpdate)
            locationOverlay.isVisible = true

        }

        for (ID in currentMarkerRestaurantIdList) {
            if (restaurantIdAndMarker[ID] == null) {
                makeMarker(ID)
                initInfoWind(ID)
                makeSimpleInfoView(App.applicationContext(), ID)
            }
            restaurantIdAndMarker[ID]!!.map = naverMap

        }
        naverMap.setOnMapClickListener { pointF, latLng ->
            if (currentOpenInfoWindowRestaurantId != "") {
                restaurantIdAndInfoWindow[currentOpenInfoWindowRestaurantId]!!.close()
                currentOpenInfoWindowRestaurantId = ""
                hiddenSlideView()
            }
        }//맵의 다른 부분을 누르면 현재 열려있는 infoWindow창을 close
        //
    }


    private fun makeMarker(restaurantId: String) {
        val marker = Marker()
        marker.position = LatLng(
            restaurantIdAndRestaurant[restaurantId]!!.gps_N.toDouble(),
            restaurantIdAndRestaurant[restaurantId]!!.gps_E.toDouble()
        )
        marker.onClickListener = makerListener(restaurantId)
        restaurantIdAndMarker[restaurantId] = marker

    }


    private fun infoWindowListener(): Overlay.OnClickListener {
        return Overlay.OnClickListener {
            fullVisibleReviewSlideView()
            true
        }
    }//뷰에서의 리스터는 소용이 없다... 그래서 infoWindow로 만들어주자

    private fun makerListener(id: String): Overlay.OnClickListener {
        return Overlay.OnClickListener { overlay ->
            val marker = overlay as Marker

            if (marker.infoWindow == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                if (currentOpenInfoWindowRestaurantId != "") {
                    restaurantIdAndInfoWindow[currentOpenInfoWindowRestaurantId]!!.close()

                }
                visibleReviewSlideView()
                restaurantIdAndInfoWindow[id]!!.open(restaurantIdAndMarker[id]!!)
                currentOpenInfoWindowRestaurantId = id
                bindingSlideView(id)
                //여기에 슬라이드 뷰 바인딩을..
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                restaurantIdAndInfoWindow[id]!!.close()
                currentOpenInfoWindowRestaurantId = ""
                hiddenSlideView()
                //이거 호출하면 맵이 리프래쉬됨
                //필터를 통하여 남은 음식적으로 리스트로 만들어서 관리
                //원본 음식점 배열은 유지

            }


            true
        }
    }

    fun hiddenSlideView() {
        slideViewState.value = "hidden"

    }

    fun visibleReviewSlideView() {
        reviewVisible.value = View.VISIBLE
        optionViewVisible.value = View.GONE
        scrollView!!.isFocusableInTouchMode = true
        scrollView!!.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        slideViewState.value = "visible"
        slideViewHeight.value = 58

    }
    fun visibleOptionSlideView(){
        reviewVisible.value = View.GONE
        optionViewVisible.value = View.VISIBLE
        scrollView!!.isFocusableInTouchMode = true
        scrollView!!.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        slideViewState.value = "full"
        slideViewHeight.value = 0

    }
    fun fullVisibleReviewSlideView() {
        reviewVisible.value = View.VISIBLE
        optionViewVisible.value = View.GONE
        scrollView!!.isFocusableInTouchMode = true
        scrollView!!.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        slideViewState.value = "full"
        slideViewHeight.value = 58

    }

    fun halfHiddenSlideView(){
        scrollView!!.fullScroll(ScrollView.FOCUS_UP)
        scrollView!!.isFocusableInTouchMode = true
        scrollView!!.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        slideViewState.value = "collapsed"

    }
    private fun failLoadRestaurant() {
        errorMessage.value = com.mksoft.maknaeya_sikdang_chajara.R.string.fail_receive
    }

    private fun checkPermission(){
        TedRx2Permission.with(App.applicationContext())
            .setRationaleTitle("권한 요청")
            .setRationaleMessage("위치 권한이 필요합니다.") // "we need permission for read contact and find your location"
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .request()
            .subscribe({ tedPermissionResult ->
                if (tedPermissionResult.isGranted) {
                    initLocationAndCallApi()
                } else {
                    denyPermission()
                }
            },
                {
                    initLocationAndCallApi()
                })//이미 권한이 허가가 되어 있을 때 여기로 넘어온다.
    }
    @SuppressLint("MissingPermission")
    private fun initLocationAndCallApi(){
        locationManager = App.applicationContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        refreshMap()//권한을 받고 위치 갱신
        getRestaurant(location!!)
    }
    private fun denyPermission(){
        errorMessage.value = string.deny_permission
    }
    private fun bindingSlideView(restaurantId: String) {


        slideViewRestaurantName.value = restaurantIdAndRestaurant[restaurantId]!!.restaurant_name
        slideViewRestaurantRate.value = restaurantIdAndRestaurant[restaurantId]!!.rating
        val detailContents =
            restaurantIdAndRestaurant[restaurantId]!!.category + "\n" + restaurantIdAndRestaurant[restaurantId]!!.phone_number + "\n" +
                    restaurantIdAndRestaurant[restaurantId]!!.location
        slideViewRestaurantDetailContents.value = detailContents
        slideViewRestaurantMainMenuPrice.value = restaurantIdAndRestaurant[restaurantId]!!.main_menu
        slideViewRestaurantImageSrc.value = restaurantIdAndRestaurant[restaurantId]!!.image_src
        if (restaurantIdAndReview[restaurantId] != null)
            reviewListAdapter.updateReviewList(restaurantIdAndReview[restaurantId]!!)
    }
    fun initScrollView(scrollView:ScrollView){
        this.scrollView = scrollView
    }
}

