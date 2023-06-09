package com.example.myapplication

import com.example.myapplication.Errors
import com.google.gson.annotations.SerializedName


data class ExampleJson2KtKotlin (

    @SerializedName("errors" ) var errors : ArrayList<Errors> = arrayListOf(),
    @SerializedName("files"  ) var files  : ArrayList<Files>  = arrayListOf()

)