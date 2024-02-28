package com.example.petmatch.io.response

data class Usuario(
    val celular: String,
    val direccion: Any,
    val email: String,
    val idusuario: Int,
    val password: String,
    val roles: List<Role>,
    val username: String
)