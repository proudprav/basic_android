package com.example.myapplication

import com.google.gson.annotations.SerializedName


data class Errors (

  @SerializedName("error"             ) var error             : Error?  = Error(),
  @SerializedName("formDataFieldName" ) var formDataFieldName : String? = null

)