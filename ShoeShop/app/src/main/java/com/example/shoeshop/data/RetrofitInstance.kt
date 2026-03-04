package com.example.shoeshop.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy

object RetrofitInstance {
    const val SUBABASE_URL = "https://fgyhruylbifvlxlywabx.supabase.co/"

   private val proxy= Proxy(Proxy.Type.HTTP, InetSocketAddress("10.207.106.59",3128))
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
      .proxy(proxy)
        .addInterceptor(loggingInterceptor)
        .build()


    private  val retrofit = Retrofit.Builder()
        .baseUrl(SUBABASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
    val userManagementService = retrofit.create(UserManagementService::class.java)
}