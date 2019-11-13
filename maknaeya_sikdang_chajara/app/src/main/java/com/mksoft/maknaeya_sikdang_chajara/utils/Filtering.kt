package com.mksoft.maknaeya_sikdang_chajara.utils

import androidx.collection.ArrayMap
import com.mksoft.maknaeya_sikdang_chajara.model.FilterData
import com.mksoft.maknaeya_sikdang_chajara.model.Restaurant
import com.mksoft.maknaeya_sikdang_chajara.model.Review


//class OptionFilter {

fun Filtering(
    restaurants: ArrayMap<String, Restaurant>,
    reviews: ArrayMap<String, MutableList<Review>>,
    filters: FilterData
): MutableList<String> {

    var ret = mutableListOf<String>()

    val range = filters.range
    val keyword = filters.keyword.toString()
    val rating = filters.rate!!.toDouble()
    val reviewCount = filters.reviewCount!!.toInt()

    for (elem in restaurants) {

        val e = elem.value

        if (e.distance <= range!!) {
            if (e.rating.toDouble() >= rating) {
                if (e.review_count_number.toInt() >= reviewCount) {

                    var dummyStr = ""
                    if (keyword.length > 0) {
                        if (reviews[elem.key] != null) {

                            for (rev in reviews[elem.key].orEmpty()) {
                                dummyStr += rev
                            }
                            if (dummyStr.contains(keyword)) {
                                ret.add(elem.key)
                            }
                        }
                    } else {
                        ret.add(elem.key)
                    }

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