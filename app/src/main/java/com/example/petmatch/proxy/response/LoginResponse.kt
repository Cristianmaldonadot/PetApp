package com.example.petmatch.io.response

data class LoginResponse(
    val Message: String,
    val Rol: List<Rol>,
    val Username: String,
    val token: String
)
