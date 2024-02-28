package com.example.petmatch

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.lifecycle.lifecycleScope
import com.example.petmatch.getlocation.LocationService
import com.example.petmatch.io.ApiService
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class activity_agregarMascota : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    private val locationService:LocationService = LocationService()

    val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        if(uri !=null){
            ivImagen.setImageURI(uri)
            imageUri = uri
        }
    }

    lateinit var btnCargarImage: Button
    lateinit var ivImagen: ImageView
    lateinit var imageUri: Uri
    lateinit var btnGuardarMascota: Button
    private var latitud: Double = 0.0
    private var longuitud: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_mascota)

        btnCargarImage = findViewById(R.id.btn_agregar_btnimagen)
        ivImagen = findViewById(R.id.iv_mostrar_imagen)

        btnCargarImage.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        btnGuardarMascota = findViewById(R.id.btn_agregar_mascota)
        btnGuardarMascota.setOnClickListener{
            val archivo = obtenerArchivoDesdeUri(imageUri)
            if (archivo != null) {
                enviarMascota(archivo)
            }
        }

        lifecycleScope.launch {
                val result = locationService.getUserLocation(this@activity_agregarMascota)

                if(result != null){
                    latitud = result.latitude
                    longuitud = result.longitude
                }
        }

    }

    private fun obtenerArchivoDesdeUri(uri: Uri): File? {
        val numeroAleatorio = generarNumeroAleatorio(584,1869)
        val inputStream = contentResolver.openInputStream(uri)
        val archivo = File(cacheDir, "imagen_${numeroAleatorio}.jpg")
        inputStream?.use { input ->
            archivo.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return archivo
    }

    private fun enviarMascota(archivo: File) {

        val etNombre = findViewById<TextInputEditText>(R.id.et_agregar_nombre).text.toString()
        val etRaza = findViewById<TextInputEditText>(R.id.et_agregar_raza).text.toString()
        val etColor = findViewById<TextInputEditText>(R.id.et_agregar_color).text.toString()
        val etEdad = findViewById<TextInputEditText>(R.id.et_agregar_edad).text.toString()
        val edad: Int = if (etEdad.isNotEmpty()) {
            etEdad.toInt()
        } else {
            0
        }

        val tipoContenido = "image/jpeg".toMediaTypeOrNull()
        val cuerpoArchivo = archivo.asRequestBody(tipoContenido)

        val imagenParte = MultipartBody.Part.createFormData("file", archivo.name, cuerpoArchivo)


        val call = apiService.postRegistrarMascota(
            etNombre,
            etRaza,
            etColor,
            edad,
            imagenParte,
            latitud,
            longuitud,
            1
        )
        call.enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    val respuesta = response.body()
                    if(respuesta != null){
                        //Toast.makeText(applicationContext, "Mascota Registrada", Toast.LENGTH_SHORT).show()
                    }else{
                        //Toast.makeText(applicationContext, "Se produjo un error en el servidor 1", Toast.LENGTH_SHORT).show()
                        return
                    }
                }else{
                    //Toast.makeText(applicationContext, "Se produjo un error en el servidor 2", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(applicationContext, "Se produjo un error en el servidor xD", Toast.LENGTH_SHORT).show()
            }

        })


    }

    fun generarNumeroAleatorio(min: Int, max: Int): Int {
        require(min < max) { "El valor mínimo debe ser menor que el valor máximo" }
        return (min..max).random()
    }



}