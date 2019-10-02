package com.mksoft.maknaeya_sikdang_chajara.utils

import android.content.res.Resources
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
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
            } else if(value =="hidden"){
                view.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
            }else if(value =="full"){
                view.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            }else if(value == "collapsed"){
                view.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
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
@BindingAdapter("mutableVisibility")
fun setMutableVisibility(view: View, visibility: MutableLiveData<Int>?) {
    val parentActivity: AppCompatActivity? = view.getParentActivity()
    if(parentActivity != null && visibility != null) {
        visibility.observe(parentActivity, Observer { value -> view.visibility = value?: View.VISIBLE})
        //MutableLiveData을 쓰는 방법은 옵저브(주인 엑티비티, 변경시 변경된 값 바인딩해주는 옵저버)
        //그래서 현제 뷰의 부모 엑티비티(주인)가 필요
    }
}

@BindingAdapter("mutablePanelHeight")
fun setMutablePanelHeight(view: SlidingUpPanelLayout, panelHeight: MutableLiveData<Int>?){
    val parentActivity:AppCompatActivity? = view.getParentActivity()
    if(parentActivity != null && panelHeight!=null){
        panelHeight.observe(parentActivity, Observer{ value -> view.panelHeight =
            ((value?:0)* Resources.getSystem().displayMetrics.density).toInt()//dp를 인수로 받고 px로 변환
        })
    }
}
@RequiresApi(Build.VERSION_CODES.M)
@BindingAdapter("mutableTextColor")
fun setMutableTextColor(view: TextView, color: MutableLiveData<Int>?){
    val parentActivity:AppCompatActivity? = view.getParentActivity()
    if(parentActivity != null && color!=null){
        color.observe(parentActivity, Observer{ value -> view.setTextColor(App.applicationContext().getColor(value?:R.color.defaultColor))
        })
    }
}