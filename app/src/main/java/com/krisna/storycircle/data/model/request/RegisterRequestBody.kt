package com.krisna.storycircle.data.model.request

data class RegisterRequestBody(
    val email: String,
    val name: String,
    val password: String
)
