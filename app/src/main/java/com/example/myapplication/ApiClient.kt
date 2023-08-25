package com.example.myapplication

import com.example.myapplication.response.CreateCaptures
import com.example.myapplication.response.CreditsResponse
import com.example.myapplication.response.Plain
import com.example.myapplication.response.TriggerCapture
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


class APIClient {

    companion object {
        lateinit var retrofit: Retrofit
        val client: Retrofit
        get() {
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl("https://webapp.engineeringlumalabs.com/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
            return retrofit
        }
    }
}


interface APIInterface {

//    @Multipart
//    @POST("v2/accounts/kW15bMw/uploads/form_data")
//    fun  doGetListResources(@Part  file: MultipartBody.Part , @HeaderMap headers: Map<String, String>) : Call<ExampleJson2KtKotlin>

    @GET("capture/credits")
    fun getCredits(@HeaderMap headers: Map<String, String>): Call<CreditsResponse>

    @POST("capture")
    @FormUrlEncoded
    fun createCapture(@HeaderMap headers: Map<String, String>, @Field("title") title : String ):Call<CreateCaptures>

    @Multipart
    @PUT
    fun uploadCapture(@Url url : String, @Part  file: MultipartBody.Part): Call<ResponseBody>

    @POST("capture/{slug}")
    fun triggerCapture(@HeaderMap headers: Map<String, String>,@Path("slug") slug : String): Call<TriggerCapture>
}