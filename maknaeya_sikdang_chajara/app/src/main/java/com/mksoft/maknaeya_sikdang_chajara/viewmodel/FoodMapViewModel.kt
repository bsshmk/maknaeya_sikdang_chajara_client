package com.mksoft.maknaeya_sikdang_chajara.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import com.mksoft.maknaeya_sikdang_chajara.App
import com.mksoft.maknaeya_sikdang_chajara.R
import com.mksoft.maknaeya_sikdang_chajara.R.*
import com.mksoft.maknaeya_sikdang_chajara.api.FoodMapAPI
import com.mksoft.maknaeya_sikdang_chajara.base.BaseViewModel
import com.mksoft.maknaeya_sikdang_chajara.model.FilterData
import com.mksoft.maknaeya_sikdang_chajara.model.Review
import com.mksoft.maknaeya_sikdang_chajara.ui_view.FoodMapActivity
import com.mksoft.maknaeya_sikdang_chajara.ui_view.ReviewListAdapter
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import com.tedpark.tedpermission.rx2.TedRx2Permission
import com.mksoft.maknaeya_sikdang_chajara.model.Restaurant
import com.mksoft.maknaeya_sikdang_chajara.utils.Filtering
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.*
import java.util.*
import kotlin.collections.HashMap


class FoodMapViewModel : BaseViewModel(), OnMapReadyCallback {

    @Inject
    lateinit var foodMapAPI: FoodMapAPI


    private var currentMarkerRestaurantIdList = mutableListOf<String>()//현재 관리되고 있는 레스토랑 id
    private var preMarkerRestaurantIdList = mutableListOf<String>()
    private val totalRestaurantIdList = mutableListOf<String>()

    private val restaurantIdAndRestaurant: HashMap<String, Restaurant> = HashMap()
    private val restaurantIdAndMarker: HashMap<String, Marker> = HashMap()
    private val restaurantIdAndReview: HashMap<String, MutableList<Review>> = HashMap()
    private val restaurantIdAndInfoWindow: HashMap<String, InfoWindow> = HashMap()
    val restaurantIdAndSimpleView: HashMap<String, View> = HashMap()//한번 클릭시 보이는 뷰

    private var currentOpenInfoWindowRestaurantId: String = ""
    private lateinit var subscription: Disposable

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val refreshButtonVisibility: MutableLiveData<Int> = MutableLiveData()
    val slideViewState: MutableLiveData<String> = MutableLiveData()


    val reviewVisible: MutableLiveData<Int> = MutableLiveData()
    val optionViewVisible: MutableLiveData<Int> = MutableLiveData()
    val slideViewHeight: MutableLiveData<Int> = MutableLiveData()

    val slideViewRestaurantName: MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantRate: MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantDetailContents: MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantMainMenuPrice: MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantImageSrc: MutableLiveData<String> = MutableLiveData()
    val reviewListAdapter: ReviewListAdapter = ReviewListAdapter()
    val errorMessage: MutableLiveData<Int> = MutableLiveData()
    val errorClickListerFailReceive = View.OnClickListener { initLocationAndCallApi() }
    val errorClickListerDenyPermission = View.OnClickListener { checkPermission() }
    lateinit var locationManager: LocationManager
    private var location: Location? = null
    var scrollView: ScrollView? = null


    private val shortestPath = PathOverlay()
    var currentNaverMap: NaverMap ?= null


    override fun onCleared() {
        super.onCleared()
        subscription.dispose()

    }//viewModel이 끝날 때 돌고 있는 subscription 종료

    init {
        shortestPath.color = Color.YELLOW
        terminateLoadRefresh()
        checkPermission()
        hiddenSlideView()


    }
    //activity 초기상태 설정
    private fun getReviewList(restaurantId: String) {
        subscription = foodMapAPI.getReview(restaurantId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { reviewList ->
                    restaurantIdAndReview[restaurantId] = reviewList

                },
                { err -> Log.d("getReviewList", err.toString()) }
            )
    }//서버로부터 restaurantId을 통하여 음식점 리스트 반환 후 hashMap에 저장

    private fun getRestaurant(location: Location) {
        subscription = foodMapAPI.getRestaurant(location!!.latitude, location!!.longitude, 2.0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { loadRefresh() }
            .doOnTerminate { terminateLoadRefresh() }

            .subscribe(
                { restaurantList ->
                    for (item in restaurantList) {
                        restaurantIdAndRestaurant[item.restaurant_id] = item
                        getReviewList(item.restaurant_id)
                        totalRestaurantIdList.add(item.restaurant_id)
                    }

                },
                { err ->
                    Log.d("getRestaurant", err.toString())
                    failLoadRestaurant()
                }
            )
    }
    //서버로 경도 위도 범위 요청시에 그 주변 레스토랑 정보 받기 후 hashMap에 ID별로 restaurant정보 저장

    fun refreshMap() {
        FoodMapActivity.getMapFragment().getMapAsync(this)
    }
    //맵의 싱크를 맞춰주는 함수

    private fun initInfoWind(id: String) {
        restaurantIdAndInfoWindow[id] = InfoWindow()
        restaurantIdAndInfoWindow[id]!!.onClickListener = infoWindowListener()


        restaurantIdAndInfoWindow[id]!!.adapter =
            object : InfoWindow.DefaultViewAdapter(App.applicationContext()) {

                override fun getContentView(p0: InfoWindow): View {

                    return restaurantIdAndSimpleView[id]!!
                }

            }
    }
    //지도에서 marker 클릭시 info window에 simple view(간단한 음식점 정보) binding


    private fun makeSimpleInfoView(context: Context, restaurantId: String) {
        val simpleInfoView = View.inflate(context, layout.simple_info_window_view, null)
        val simpleInfoTitle =
            simpleInfoView.findViewById<TextView>(id.simple_info_window_view_restaurantName_TextView)
        val simpleInfoRate =
            simpleInfoView.findViewById<TextView>(id.simple_info_window_view_restaurantRate_TextView)
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
    //restaurantID 별로 simpleView를 만들어서 hashMap에 저장

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        naverMap.mapType = NaverMap.MapType.Navi

        for (ID in preMarkerRestaurantIdList) {

            restaurantIdAndMarker[ID]!!.map = null

        }//전 상태의 marker들을 모두 닫고
        preMarkerRestaurantIdList.clear()
        val locationOverlay = naverMap.locationOverlay
        if (location != null) {
            locationOverlay.position = LatLng(location!!.latitude, location!!.longitude)
            val cameraUpdate =
                CameraUpdate.scrollTo(LatLng(location!!.latitude, location!!.longitude))
            naverMap.moveCamera(cameraUpdate)
            locationOverlay.isVisible = true

        }//현재 위치로 맵을 이동하고

        for (ID in currentMarkerRestaurantIdList) {
            if (restaurantIdAndMarker[ID] == null) {
                makeMarker(ID)
                initInfoWind(ID)
                makeSimpleInfoView(App.applicationContext(), ID)
            }
            restaurantIdAndMarker[ID]!!.map = naverMap

        }//필터로 걸러진 현재 음식점 marker를 오픈하고
        naverMap.setOnMapClickListener { pointF, latLng ->
            if (currentOpenInfoWindowRestaurantId != "") {
                restaurantIdAndInfoWindow[currentOpenInfoWindowRestaurantId]!!.close()
                currentOpenInfoWindowRestaurantId = ""
                hiddenSlideView()
                shortestPath.map = null//경로 숨기기
            }
        }//맵의 다른 부분을 누르면 현재 열려있는 infoWindow창을 close
        //
        currentNaverMap = naverMap//최단 경로를 그리기 위하여 전역에 저장
    }


    private fun makeMarker(restaurantId: String) {
        val marker = Marker()
        marker.position = LatLng(
            restaurantIdAndRestaurant[restaurantId]!!.gps_N.toDouble(),
            restaurantIdAndRestaurant[restaurantId]!!.gps_E.toDouble()
        )
        marker.icon = OverlayImage.fromResource(R.drawable.dish)
        marker.width = 70
        marker.height = 70
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
                bindingSlideViewToRestaurantInfo(id)
                //여기에 슬라이드 뷰 바인딩을..

                testPath()//경로 요청하고 경로 갱신
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                restaurantIdAndInfoWindow[id]!!.close()
                currentOpenInfoWindowRestaurantId = ""
                shortestPath.map = null
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
    //slide view 하나로 option view와 detail info view 관리
    fun visibleReviewSlideView() {
        reviewVisible.value = View.VISIBLE
        optionViewVisible.value = View.GONE
        scrollView!!.isFocusableInTouchMode = true
        scrollView!!.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        slideViewState.value = "visible"
        slideViewHeight.value = 58

    }//review view(small)로 slide view를 갱신

    fun visibleOptionSlideView() {
        reviewVisible.value = View.GONE
        optionViewVisible.value = View.VISIBLE
        scrollView!!.isFocusableInTouchMode = true
        scrollView!!.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        slideViewState.value = "full"
        slideViewHeight.value = 0

    }//option view로 slide view를 갱신

    fun fullVisibleReviewSlideView() {
        reviewVisible.value = View.VISIBLE
        optionViewVisible.value = View.GONE
        scrollView!!.isFocusableInTouchMode = true
        scrollView!!.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        slideViewState.value = "full"
        slideViewHeight.value = 58

    }//review view(large)로 slide view를 갱신

    fun halfHiddenSlideView() {
        scrollView!!.fullScroll(ScrollView.FOCUS_UP)
        scrollView!!.isFocusableInTouchMode = true
        scrollView!!.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS
        slideViewState.value = "collapsed"

    }//slide view 포커스를 위쪽으로 옮기고 숨기기

    private fun failLoadRestaurant() {
        errorMessage.value = com.mksoft.maknaeya_sikdang_chajara.R.string.fail_receive
    }//load fail함수

    private fun checkPermission() {
        subscription = TedRx2Permission.with(App.applicationContext())
            .setRationaleTitle("권한 요청")
            .setRationaleMessage("위치 권한이 필요합니다.") // "we need permission for read contact and find your location"
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .request()
            .subscribe({ tedPermissionResult ->
                if (tedPermissionResult.isGranted) {
                    initLocationAndCallApi()
                } else {
                    denyPermission()
                }
            },
                { err ->
                    Log.d("checkPermission", err.toString())
                    initLocationAndCallApi()
                })//이미 권한이 허가가 되어 있을 때 여기로 넘어온다.
    }//권한 확인

    @SuppressLint("MissingPermission")
    fun initLocationAndCallApi() {
        locationManager =
            App.applicationContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        refreshMap()//권한을 받고 위치 갱신
        getRestaurant(location!!)
    }//위치 반환

    private fun denyPermission() {
        errorMessage.value = string.deny_permission
    }//권한 거부 상태

    private fun bindingSlideViewToRestaurantInfo(restaurantId: String) {


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
    }//slide view에 레스토랑 정보로 바인딩

    fun initScrollView(scrollView: ScrollView) {
        this.scrollView = scrollView
    }//스크롤뷰 포커스를 탑으로 옮기기 위하여

    private fun terminateLoadRefresh() {
        loadingVisibility.value = View.GONE
        refreshButtonVisibility.value = View.VISIBLE
    }//로딩이 끝났을 때 리프래시 버튼 상태

    private fun loadRefresh() {
        loadingVisibility.value = View.VISIBLE
        refreshButtonVisibility.value = View.GONE
    }//로딩할 시 리프래시 버튼 상태

    fun restaurantFilter(filterData: FilterData) {
        preMarkerRestaurantIdList = currentMarkerRestaurantIdList
        currentMarkerRestaurantIdList =
            Filtering(restaurantIdAndRestaurant, restaurantIdAndReview, filterData)
        refreshMap()

    }//레스토랑 필터 함수

    private fun setShortestPaht(latLngList:List<LatLng>){
        shortestPath.coords = latLngList
        shortestPath.map = currentNaverMap
    }
    private fun testPath(){
        val latLngList = mutableListOf<LatLng>()
        val temp1 = LatLng(37.452194, 126.653080)
        val temp2 = LatLng(37.451964, 126.653735)
        val temp3 = LatLng(37.451768, 126.654400)
        val temp4 = LatLng(37.451589, 126.655162)
        val temp5 = LatLng(37.451427, 126.655805)
        latLngList.add(temp1)
        latLngList.add(temp2)
        latLngList.add(temp3)
        latLngList.add(temp4)
        latLngList.add(temp5)
        setShortestPaht(latLngList)



    }//테스트로 경로가 그려지는지 확인
}

