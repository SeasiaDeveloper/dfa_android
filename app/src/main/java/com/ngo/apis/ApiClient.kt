package com.ngo.apis

import com.ngo.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    fun getClient(): Retrofit {
        val builder = OkHttpClient.Builder().readTimeout(73, TimeUnit.MINUTES).connectTimeout(3, TimeUnit.MINUTES).addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            val build = builder.addHeader("Content-Type", "application/json")
                .build()
            builder
            chain.proceed(build)
        }

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.networkInterceptors().add(loggingInterceptor)
        val url = Constants.BASE_URL
        return Retrofit.Builder().baseUrl(url).client(builder.build())
            .addConverterFactory(GsonConverterFactory.create()).build()

    }

}