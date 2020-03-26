package com.ngo.apis

import com.ngo.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



object ApiClient {

    fun getClient(): Retrofit {
        val builder = OkHttpClient.Builder().readTimeout(45, TimeUnit.SECONDS).connectTimeout(45, TimeUnit.SECONDS).addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            val build = builder.addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(build)
        }

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.networkInterceptors().add(loggingInterceptor)
        val url = Constants.BASE_URL
        return Retrofit.Builder().baseUrl(url).client(builder.build())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    fun getClientWithToken(): Retrofit {
        val builder = OkHttpClient.Builder().readTimeout(45, TimeUnit.SECONDS).connectTimeout(45, TimeUnit.SECONDS).addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            val build = builder.addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9zdGdzcC5hcHBzbmRldnMuY29tOjkwNDFcL2RydWdmcmVlIiwiaWF0IjoxNTg1MTQ3OTQ0LCJuYmYiOjE1ODUxNDc5NDQsImV4cCI6MTU4NTc1Mjc0NCwiZGF0YSI6eyJ1c2VyIjp7ImlkIjoiMzgifX19.yl9xlj3mHJZBmGVerSH6l7UqSIw1_fSKNeQXYkfZIaI").
                build()
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