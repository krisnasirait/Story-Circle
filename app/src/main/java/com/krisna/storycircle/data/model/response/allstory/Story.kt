package com.krisna.storycircle.data.model.response.allstory

data class Story(
    val createdAt: String,
    val description: String,
    val id: String,
    val lat: Any,
    val lon: Any,
    val name: String,
    val photoUrl: String
)