package com.example.petmatch.model

data class Mascota(
    val color: String,
    val edad: Int,
    val idmascota: Int,
    val imagen: String,
    val latitud: Double,
    val longuitud: Double,
    val nombre: String,
    val raza: String,
    val usuario: Usuario,
    val estado: String,
    val sexo: String,
    val tamanio: String,
    val tipo: String,
    val visitas: Int
) {
    var favorito: Boolean = false
}