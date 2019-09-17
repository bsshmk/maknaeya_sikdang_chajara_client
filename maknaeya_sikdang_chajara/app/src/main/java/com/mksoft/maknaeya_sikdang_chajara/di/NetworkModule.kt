package com.mksoft.mkjw_second_project.di.module

import android.graphics.Paint
import com.mksoft.maknaeya_sikdang_chajara.utils.BASE_URL
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@Suppress("unused")
class NetworkModule{


    //필요한 API여기에 추가
    @Provides
    @Singleton
    internal fun provideLogging(): HttpLoggingInterceptor {
        var logging:HttpLoggingInterceptor =  HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logging
    }
    @Provides
    @Singleton
    internal fun provideHttpClient(logging :HttpLoggingInterceptor): OkHttpClient.Builder {
        var httpClient:OkHttpClient.Builder = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        return httpClient
    }


    @Provides
    @Singleton
    internal fun provideRetrofitInterface(httpClient:OkHttpClient.Builder):Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(httpClient.build())
            .build()
    }
}