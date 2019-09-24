package com.mksoft.maknaeya_sikdang_chajara.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.UiThread
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
    val restaurantIdAndDetailView:HashMap<String, View> = HashMap()
    //var infoWindowOpenState: Boolean = false
    var currentOpenInfoWindowRestaurantId:String = ""
    var currentOpenInfoWindowViewState:Int = 0//뷰의 상태가 심플인지 디테일인지
    private lateinit var subscription: Disposable



    init{
        currentOpenInfoWindowViewState = string.view_state_simple
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
        restaurantIdAndReview[restaurant1_review1.restaurant_id]!!.add(restaurant1_review1)
        restaurantIdAndReview[restaurant1_review2.restaurant_id]!!.add(restaurant1_review2)

        val restaurant2 = Restaurant("2", "맛집1", "www.naver.com","육식", "010-1234_1321"
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

                if(currentOpenInfoWindowViewState == string.view_state_detail) {
                    return restaurantIdAndDetailView[id]!!
                }
                return restaurantIdAndSimpleView[id]!!
            }

        }
    }



    @SuppressLint("SetTextI18n")
    private fun makeDetailInfoView(context:Context, restaurantId:String){
        val detailInfoView = View.inflate(context, layout.detail_info_window_view, null)
        val detailInfoTiTle = detailInfoView.findViewById<TextView>(id.food_map_activity_dragLayoutRestaurantName_TextView)
        val detailInfoImage = detailInfoView.findViewById<ImageView>(id.food_map_activity_dragLayoutFoodImage_ImageView)
        val detailInfoRate = detailInfoView.findViewById<TextView>(id.food_map_activity_dragLayoutRestaurantRate_TextView)
        val detailInfoContents = detailInfoView.findViewById<TextView>(id.food_map_activity_dragLayoutDetailContents_TextView)
        val detailInfoMainMenuPrices = detailInfoView.findViewById<TextView>(id.food_map_activity_dragLayoutMainMenuPrices_TextView)
        val detailInfoReviewViewRecyclerView = detailInfoView.findViewById<RecyclerView>(id.food_map_activity_dragLayoutReviewView_RecyclerView)



        detailInfoTiTle.text = restaurantIdAndRestaurant[restaurantId]!!.restaurant_name
        detailInfoRate.text = restaurantIdAndRestaurant[restaurantId]!!.rating+"점"
        detailInfoContents.text = restaurantIdAndRestaurant[restaurantId]!!.category+"\n"
        detailInfoContents.append(restaurantIdAndRestaurant[restaurantId]!!.phone_number+"\n")
        detailInfoContents.append(restaurantIdAndRestaurant[restaurantId]!!.location)
        detailInfoMainMenuPrices.text = restaurantIdAndRestaurant[restaurantId]!!.main_menu_price
        Glide.with(detailInfoView.context).load(restaurantIdAndRestaurant[restaurantId]!!.image_src).into(detailInfoImage)
        //리뷰 어뎁터 만들기
        val thisReviewListAdapter = ReviewListAdapter()
        detailInfoReviewViewRecyclerView.adapter = thisReviewListAdapter
        detailInfoReviewViewRecyclerView.layoutManager = LinearLayoutManager(detailInfoView.context)
        thisReviewListAdapter.updateReviewList(restaurantIdAndReview[restaurantId]!!)
        restaurantIdAndDetailView[restaurantId] = detailInfoView
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
                makeDetailInfoView(App.applicationContext(), ID)
            }
            restaurantIdAndMarker[ID]!!.map = naverMap

        }
        naverMap.setOnMapClickListener { pointF, latLng ->
            if(currentOpenInfoWindowRestaurantId != ""){
                restaurantIdAndInfoWindow[currentOpenInfoWindowRestaurantId]!!.close()
                currentOpenInfoWindowRestaurantId = ""
                currentOpenInfoWindowViewState = string.view_state_simple

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
            if(currentOpenInfoWindowViewState == string.view_state_simple){
                currentOpenInfoWindowViewState = string.view_state_detail
                restaurantIdAndInfoWindow[id]!!.close()
                restaurantIdAndInfoWindow[id]!!.open(restaurantIdAndMarker[id]!!)


            }else if(currentOpenInfoWindowViewState == string.view_state_detail){
                currentOpenInfoWindowViewState = string.view_state_simple
                restaurantIdAndInfoWindow[id]!!.close()//기존에 있는 뷰를 닫아주고 새로운 view를 오픈해줘야한다.
                restaurantIdAndInfoWindow[id]!!.open(restaurantIdAndMarker[id]!!)

            }

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
                currentOpenInfoWindowViewState = string.view_state_simple
                restaurantIdAndInfoWindow[id]!!.open(restaurantIdAndMarker[id]!!)
                currentOpenInfoWindowRestaurantId = id
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                currentOpenInfoWindowViewState = string.view_state_simple
                restaurantIdAndInfoWindow[id]!!.close()
                currentOpenInfoWindowRestaurantId = ""
                //이거 호출하면 맵이 리프래쉬됨
                //필터를 통하여 남은 음식적으로 리스트로 만들어서 관리
                //원본 음식점 배열은 유지

            }


            true
        }
    }


}

