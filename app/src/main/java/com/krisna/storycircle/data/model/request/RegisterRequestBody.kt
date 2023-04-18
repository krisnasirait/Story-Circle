package com.krisna.storycircle.data.model.request

data class RegisterRequestBody(
    val name: String,
    val email: String,
    val password: String
)
