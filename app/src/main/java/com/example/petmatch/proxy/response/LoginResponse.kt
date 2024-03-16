package com.example.petmatch.proxy.response

data class LoginResponse(
    val Message: String,
    val Rol: List<Rol>,
    val Username: String,
    val token: String
)
