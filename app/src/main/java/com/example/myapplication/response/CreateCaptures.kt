package com.example.myapplication.response

data class CreateCaptures(
    val signedUrls: SignedUrls,
    val capture: Capture
)

data class SignedUrls(
    val source: String
)

data class Capture(
    val title: String,
    val type: String,
    val location: String?,
    val privacy: String,
    val date: String,
    val username: String,
    val status: String,
    val slug: String
)

