package com.mksoft.maknaeya_sikdang_chajara.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.UiThread
import com.bumptech.glide.Glide
import com.mksoft.maknaeya_sikdang_chajara.App
import com.mksoft.maknaeya_sikdang_chajara.base.BaseViewModel
import com.mksoft.maknaeya_sikdang_chajara.model.RestaurantInfo
import com.mksoft.maknaeya_sikdang_chajara.ui_view.FoodMapActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import io.reactivex.disposables.Disposable


class FoodMapViewModel:BaseViewModel(), OnMapReadyCallback {

    private val currentMarkerIDList = mutableListOf<String>()

    private val idAndRestaurantInfo:HashMap<String, RestaurantInfo> = HashMap()
    private val idAndMarker:HashMap<String, Marker> = HashMap()
    private val idAndInfoWindow:HashMap<String, InfoWindow> = HashMap()
    val idAndView:HashMap<String, View> = HashMap()
    var infoWindowOpenState: Boolean = false
    var currentOpenInfoWindowID:String = ""

    private lateinit var subscription: Disposable

    init{
        initRestaurantInfo()
        testStart()
    }
    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }
    fun testStart(){
        currentMarkerIDList.add("1")
        currentMarkerIDList.add("2")
        currentMarkerIDList.add("3")
        currentMarkerIDList.add("4")
        currentMarkerIDList.add("5")


    }
    fun refreshMarker(){
        FoodMapActivity.getMapFragment().getMapAsync(this)
        //엑티비티가 파괴되고 다시 엑티비티를 만들었을 때 싱크를 맞춰주는 용도
    }
    fun clearMarkers(){

        for(id in currentMarkerIDList){
            idAndMarker[id]!!.map = null
        }
        currentMarkerIDList.clear()
    }//마커를 비우고 -> 필터를 해주고 -> 필터 아이디를 IDList에 넣어주자.

    fun initRestaurantInfo(){
        val restaurant1 = RestaurantInfo(
            "1", 37.5670135, 126.9783740, "맛집1"
            , "https://img.siksinhot.com/place/1463988124958100.png?w=307&h=300&c=Y"
        )
        val restaurant2 = RestaurantInfo(
            "2", 37.565725, 126.977906, "맛집2"
            , "https://img.siksinhot.com/find/1459394303336147.jpg?w=307&h=300&c=Y"
        )
        val restaurant3 = RestaurantInfo(
            "3", 37.567347, 126.978442, "맛집3"
            , "https://img.siksinhot.com/find/1459327836354092.jpg?w=307&h=300&c=Y"
        )


        val restaurant4 = RestaurantInfo(
            "4", 37.565858, 126.974417, "맛집4"
            , "https://img.siksinhot.com/find/1457088921170056.JPG?w=307&h=300&c=Y"
        )
        val restaurant5 = RestaurantInfo(
            "5", 37.566887, 126.975162, "맛집5"
            , "https://img.siksinhot.com/place/1491775541416925.jpg?w=508&h=412&c=Y"
        )
        val restaurant6 = RestaurantInfo(
            "6", 37.567474, 126.976568, "맛집6"
            , "https://img.siksinhot.com/place/1565760567452396.jpg?w=508&h=412&c=Y"
        )

        idAndRestaurantInfo[restaurant1.id] = restaurant1
        idAndRestaurantInfo[restaurant2.id] = restaurant2
        idAndRestaurantInfo[restaurant3.id] = restaurant3
        idAndRestaurantInfo[restaurant4.id] = restaurant4
        idAndRestaurantInfo[restaurant5.id] = restaurant5
        idAndRestaurantInfo[restaurant6.id] = restaurant6

    }
    private fun initInfoWind(id:String){
        idAndInfoWindow[id] = InfoWindow()
        idAndInfoWindow[id]!!.onClickListener = infoWindowListner()
        idAndInfoWindow[id]!!.adapter = object : InfoWindow.DefaultViewAdapter(App.applicationContext()) {
            override fun getContentView(p0: InfoWindow): View {
                return idAndView[id]!!
            }

        }
    }
    private fun makeRestaurantInfoView(context:Context, id:String){
        var foodInfoView = View.inflate(context, com.mksoft.maknaeya_sikdang_chajara.R.layout.test_image, null)
        val foodInfoTitle = foodInfoView.findViewById<TextView>(com.mksoft.maknaeya_sikdang_chajara.R.id.textView)
        val foodInfoImageView = foodInfoView.findViewById<ImageView>(com.mksoft.maknaeya_sikdang_chajara.R.id.food_image)
        foodInfoTitle.setText(idAndRestaurantInfo[id]!!.restaurantName)
        Glide.with(foodInfoView.context).load(idAndRestaurantInfo[id]!!.imageSrc).into(foodInfoImageView)

        idAndView[id] = foodInfoView
    }
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        for(ID in currentMarkerIDList){
            if(idAndMarker[ID] == null) {
                makeMarker(ID)
                initInfoWind(ID)
                makeRestaurantInfoView(App.applicationContext(), ID)
            }
            idAndMarker[ID]!!.map = naverMap

        }
        naverMap.setOnMapClickListener { pointF, latLng ->
            if(infoWindowOpenState){
                infoWindowOpenState = false
                idAndInfoWindow[currentOpenInfoWindowID]!!.close()
                currentOpenInfoWindowID = ""

            }
        }//맵의 다른 부분을 누르면 현재 열려있는 infoWindow창을 close
        //
    }


    fun makeMarker(id:String){
        val marker = Marker()
        marker.position = LatLng(idAndRestaurantInfo[id]!!.latitude, idAndRestaurantInfo[id]!!.longitude)
        marker.onClickListener = makerListner(id)
        idAndMarker[id] = marker

    }


    fun infoWindowListner():Overlay.OnClickListener{
        val listener = Overlay.OnClickListener { overlay ->
            Log.d("test","test11112221212")
            true
        }
        return listener
    }//뷰에서의 리스터는 소용이 없다... 그래서 infoWindow로 만들어주자

    fun makerListner(id:String) : Overlay.OnClickListener{
        val listener = Overlay.OnClickListener { overlay ->
            val marker = overlay as Marker

            if (marker.infoWindow == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                if(!infoWindowOpenState){
                    infoWindowOpenState = true
                }else{
                    idAndInfoWindow[currentOpenInfoWindowID]!!.close()
                }

                idAndInfoWindow[id]!!.open(idAndMarker[id]!!)
                currentOpenInfoWindowID = id
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                idAndInfoWindow[id]!!.close()

                //이거 호출하면 맵이 리프래쉬됨
                //필터를 통하여 남은 음식적으로 리스트로 만들어서 관리
                //원본 음식점 배열은 유지

            }



            true
        }
        return listener
    }


}

