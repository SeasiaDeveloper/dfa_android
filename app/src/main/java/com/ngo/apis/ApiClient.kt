package com.ngo.apis

import com.ngo.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



object ApiClient {

    fun getClient(): Retrofit {
        val builder = OkHttpClient.Builder().readTimeout(3, TimeUnit.MINUTES).connectTimeout(3, TimeUnit.MINUTES).addInterceptor { chain ->
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
            val build = builder.addHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9zdGdzcC5hcHBzbmRldnMuY29tOjkwNDFcL2RydWdmcmVlIiwiaWF0IjoxNTg1MTEwODYyLCJuYmYiOjE1ODUxMTA4NjIsImV4cCI6MTU4NTcxNTY2MiwiZGF0YSI6eyJ1c2VyIjp7ImlkIjoiMjkifX19.pLRaonhKDJwOQIq7MJtbkm3exbcdNlKWJ0LDseLEtB0").
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