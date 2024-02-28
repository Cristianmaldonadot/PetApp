package com.example.petmatch.model

data class CreateUserDTO(
    val username: String,
    val email: String,
    val password: String,
    val roles: List<String>,
    val celular: String
)
