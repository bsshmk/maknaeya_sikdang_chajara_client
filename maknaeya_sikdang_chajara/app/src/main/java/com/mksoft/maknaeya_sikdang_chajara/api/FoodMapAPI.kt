package com.mksoft.maknaeya_sikdang_chajara.api

import com.mksoft.maknaeya_sikdang_chajara.model.Restaurant
import com.mksoft.maknaeya_sikdang_chajara.model.Review
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import com.squareup.moshi.JsonQualifier



interface FoodMapAPI {

    @GET("/restaurant/distByLoc")
    fun getRestaurant(
        @Query("gps_N") gps_N: Double,
        @Query("gps_E") gps_E: Double,
        @Query("range") range: Double
    ): Observable<List<Restaurant>>

    @GET("/restaurant/reviewById")
    fun getReview(
        @Query("id") id: String
    ): Observable<MutableList<Review>>

}