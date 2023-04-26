package com.krisna.storycircle.data.model.response.allstory

data class Story(
    val createdAt: String,
    val description: String,
    val id: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val photoUrl: String
)