package com.example.myapplication

import com.google.gson.annotations.SerializedName


data class Files (

  @SerializedName("formDataFieldName" ) var formDataFieldName : String? = null,
  @SerializedName("accountId"         ) var accountId         : String? = null,
  @SerializedName("filePath"          ) var filePath          : String? = null,
  @SerializedName("fileUrl"           ) var fileUrl           : String? = null

)