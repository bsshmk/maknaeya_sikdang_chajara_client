package com.mksoft.maknaeya_sikdang_chajara.utils

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mksoft.maknaeya_sikdang_chajara.App
import com.mksoft.maknaeya_sikdang_chajara.R
import com.sothree.slidinguppanel.SlidingUpPanelLayout

@BindingAdapter("adapter")
fun setAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    view.adapter = adapter
}


@BindingAdapter("mutableImage")
fun setImage(view: ImageView, imageSrc:MutableLiveData<String>?){
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && imageSrc != null) {
        imageSrc.observe(parentActivity, Observer { value ->
            Glide.with(App.applicationContext()).load(value).into(view)
        })
    }
}


@BindingAdapter("mutableSlideViewState")
fun setMutableSlideViewState(view: SlidingUpPanelLayout, state: MutableLiveData<String>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && state != null) {
        state.observe(parentActivity, Observer { value ->
            if (value == "visible") {
                view.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            } else {
                view.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
            }
        })
    }
}

@BindingAdapter("mutableText")
fun setMutableText(view: TextView, text: MutableLiveData<String>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if (parentActivity != null && text != null) {
        text.observe(parentActivity, Observer { value -> view.text = value ?: "" })
    }
}


