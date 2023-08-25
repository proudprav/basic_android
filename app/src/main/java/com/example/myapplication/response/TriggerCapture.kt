package com.example.myapplication.response

data class TriggerCapture(
    val title: String,
    val type: String,
    val location: String?,
    val privacy: String,
    val date: String,
    val username: String,
    val status: String,
    val slug: String,
    val latestRun: LatestRun
)

data class LatestRun(
    val status: String,
    val progress: Int,
    val currentStage: String,
    val artifacts: List<String>
)
