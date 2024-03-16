package com.example.petmatch

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.lifecycle.lifecycleScope
import com.example.petmatch.getlocation.LocationService
import com.example.petmatch.proxy.interfaces.ApiService
import com.example.petmatch.proxy.retrofit.MascotaRetrofit
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.util.Arrays

class activity_agregarMascota : AppCompatActivity() {

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
    private var idUsuario: Int = 0
    private var indexSexo: String? = null
    private var indexTamanio: String? = null
    private var indexTipo: String? = null
    private lateinit var rol: String
    private lateinit var navegador: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_mascota)

        navegador = findViewById(R.id.navega_bar)
        navegador.selectedItemId = R.id.action_agregarpet

        Navegacion(this,navegador)

        rol = getSessionRol()!!
        verMenu()

        idUsuario = getSessionIdUsuario()

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
        crearSpinners()

    }
    private fun getSessionIdUsuario(): Int {
        val preference = PreferenceHelper.defaultPrefs(this)
        return preference["idusuario", 0]
    }

    private fun crearSpinners() {
        val spnSexo: Spinner = findViewById(R.id.spn_agregar_sexo)
        val opcionesSexo = resources.getStringArray(R.array.spnSexo)
        val adapterSexo = ArrayAdapter(this,R.layout.custom_spinner_dropdown_item, opcionesSexo)
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSexo.adapter = adapterSexo

        spnSexo.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                indexSexo = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val spnTamanio: Spinner = findViewById(R.id.spn_agregar_tamanio)
        val opcionesTamanio = resources.getStringArray(R.array.spnTamanio)
        val adapterTamanio = ArrayAdapter(this,R.layout.custom_spinner_dropdown_item, opcionesTamanio)
        adapterTamanio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTamanio.adapter = adapterTamanio

        spnTamanio.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                indexTamanio = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val spnTipo: Spinner = findViewById(R.id.spn_agregar_tipo)
        val opcionesTipo = resources.getStringArray(R.array.spnTipo)
        val adapterTipo = ArrayAdapter(this,R.layout.custom_spinner_dropdown_item, opcionesTipo)
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTipo.adapter = adapterTipo

        spnTipo.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                indexTipo = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


    }

    private fun getSessionRol(): String? {
        val preference = PreferenceHelper.defaultPrefs(this)
        return preference["rol", null]
    }

    private fun verMenu() {
        val menu = navegador.menu.findItem(R.id.action_agregarpet)
        if(rol=="ADMIN"){
            menu.isVisible = true
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

        CoroutineScope(Dispatchers.IO).launch {
            val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                .postRegistrarMascota(
                    etNombre,
                    etRaza,
                    etColor,
                    edad,
                    imagenParte,
                    latitud,
                    longuitud,
                    idUsuario,
                    "Adopción",
                    indexSexo,
                    indexTamanio,
                    indexTipo)
            val data = retrofit.body()
            runOnUiThread{
                Toast.makeText(this@activity_agregarMascota, "Publicacion agregada correctamente", Toast.LENGTH_LONG).show()
                val irAlLogin = Intent(this@activity_agregarMascota, MainActivity::class.java)
                startActivity(irAlLogin)
            }
        }

    }

    fun generarNumeroAleatorio(min: Int, max: Int): Int {
        require(min < max) { "El valor mínimo debe ser menor que el valor máximo" }
        return (min..max).random()
    }



}