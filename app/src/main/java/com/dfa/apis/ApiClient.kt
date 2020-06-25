package com.dfa.apis

import com.dfa.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiClient {

    fun getClient(): Retrofit {
        val builder = /*getUnsafeOkHttpClient()*/OkHttpClient.Builder().readTimeout(3, TimeUnit.MINUTES).connectTimeout(3, TimeUnit.MINUTES).writeTimeout(3, TimeUnit.MINUTES).addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            val build = builder.addHeader("Content-Type", "application/json")
                .build()
            builder
            chain.proceed(build)
        }

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        builder.networkInterceptors().add(loggingInterceptor)
        val url = Constants.BASE_URL
        return Retrofit.Builder().baseUrl(url).client(builder.build())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    /*@JvmStatic
    fun getUnsafeOkHttpClient(): OkHttpClient.Builder =
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) = Unit

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) = Unit

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )
            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(
                sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { _, _ -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }*/
}