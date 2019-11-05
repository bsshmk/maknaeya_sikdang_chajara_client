package com.mksoft.maknaeya_sikdang_chajara.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mksoft.maknaeya_sikdang_chajara.base.BaseViewModel
import com.mksoft.maknaeya_sikdang_chajara.model.Review

class ReviewViewModel : BaseViewModel() {
    private val nameAndRate: MutableLiveData<String> = MutableLiveData()
    private val contents: MutableLiveData<String> = MutableLiveData()
    fun bind(review: Review) {
        nameAndRate.value = review.writer_id + " / " + review.review_score
        contents.value = review.review_contents
    }

    fun getNameAndRate(): MutableLiveData<String> {
        return nameAndRate
    }

    fun getContents(): MutableLiveData<String> {
        return contents
    }
}//review List item 바인딩