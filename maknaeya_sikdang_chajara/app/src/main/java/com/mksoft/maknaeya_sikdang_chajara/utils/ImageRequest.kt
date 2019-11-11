package com.mksoft.maknaeya_sikdang_chajara.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import android.widget.ImageView
import androidx.core.util.lruCache
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.Volley
import com.mksoft.maknaeya_sikdang_chajara.App

object ImageRequest {//싱글톤으로 적용하기 위하여 class 말고 object로 한듯
    private val requestQueue : RequestQueue
    //요청별 우선 순위 : 목록조회와 이미지 다운로드를 할때 목록조회가 우선순위가 높게 설정. 다음 페이지의 목록조회를 요청하면 이전페이지의 이미지로딩이 끝나지 않아도 기다리지 않고 수행.
    private val imageLoader:ImageLoader
    private val maxByteSize:Int

    init{
        val context = App.applicationContext()
        this.requestQueue = Volley.newRequestQueue(context)
        //요청별 우선 순위 : 목록조회와 이미지 다운로드를 할때 목록조회가 우선순위가 높게 설정. 다음 페이지의 목록조회를 요청하면 이전페이지의 이미지로딩이 끝나지 않아도 기다리지 않고 수행.
        this.requestQueue.start()
        this.maxByteSize =calculateMaxByteSize(context)
        //이미지 로더(volley에서 지원)에는 request queue, image cache가 들어감
        this.imageLoader = ImageLoader(requestQueue,
            object :ImageLoader.ImageCache{
                private val lruCache = object:LruCache<String, Bitmap>(maxByteSize){
                    override fun sizeOf(key: String?, value: Bitmap): Int {
                        return value.byteCount
                    }
                }
                @Synchronized//동기화
                override fun getBitmap(url: String): Bitmap? {//?를 넣어주면서 null일 수도 있게... 이게 없으니 에러뜸...
                    return lruCache.get(url)
                }
                @Synchronized//동기화
                override fun putBitmap(url: String?, bitmap: Bitmap?) {
                    lruCache.put(url, bitmap)
                }
            }

        )

    }


    fun setImageFromUrl(imageView: NetworkImageView, url:String){
        imageView.setImageUrl(url, imageLoader)
    }

    private fun calculateMaxByteSize(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics//디스플레이에 대한 일반적인 정보를 설명하는 구조
        val screenBytes = displayMetrics.widthPixels * displayMetrics.heightPixels * 4
        //스크린 크기 * 4(비트맵의 1픽셀의 크기)
        return screenBytes * 3
    }
}