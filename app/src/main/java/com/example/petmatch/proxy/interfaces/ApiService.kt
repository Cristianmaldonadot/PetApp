package com.example.petmatch.proxy.interfaces

import com.example.petmatch.proxy.response.LoginResponse
import com.example.petmatch.proxy.response.Usuario
import com.example.petmatch.model.CreateUserDTO
import com.example.petmatch.model.Favorito
import com.example.petmatch.model.Mascota
import com.example.petmatch.model.UpdateUserDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST(value = "login")
    suspend fun postLogin(@Body requestBody: Map<String, String>): Response<LoginResponse>

    @POST(value = "registrarusuario")
    suspend fun postSignIn(@Body requestBody: CreateUserDTO): Response<Usuario>

    @POST(value = "actualizarusuario/{idusuario}")
    suspend fun postUpdateUser(@Path("idusuario") idusuario: Int, @Body requestBody:UpdateUserDTO): Response<com.example.petmatch.model.Usuario>

    @POST(value = "cambiarcontrasena/{idUsuario}/{contraOld}/{contraNew}")
    suspend fun postCambiarContra(@Path("idUsuario") idUsuario: Int, @Path("contraOld") contraOld: Int,
                                  @Path("contraNew") contraNew: Int) :Response<Unit>

    @GET(value = "listarmascotas")
    suspend fun postListMascotas(): Response<List<Mascota>>

    @DELETE(value = "delete/{idMascota}")
    suspend fun deleteMascota(@Path("idMascota") idmascota: Int):Response<Unit>

    @GET(value = "listarmascotaspornombre/{username}")
    suspend fun getListMascotasPorNombre(@Path("username") username:String):Response<List<Mascota>>

    @Multipart
    @POST(value = "registrarmascota")
    suspend fun postRegistrarMascota(@Query("nombre") nombre:String, @Query("raza") raza:String,
                             @Query("color") color:String, @Query("edad") edad:Int,
                             @Part file:MultipartBody.Part?, @Query("latitud") latitud:Double,
                             @Query ("longuitud") longuitud:Double, @Query ("usuario") usuario:Int,
                             @Query("estado") estado:String?, @Query("sexo") sexo:String?,
                             @Query("tamanio") tamanio:String?, @Query("tipo") tipo:String?) : Response<Mascota>

    @POST(value = "updatemascota")
    suspend fun updateMascota(@Query("id") id: Int, @Query("nombre") nombre:String, @Query("raza") raza:String,
                                     @Query("color") color:String, @Query("edad") edad:Int, @Query("latitud") latitud:Double,
                                     @Query ("longuitud") longuitud:Double, @Query ("usuario") usuario:Int,
                                     @Query("estado") estado:String?, @Query("sexo") sexo:String?,
                                     @Query("tamanio") tamanio:String?, @Query("tipo") tipo:String?) : Response<Unit>

    @GET(value = "obtenermascota/{idmascota}")
    suspend fun getMascota(@Path("idmascota") idmascota:Int):Response<Mascota>

    @GET(value = "usuariopornombre/{nombre}")
    suspend fun getUsuario(@Path("nombre") nombre: String):Response<com.example.petmatch.model.Usuario>

    @POST(value = "agregarfavoritousuario/{id}")
    suspend fun addFavorite(@Path("id") id: Int, @Query("idmascota") idmascota: Int): Response<com.example.petmatch.model.Usuario>

    @POST(value = "listarporfavoritos")
    suspend fun getMascotasFavoritos(@Body requestBody: List<Favorito>): Response<List<Mascota>>

    @DELETE(value = "eliminarfavorito/{idUsuario}/{idMascota}")
    suspend fun deleteMascotaFavorito(@Path("idUsuario") idUsuario:Int, @Path("idMascota") idMascota:Int):Response<Unit>

    @POST(value = "crearsolicitud")
    suspend fun postCrearSolicitud(@Query("fecha") fecha:String, @Query("observaciones") observaciones:String?,
                                   @Query("usuario") usuario: Int, @Query("mascota") mascota:Int): Response<Unit>

    companion object Factory{
        private const val BASE_URL = "http://192.168.0.231:8098/"
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
