package com.example.petmatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petmatch.adapter.MascotaAdapter
import com.example.petmatch.model.Favorito
import com.example.petmatch.model.Mascota
import com.example.petmatch.proxy.interfaces.ApiService
import com.example.petmatch.proxy.retrofit.MascotaRetrofit
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.get
import com.example.petmatch.utils.PreferenceHelper.set
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class activity_favoritos : AppCompatActivity() {
    private lateinit var recycledViewMascota: RecyclerView
    private lateinit var lblTitle: TextView
    private var idUsuario: Int = 0
    private lateinit var usuario: String
    private lateinit var miLista: List<Favorito>
    private lateinit var rol: String
    private lateinit var navegador: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoritos)

        navegador = findViewById(R.id.navega_bar)
        navegador.selectedItemId = R.id.action_favoritos

        Navegacion(this,navegador)

        recycledViewMascota = findViewById(R.id.recycle_mascotas)
        lblTitle = findViewById(R.id.lbl_listar_title)

        usuario = getSessionUsername()!!
        rol = getSessionRol()!!

        verMenu()
        obtenerIdUsuario()
        configuration()
        obtenerListMascotas()

    }

    private fun createIdUsuario(idusuario: Int){
        val preference = PreferenceHelper.defaultPrefs(this)
        preference["idusuario"] = idusuario
        //Toast.makeText(applicationContext, "USUARIO"+idusuario, Toast.LENGTH_SHORT).show()
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
                }
            }
        }
    }

    private fun getSessionUsername(): String? {
        val preference = PreferenceHelper.defaultPrefs(this)
        return preference["username", null]
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

    private fun configuration() {
        recycledViewMascota.layoutManager = GridLayoutManager(this, 2)
        //Toast.makeText(applicationContext, "IDUSUARIO"+idUsuario, Toast.LENGTH_SHORT).show()
    }

    private fun obtenerListMascotas() {

        CoroutineScope(Dispatchers.IO).launch {
            val retrofitUsuario = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                .getUsuario(usuario)
            val datausuario = retrofitUsuario.body()
            if (datausuario != null) {
                miLista = datausuario.favoritos
            }
            val retrofitMascotas = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                .getMascotasFavoritos(miLista)
            val dataMascotas = retrofitMascotas.body()
            if (dataMascotas != null) {
                dataMascotas.forEach { it.favorito = true }
            }
            runOnUiThread{
                recycledViewMascota.adapter = MascotaAdapter( dataMascotas!!, miLista,
                    { it ->
                        val mascotaId = it.idmascota
                        val intent = Intent( this@activity_favoritos, activity_detalleMascota::class.java)
                            .apply {
                                putExtra("idMascota", it.idmascota)
                            }
                        startActivity(intent)
                        return@MascotaAdapter
                    },{
                        val mascotaId = it.idmascota
                        CoroutineScope(Dispatchers.IO).launch {
                            val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                                .deleteMascotaFavorito(idUsuario, mascotaId)
                            if(retrofit.isSuccessful){
                                val message = retrofit.message()
                                runOnUiThread{
                                    Toast.makeText(this@activity_favoritos, "Eliminado satisfactoriamente", Toast.LENGTH_SHORT).show()
                                    val intento = Intent(this@activity_favoritos, activity_favoritos::class.java)
                                    startActivity(intento)
                                }
                            }
                        }
                        return@MascotaAdapter
                    }
                )

            }
        }

    }
}