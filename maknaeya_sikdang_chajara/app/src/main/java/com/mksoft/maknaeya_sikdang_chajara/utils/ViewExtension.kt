package com.mksoft.maknaeya_sikdang_chajara.utils

import android.content.ContextWrapper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

fun View.getParentActivity(): AppCompatActivity?{
    var context = this.context//이건 무엇을 의미할까? 어디에서의 this일까?

    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext//컴포넌트(엑티비티 같은 친구)가 만들어 질 때 만들어지는 친구를 baseContext라 부르고
        //aplicationContext는 어플이 만들어 질 때 존재하는 하나인 친구를 의미
    }
    return null
}//결론적으로는 activity에서의 context를 찾기위한 과정으로 보인다.