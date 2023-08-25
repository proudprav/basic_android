package com.example.myapplication.response

data class CreditsResponse(
    val remaining: Int,
    val used: Int,
    val total: Int
)