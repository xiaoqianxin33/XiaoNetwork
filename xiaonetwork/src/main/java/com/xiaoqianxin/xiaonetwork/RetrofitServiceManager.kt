package com.xiaoqianxin.xiaonetwork

import com.xiaoqianxin.xiaonetwork.interceptor.HttpLoggingInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.logging.Level

object RetrofitServiceManager {
    //超时时间
    private val DEFAULT_TIME_OUT = 5L
    private val DEFAULT_READ_TIME_OUT = 10L
    private lateinit var mRetrofit: Retrofit
    val mBuild: OkHttpClient.Builder
    private var baseUrl: String

    init {
        baseUrl = ""
        mBuild = OkHttpClient.Builder()
        mBuild.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
        mBuild.readTimeout(DEFAULT_READ_TIME_OUT, TimeUnit.SECONDS)
        val logInterceptor = HttpLoggingInterceptor("retrofit").apply {
            setPrintLevel(HttpLoggingInterceptor.Level.BODY)
            setColorLevel(Level.WARNING)
        }
        mBuild.addInterceptor(logInterceptor)
        createRetrofit()
    }

    private fun createRetrofit() {
        //创建retrofit
        mRetrofit = Retrofit.Builder()
                .client(mBuild.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
    }

    /**
     *添加拦截器
     */
    fun addInterceptor(interceptor: Interceptor): RetrofitServiceManager {
        mBuild.addInterceptor(interceptor)
        createRetrofit()
        return this
    }

    /**
     * 设置Host地址
     */
    fun setBaseUrl(baseUrl: String): RetrofitServiceManager {
        RetrofitServiceManager.baseUrl = baseUrl
        createRetrofit()
        return this
    }


    //获取对应的service
    fun <T> create(service: Class<T>): T {
        return mRetrofit.create(service)
    }

}