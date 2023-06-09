package com.example.myapplication

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


class APIClient {

    companion object {
        lateinit var retrofit: Retrofit
        val client: Retrofit
        get() {
            val client = OkHttpClient.Builder().build()
            retrofit = Retrofit.Builder()
                .baseUrl("https://api.upload.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit
        }
    }
}


interface APIInterface {

    @Multipart
    @POST("v2/accounts/kW15bMw/uploads/form_data")
    fun  doGetListResources(@Part  file: MultipartBody.Part , @HeaderMap headers: Map<String, String>) : Call<ExampleJson2KtKotlin>
}