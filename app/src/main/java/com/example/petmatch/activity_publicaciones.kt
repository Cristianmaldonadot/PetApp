package com.example.petmatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petmatch.adapter.MascotaPubliAdapter
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


class activity_publicaciones : AppCompatActivity() {

    private lateinit var recycledViewMascota: RecyclerView
    private var idUsuario: Int = 0
    private lateinit var usuario: String
    private lateinit var rol: String
    private lateinit var navegador: BottomNavigationView
    private var listaFiltradaOriginal: List<Mascota>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicaciones)

        navegador = findViewById(R.id.navega_bar)
        navegador.selectedItemId = R.id.action_perfil

        Navegacion(this,navegador)

        recycledViewMascota = findViewById(R.id.recycle_publi_mascotas)

        usuario = getSessionUsuario()!!


        obtenerIdUsuario()
        configuration()
        obtenerListMascotas()

    }

    override fun onResume() {
        super.onResume()
        obtenerListMascotas()
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

    private fun getSessionUsuario(): String? {
        val preference = PreferenceHelper.defaultPrefs(this)
        return preference["username", null]
    }


    private fun configuration() {
        recycledViewMascota.layoutManager = LinearLayoutManager(this)
        //Toast.makeText(applicationContext, "IDUSUARIO"+idUsuario, Toast.LENGTH_SHORT).show()
    }

    private fun obtenerListMascotas() {

        CoroutineScope(Dispatchers.IO).launch {
            val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                .getListMascotasPorNombre(usuario)
            listaFiltradaOriginal = retrofit.body()
            renderizarLista()

        }
    }

    private fun renderizarLista() {
        runOnUiThread{
            recycledViewMascota.adapter = MascotaPubliAdapter( listaFiltradaOriginal!!)
                { it ->
                    val mascotaId = it.idmascota
                    val intent = Intent( this@activity_publicaciones, activity_editpublicacion::class.java)
                        .apply {
                            putExtra("idMascota", it.idmascota)
                        }
                    startActivity(intent)
                }

        }
    }


}