package com.mksoft.maknaeya_sikdang_chajara.api

import com.mksoft.maknaeya_sikdang_chajara.model.Restaurant
import io.reactivex.Observable
import retrofit2.http.GET

interface FoodMapAPI {
    @GET("/restaurant/name")
    fun testGetRestaurant()
    : Observable<List<Restaurant>>

}