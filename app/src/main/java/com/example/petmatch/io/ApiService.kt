package com.example.petmatch.io

import com.example.petmatch.io.response.LoginResponse
import com.example.petmatch.io.response.Usuario
import com.example.petmatch.model.CreateUserDTO
import com.example.petmatch.model.Mascota
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST(value = "login")
    fun postLogin(@Body requestBody: Map<String, String>): Call<LoginResponse>

    @POST(value = "registrarusuario")
    fun postSignIn(@Body requestBody: CreateUserDTO): Call<Usuario>

    @GET(value = "listarmascotas")
    fun postListMascotas(): Call<List<Mascota>>

    @POST(value = "registrarmascota")
    fun postRegistrarMascota(@Query("nombre") nombre:String, @Query("raza") raza:String,
                             @Query("color") color:String, @Query("edad") edad:Int,
                             @Part file:MultipartBody.Part?, @Query("latitud") latitud:Double,
                             @Query ("longuitud") longuitud:Double, @Query ("usuario") usuario:Int )
    : Call<String>

    companion object Factory{
        private const val BASE_URL = "http://192.168.0.231:8098/"
        fun create(): ApiService{
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
