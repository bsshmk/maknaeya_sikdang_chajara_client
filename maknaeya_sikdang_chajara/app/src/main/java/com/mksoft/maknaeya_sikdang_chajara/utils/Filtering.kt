package com.mksoft.maknaeya_sikdang_chajara.utils

import com.mksoft.maknaeya_sikdang_chajara.model.FilterData
import com.mksoft.maknaeya_sikdang_chajara.model.Restaurant
import com.mksoft.maknaeya_sikdang_chajara.model.Review


//class OptionFilter {

    fun Filtering(restaurants: HashMap<String, Restaurant>, reviews: HashMap<String, MutableList<Review>>, filters: FilterData) : MutableList<String>{

        var ret = mutableListOf<String>()

        val range = filters.range
        val keyword = filters.keyword
        val rating = filters.rate!!.toDouble()
        val reviewCount = filters.reviewCount!!.toInt()

        for (elem in restaurants){

            val e = elem.value
            var temp_str = ""

            if(e.distance <= range!!){
                if(e.rating.toDouble() >= rating){
                    if(e.review_count_number.toInt() >= reviewCount){

                        ret.add(elem.key)

                        // keyword 확인 options
                        // 1. String.contains 사용
                        // 2. byte 단위로 kmp 만들어서 사용
                    }
                }
            }

        }


        return ret
    }

//}