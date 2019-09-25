package com.mksoft.maknaeya_sikdang_chajara.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mksoft.maknaeya_sikdang_chajara.App
import com.mksoft.maknaeya_sikdang_chajara.R.*
import com.mksoft.maknaeya_sikdang_chajara.base.BaseViewModel
import com.mksoft.maknaeya_sikdang_chajara.model.Restaurant
import com.mksoft.maknaeya_sikdang_chajara.model.Review
import com.mksoft.maknaeya_sikdang_chajara.ui_view.FoodMapActivity
import com.mksoft.maknaeya_sikdang_chajara.ui_view.ReviewListAdapter
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import io.reactivex.disposables.Disposable


class FoodMapViewModel:BaseViewModel(), OnMapReadyCallback {

    private val currentMarkerRestaurantIdList = mutableListOf<String>()//현제 관리되고 있는 레스토랑 id

    private val restaurantIdAndRestaurant:HashMap<String, Restaurant> = HashMap()
    private val restaurantIdAndMarker:HashMap<String, Marker> = HashMap()
    private val restaurantIdAndReview:HashMap<String, MutableList<Review>> = HashMap()
    private val restaurantIdAndInfoWindow:HashMap<String, InfoWindow> = HashMap()
    val restaurantIdAndSimpleView:HashMap<String, View> = HashMap()//한번 클릭시 보이는 뷰
    val slideViewState: MutableLiveData<String> = MutableLiveData()
    private var currentOpenInfoWindowRestaurantId:String = ""
    private lateinit var subscription: Disposable

    val slideViewRestaurantName:MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantRate:MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantDetailContents:MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantMainMenuPrice:MutableLiveData<String> = MutableLiveData()
    val slideViewRestaurantImageSrc:MutableLiveData<String> = MutableLiveData()
    val reviewListAdapter:ReviewListAdapter = ReviewListAdapter()

    init{
        hiddenSlideView()
        //초기 위치를 서버에 보내고 그 위치에 맞는 음식점 리스트를 받은 다음 테이블 작성을 해주자.
        initRestaurantInfo()
        testStart()
    }
    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }
    private fun testStart(){
        //레스토랑 정보를 긁어와서 맵에 넣어야함
        currentMarkerRestaurantIdList.add("1")

        currentMarkerRestaurantIdList.add("2")
    }
    fun refreshMarker(){
        FoodMapActivity.getMapFragment().getMapAsync(this)
        //엑티비티가 파괴되고 다시 엑티비티를 만들었을 때 싱크를 맞춰주는 용도
    }
    fun clearMarkers(){

        for(id in currentMarkerRestaurantIdList){
            restaurantIdAndMarker[id]!!.map = null
        }
        currentMarkerRestaurantIdList.clear()
    }//마커를 비우고 -> 필터를 해주고 -> 필터 아이디를 IDList에 넣어주자.

    fun initRestaurantInfo(){
        val restaurant1 = Restaurant("1", "맛집1", "www.naver.com","육식", "010-1234_1321"
        ,"4.7", "서울시", "37.5670135", "126.9783740", "https://img.siksinhot.com/place/1463988124958100.png?w=307&h=300&c=Y",
            "삼겹살","삼겹살 160g - 37000원","130")
        restaurantIdAndRestaurant["1"] = restaurant1
        restaurantIdAndReview["1"] = mutableListOf()
        val restaurant1_review1 = Review("r1", "1", "cmk5432","존맛탱","4.5")
        val restaurant1_review2 = Review("r2", "1", "bs1112","개 노맛","1")
        val restaurant1_review3 = Review("r3", "1", "bs1112","개 노맛","1")
        val restaurant1_review4 = Review("r4", "1", "bs1112","개 노맛","1")
        val restaurant1_review5 = Review("r5", "1", "bs1112","개 노맛","1")

        restaurantIdAndReview[restaurant1_review1.restaurant_id]!!.add(restaurant1_review1)
        restaurantIdAndReview[restaurant1_review2.restaurant_id]!!.add(restaurant1_review2)
        restaurantIdAndReview[restaurant1_review3.restaurant_id]!!.add(restaurant1_review3)
        restaurantIdAndReview[restaurant1_review4.restaurant_id]!!.add(restaurant1_review4)
        restaurantIdAndReview[restaurant1_review5.restaurant_id]!!.add(restaurant1_review5)

        val restaurant2 = Restaurant("2", "맛집2", "www.naver.com","육식", "010-1234_1321"
            ,"4.7", "서울시", "37.565725", "126.977906", "https://img.siksinhot.com/place/1463988124958100.png?w=307&h=300&c=Y",
            "삼겹살","삼겹살 160g - 37000원","130")
        restaurantIdAndRestaurant["2"] = restaurant2
        restaurantIdAndReview["2"] = mutableListOf()
        val restaurant2_review1 = Review("r3", "2", "cmk","존맛탱2222","4.5")
        val restaurant2_review2 = Review("r4", "2", "bs112","개 노맛ㅜㅜ","1.5")
        restaurantIdAndReview[restaurant2_review1.restaurant_id]!!.add(restaurant2_review1)
        restaurantIdAndReview[restaurant2_review2.restaurant_id]!!.add(restaurant2_review2)

    }
    private fun initInfoWind(id:String){
        restaurantIdAndInfoWindow[id] = InfoWindow()
        restaurantIdAndInfoWindow[id]!!.onClickListener = infoWindowListener(id)


        restaurantIdAndInfoWindow[id]!!.adapter = object : InfoWindow.DefaultViewAdapter(App.applicationContext()) {

            override fun getContentView(p0: InfoWindow): View {

                return restaurantIdAndSimpleView[id]!!
            }

        }
    }






    private fun makeSimpleInfoView(context:Context, restaurantId:String){
        val simpleInfoView = View.inflate(context, layout.simple_info_window_view, null)
        val simpleInfoTitle = simpleInfoView.findViewById<TextView>(id.simple_info_window_view_restaurantName_TextView)
        val simpleInfoRate = simpleInfoView.findViewById<TextView>(id.simple_info_window_view_restaurantRate_TextView)
        val simpleInfoReviewCountNumber = simpleInfoView.findViewById<TextView>(id.simple_info_window_view_restaurantReviewCount_TextView)
        val simpleInfoMainMenu = simpleInfoView.findViewById<TextView>(id.simple_info_window_view_restaurantMainMenu_TextView)

        simpleInfoTitle.text = restaurantIdAndRestaurant[restaurantId]!!.restaurant_name
        simpleInfoRate.text = restaurantIdAndRestaurant[restaurantId]!!.rating+"점"
        simpleInfoReviewCountNumber.text = "("+restaurantIdAndRestaurant[restaurantId]!!.review_count_number+"개"+")"
        simpleInfoMainMenu.text = restaurantIdAndRestaurant[restaurantId]!!.main_menu


        restaurantIdAndSimpleView[restaurantId] = simpleInfoView
    }
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        for(ID in currentMarkerRestaurantIdList){
            if(restaurantIdAndMarker[ID] == null) {
                makeMarker(ID)
                initInfoWind(ID)
                makeSimpleInfoView(App.applicationContext(), ID)
            }
            restaurantIdAndMarker[ID]!!.map = naverMap

        }
        naverMap.setOnMapClickListener { pointF, latLng ->
            if(currentOpenInfoWindowRestaurantId != ""){
                restaurantIdAndInfoWindow[currentOpenInfoWindowRestaurantId]!!.close()
                currentOpenInfoWindowRestaurantId = ""
                hiddenSlideView()
            }
        }//맵의 다른 부분을 누르면 현재 열려있는 infoWindow창을 close
        //
    }


    private fun makeMarker(restaurantId:String){
        val marker = Marker()
        marker.position = LatLng(restaurantIdAndRestaurant[restaurantId]!!.gps_N.toDouble(), restaurantIdAndRestaurant[restaurantId]!!.gps_E.toDouble())
        marker.onClickListener = makerListener(restaurantId)
        restaurantIdAndMarker[restaurantId] = marker

    }


    private fun infoWindowListener(id:String):Overlay.OnClickListener{
        return Overlay.OnClickListener {
            restaurantIdAndInfoWindow[id]!!.close()//기존에 있는 뷰를 닫아주고 새로운 view를 오픈해줘야한다.
            hiddenSlideView()
            true
        }
    }//뷰에서의 리스터는 소용이 없다... 그래서 infoWindow로 만들어주자

    private fun makerListener(id:String) : Overlay.OnClickListener{
        return Overlay.OnClickListener { overlay ->
            val marker = overlay as Marker

            if (marker.infoWindow == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                if(currentOpenInfoWindowRestaurantId != ""){
                    restaurantIdAndInfoWindow[currentOpenInfoWindowRestaurantId]!!.close()

                }
                visibleSlideView()
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

    fun hiddenSlideView(){
        slideViewState.value = "hidden"

    }
    fun visibleSlideView(){
        slideViewState.value = "visible"

    }
    fun bindingSlideView(restaurantId: String){


        slideViewRestaurantName.value = restaurantIdAndRestaurant[restaurantId]!!.restaurant_name
        slideViewRestaurantRate.value = restaurantIdAndRestaurant[restaurantId]!!.rating
        val detailContents = restaurantIdAndRestaurant[restaurantId]!!.category+"\n"+restaurantIdAndRestaurant[restaurantId]!!.phone_number+"\n"+
                restaurantIdAndRestaurant[restaurantId]!!.location
        slideViewRestaurantDetailContents.value = detailContents
        slideViewRestaurantMainMenuPrice.value = restaurantIdAndRestaurant[restaurantId]!!.main_menu
        slideViewRestaurantImageSrc.value = restaurantIdAndRestaurant[restaurantId]!!.image_src
        reviewListAdapter.updateReviewList(restaurantIdAndReview[restaurantId]!!)
    }
}

