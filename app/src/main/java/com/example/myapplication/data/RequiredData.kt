package com.example.myapplication.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue

object RequiredData {

    var authKey = "luma-api-key=15402ec8-96af-44e5-b4d8-1f039fb419cc-74cae78-ed1a-4785-ae5c-75cd0b45f77c"

    var sourceLink = ""

    var slug = ""

    var text by mutableStateOf(TextFieldValue(""))

    fun setToken(text: String){
        authKey = "luma-api-key=$text"
    }

}
