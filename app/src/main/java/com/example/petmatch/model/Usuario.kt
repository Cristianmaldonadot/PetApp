package com.example.petmatch.model

data class Usuario(
    val celular: String,
    val direccion: String,
    val email: String,
    val idusuario: Int,
    val password: String,
    val roles: List<Role>,
    val username: String,
    val favoritos: List<Favorito>,
    val nombre:String,
    val appaterno:String
)