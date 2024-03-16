package com.example.petmatch

import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.utils.widget.MotionButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petmatch.adapter.MascotaAdapter
import com.example.petmatch.getlocation.LocationService
import com.example.petmatch.model.Favorito
import com.example.petmatch.model.Mascota
import com.example.petmatch.proxy.interfaces.ApiService
import com.example.petmatch.proxy.retrofit.MascotaRetrofit
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.get
import com.example.petmatch.utils.PreferenceHelper.set
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.*

class activity_listar : AppCompatActivity() {


    private val locationService: LocationService = LocationService()

    private var latitud: Double = 0.0
    private var longuitud: Double = 0.0

    private lateinit var recycledViewMascota: RecyclerView
    private lateinit var lblTitle: TextView
    private var idUsuario: Int = 0
    private lateinit var usuario: String
    lateinit var miLista: List<Favorito>
    private lateinit var rol: String
    private lateinit var navegador: BottomNavigationView
    private var listaFiltradaOriginal: List<Mascota>? = null
    private var listaFiltradaNueva: List<Mascota>? = null
    private lateinit var btnBuscar: ImageButton
    private lateinit var tiBuscar: TextInputEditText
    private lateinit var btnCerca: MotionButton
    private lateinit var btnMasVisitados: MotionButton
    private lateinit var btnGrande: MotionButton
    private lateinit var btnPequenio: MotionButton
    private lateinit var btnMacho: MotionButton
    private lateinit var btnHembra: MotionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar)


        navegador = findViewById(R.id.navega_bar)
        navegador.selectedItemId = R.id.action_inicio

        Navegacion(this,navegador)

        recycledViewMascota = findViewById(R.id.recycle_mascotas)
        lblTitle = findViewById(R.id.lbl_listar_title)

        usuario = getSessionToken()!!

        lblTitle.text = getString(R.string.welcome_message, usuario)

        obtenerIdUsuario()
        configuration()
        obtenerListMascotas()


        lifecycleScope.launch {
            val result = locationService.getUserLocation(this@activity_listar)

            if(result != null){
                latitud = result.latitude
                longuitud = result.longitude
            }
        }


        tiBuscar = findViewById(R.id.et_listar_buscar)
        tiBuscar.setOnKeyListener{ v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val palabra = tiBuscar.text.toString()
                listaFiltradaNueva = filtrarListaAll(palabra)
                renderizarLista()
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(tiBuscar.windowToken, 0)
                return@setOnKeyListener true
            }
            false
        }
        btnBuscar = findViewById(R.id.searchButton)
        btnBuscar.setOnClickListener{
            val palabra = tiBuscar.text.toString()
            listaFiltradaNueva = filtrarListaAll(palabra)
            renderizarLista()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(tiBuscar.windowToken, 0)
        }


        btnCerca = findViewById(R.id.btn_filtro_cerca)
        btnCerca.setOnClickListener{
            listaFiltradaNueva = filtrarListaCerca(5.0)
            renderizarLista()
        }
        btnMasVisitados = findViewById(R.id.btn_filtro_popular)
        btnMasVisitados.setOnClickListener{
            listaFiltradaNueva = filtrarListaMasVisto()
            renderizarLista()
        }


        btnGrande = findViewById(R.id.btn_filtro_grande)
        btnGrande.setOnClickListener{
            listaFiltradaNueva = filtrarListaTamanio("Grande")
            renderizarLista()
        }
        btnPequenio = findViewById(R.id.btn_filtro_pequenio)
        btnPequenio.setOnClickListener{
            listaFiltradaNueva = filtrarListaTamanio("Pequeño")
            renderizarLista()
        }
        btnMacho = findViewById(R.id.btn_filtro_macho)
        btnMacho.setOnClickListener{
            listaFiltradaNueva = filtrarListaSexo("Macho")
            renderizarLista()
        }
        btnHembra = findViewById(R.id.btn_filtro_hembra)
        btnHembra.setOnClickListener{
            listaFiltradaNueva = filtrarListaSexo("Hembra")
            renderizarLista()
        }

    }

    override fun onResume() {
        super.onResume()
        obtenerListMascotas()
    }
    private fun filtrarListaCerca(kilometro:Double): List<Mascota>? {
        println("Mi Latitud es : $latitud y mi Longuitud es: $longuitud")
        val mascotasEnRadio  = listaFiltradaOriginal?.filter { mascota ->
            distanciaEntreDosPuntos(latitud, longuitud,mascota.latitud, mascota.longuitud
            ) <= kilometro
        }
        return mascotasEnRadio
    }
    private fun filtrarListaMasVisto(): List<Mascota>? {
        val filtro = listaFiltradaOriginal?.sortedByDescending { it.visitas}
        return filtro
    }
    private fun filtrarListaTamanio(palabraClave:String): List<Mascota>? {
        val filtro = listaFiltradaOriginal?.filter { it.tamanio.contains(palabraClave,ignoreCase = true) }
        return filtro
    }
    private fun filtrarListaSexo(palabraClave:String): List<Mascota>? {
        val filtro = listaFiltradaOriginal?.filter { it.sexo.contains(palabraClave,ignoreCase = true) }
        return filtro
    }
    private fun filtrarListaAll(palabraClave:String): List<Mascota>? {
        val filtro = listaFiltradaOriginal?.filter {
                it.color.contains(palabraClave,ignoreCase = true) ||
                it.nombre.contains(palabraClave,ignoreCase = true) ||
                it.raza.contains(palabraClave,ignoreCase = true) ||
                it.sexo.contains(palabraClave,ignoreCase = true) ||
                it.tamanio.contains(palabraClave,ignoreCase = true) ||
                it.tipo.contains(palabraClave,ignoreCase = true) ||
                it.edad.toString().contains(palabraClave,ignoreCase = true)}
        return filtro
    }
    fun distanciaEntreDosPuntos(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radioTierra = 6371 // Radio de la Tierra en kilómetros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return radioTierra * c
    }

    private fun createIdUsuario(idusuario: Int){
        val preference = PreferenceHelper.defaultPrefs(this)
        preference["idusuario"] = idusuario
        //Toast.makeText(applicationContext, "USUARIO"+idusuario, Toast.LENGTH_SHORT).show()
    }
    private fun createRolUsuario(rol: String) {
        val preference = PreferenceHelper.defaultPrefs(this)
        preference["rol"] = rol
    }

    private fun obtenerIdUsuario() {
        CoroutineScope(Dispatchers.IO).launch {
            val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                .getUsuario(usuario)
            val data = retrofit.body()
            runOnUiThread{
                if(data != null){
                    createIdUsuario(data!!.idusuario)
                    idUsuario = data!!.idusuario
                    rol = data!!.roles[0].name
                    createRolUsuario(rol)
                    verMenu()
                }
            }
        }
    }


    private fun verMenu() {
        val menu = navegador.menu.findItem(R.id.action_agregarpet)
        if(rol=="ADMIN"){
            menu.isVisible = true
        }
    }

    private fun getSessionToken(): String? {
        val preference = PreferenceHelper.defaultPrefs(this)
        return preference["username", null]
    }


    private fun configuration() {
        recycledViewMascota.layoutManager = GridLayoutManager(this, 2)
        //Toast.makeText(applicationContext, "IDUSUARIO"+idUsuario, Toast.LENGTH_SHORT).show()
    }

    private fun obtenerListMascotas() {

        CoroutineScope(Dispatchers.IO).launch {
            val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                .postListMascotas()
            listaFiltradaOriginal = retrofit.body()
            listaFiltradaNueva = listaFiltradaOriginal
            val retrofitUsuario = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                .getUsuario(usuario)
            val datausuario = retrofitUsuario.body()
            if (datausuario != null) {
                miLista = datausuario.favoritos
            }
            renderizarLista()

        }



        /*val call = apiService.postListMascotas()
        call.enqueue(object: Callback<List<Mascota>>{
            override fun onResponse(call: Call<List<Mascota>>, response: Response<List<Mascota>>) {
                if(response.isSuccessful){
                    val listMascotas = response.body()
                    if(listMascotas == null){
                        Toast.makeText(applicationContext, "La respuesta es nula", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if(listMascotas != null){
                        listaMascotasfinal.addAll(listMascotas)
                        ejecutarSiguienteBloque()
                    }else{
                        Toast.makeText(applicationContext, "Las credenciales son incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(applicationContext, "Se produjo un error en el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Mascota>>, t: Throwable) {
                Toast.makeText(applicationContext, "Se produjo un error en el servidor xD", Toast.LENGTH_SHORT).show()
            }

        })*/
    }

    private fun renderizarLista() {
        runOnUiThread{
            recycledViewMascota.adapter = MascotaAdapter( listaFiltradaNueva!!, miLista,
                { it ->
                    val mascotaId = it.idmascota
                    val intent =Intent( this@activity_listar, activity_detalleMascota::class.java)
                        .apply {
                            putExtra("idMascota", it.idmascota)
                        }
                    startActivity(intent)
                    return@MascotaAdapter
                },{
                    val mascotaId = it.idmascota
                    CoroutineScope(Dispatchers.IO).launch {
                        val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                            .addFavorite(idUsuario, mascotaId)
                        val data = retrofit.body()
                        runOnUiThread{
                            if(data != null){
                                Toast.makeText(this@activity_listar, "Favorito Agregado", Toast.LENGTH_SHORT).show()
                            }
                            /*val irLista = Intent(this@activity_listar, activity_listar::class.java)

                            startActivity(irLista)*/
                        }
                    }
                    return@MascotaAdapter
                }
            )

        }
    }
    /*private fun ejecutarSiguienteBloque() {
        // Aquí puedes colocar el código que deseas ejecutar después de recibir la respuesta
        println("Estas son las mascotas : "+ listaMascotasfinal)
        // Configuramos el RecyclerView con la lista de mascotas
        val recycledviewMascota: RecyclerView =findViewById(R.id.recycle_mascotas)
        recycledviewMascota.layoutManager = LinearLayoutManager(this@activity_listar)
        recycledviewMascota.adapter = MascotaAdapter(listaMascotasfinal)
    }*/
}