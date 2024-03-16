package com.example.petmatch.proxy.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MascotaRetrofit {

    companion object{
        private const val BASE_URL = "http://192.168.0.231:8098/"
        fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}