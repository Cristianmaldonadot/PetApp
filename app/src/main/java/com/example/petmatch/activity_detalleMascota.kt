package com.example.petmatch

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.utils.widget.MotionButton
import com.bumptech.glide.Glide
import com.example.petmatch.proxy.interfaces.ApiService
import com.example.petmatch.proxy.retrofit.MascotaRetrofit
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.get
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.create
import java.net.URLEncoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class activity_detalleMascota : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var lblNombre: TextView
    private lateinit var lblTipo: TextView
    private lateinit var lblTamanio: TextView
    private lateinit var lblRaza: TextView
    private lateinit var lblColor: TextView
    private lateinit var lblEdad: TextView
    private lateinit var imgImagen: ImageView
    private lateinit var imgSexo: ImageView
    private lateinit var imgTipo: ImageView
    private lateinit var imgTamanio: ImageView
    private lateinit var mbtnUbicacion: MotionButton
    private lateinit var scrollDetails: ScrollView
    private lateinit var mbtnContacto: MotionButton
    private var latitud:Double = 0.0
    private var longuitud:Double = 0.0
    private lateinit var numeroContacto: String
    private lateinit var nombreMascota: String
    private lateinit var btnBack:ImageView
    private lateinit var btnSolicitud:MotionButton
    private var idMascota = 0
    private var idUsuario = 0


    private lateinit var gMap:GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_mascota)


        configuracion()
        obtenerData()
        btnBack.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        CoroutineScope(Dispatchers.IO).launch {
            obtenerMapa()
        }

        mbtnUbicacion.setOnClickListener{
            scrollDetails.post{
                scrollDetails.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }

        mbtnContacto.setOnClickListener{
            val mensaje = "Hola quiero adoptar a $nombreMascota!!"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/$numeroContacto?text=${URLEncoder.encode(mensaje, "UTF-8")}")
            startActivity(intent)
        }

        btnSolicitud.setOnClickListener {
            generarSolicitud()
        }

        idUsuario = getIdUsuario()
    }

    private fun getIdUsuario(): Int {
        val preference = PreferenceHelper.defaultPrefs(this)
        return preference["idusuario", null]

    }

    private fun generarSolicitud() {

        val fecha = LocalDate.now()
        val formatter = DateTimeFormatter.ISO_DATE // Puedes ajustar el formato según tus necesidades
        val fechaFormateada = fecha.format(formatter)

        CoroutineScope(Dispatchers.IO).launch {
            val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                .postCrearSolicitud(fechaFormateada, "Este usuario es el 1 solicitante", idMascota, idUsuario )
            val message = retrofit.message()
            runOnUiThread{
                if(retrofit.isSuccessful){
                    Toast.makeText(this@activity_detalleMascota, "Solicitud Creada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun obtenerMapa() {
        delay(2000)
        withContext(Dispatchers.Main) {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment
            mapFragment.getMapAsync{
                    googleMap -> onMapReady(googleMap)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        val ubicacion = LatLng(latitud, longuitud)
        val zoomLevel = 16.0f
        gMap.addMarker(MarkerOptions().position(ubicacion).title("Marker in Sydney"))
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, zoomLevel))
    }

    private fun configuracion() {
        lblNombre = findViewById(R.id.lbl_details_nombre)
        lblTipo = findViewById(R.id.lbl_details_tipo)
        lblTamanio = findViewById(R.id.lbl_details_tamaño)
        lblRaza = findViewById(R.id.lbl_details_raza)
        lblColor = findViewById(R.id.lbl_details_color)
        lblEdad = findViewById(R.id.lbl_details_edad)
        imgImagen = findViewById(R.id.img_details_main)
        imgSexo = findViewById(R.id.img_details_sex)
        imgTipo = findViewById(R.id.img_details_tipo)
        imgTamanio = findViewById(R.id.img_details_tamaño)
        mbtnUbicacion = findViewById(R.id.mbtn_ubicacion)
        scrollDetails = findViewById(R.id.scroll_details)
        mbtnContacto = findViewById(R.id.mbtn_details_contacto)
        btnBack = findViewById(R.id.img_details_back)
        btnSolicitud = findViewById(R.id.mbtn_solicitud)
    }

    private fun obtenerData() {
        val extras =intent.extras
        if(extras != null){

            idMascota =extras.getInt("idMascota")
            CoroutineScope(Dispatchers.IO).launch {
                val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                    .getMascota(idMascota)
                val data = retrofit.body()
                runOnUiThread{
                    if(retrofit.isSuccessful){
                        lblNombre.setText(data!!.nombre)
                        lblTipo.setText(data!!.tipo)
                        lblTamanio.setText(data!!.tamanio)
                        lblRaza.setText(data!!.raza)
                        lblColor.setText(data!!.color)
                        lblEdad.setText(data!!.edad.toString())
                        Glide.with(this@activity_detalleMascota).load(data!!.imagen).into(imgImagen)
                        when(data!!.sexo){
                            "Macho" -> imgSexo.setImageResource(R.drawable.male)
                            "Hembra" -> imgSexo.setImageResource(R.drawable.famele)
                        }
                        when(data!!.tipo){
                            "Perro" -> imgTipo.setImageResource(R.drawable.dog)
                            "Pato" -> imgTipo.setImageResource(R.drawable.cat)
                        }
                        when(data!!.tamanio){
                            "Pequeño" -> imgTamanio.setImageResource(R.drawable.bone_small)
                            "Mediano" -> imgTamanio.setImageResource(R.drawable.bone_medium)
                            "Grande" -> imgTamanio.setImageResource(R.drawable.bone_big)
                        }
                        latitud = data!!.latitud
                        longuitud = data!!.longuitud
                        numeroContacto = data!!.usuario.celular
                        nombreMascota = data!!.nombre
                    }
                }
            }

        }
    }

}