package com.example.petmatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.petmatch.model.Usuario
import com.example.petmatch.proxy.interfaces.ApiService
import com.example.petmatch.proxy.retrofit.MascotaRetrofit
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.get
import com.example.petmatch.utils.PreferenceHelper.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.create

class activity_menu : AppCompatActivity() {

    private lateinit var userName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        userName = getSessionToken().toString()

        val btnLogout = findViewById<Button>(R.id.btn_cerrar_sesion)
        btnLogout.setOnClickListener{
            clearSessionPreference()
            goToLogin()
        }

        val btnAgregarMascota = findViewById<Button>(R.id.btn_menu_agregar_mascota)
        btnAgregarMascota.setOnClickListener{
            goAgregarMascota()
        }

        val btnListarMascotas = findViewById<Button>(R.id.btn_menu_buscar_mascota)
        btnListarMascotas.setOnClickListener{
            goListarMascota()
        }

        obtenerUsuario()
    }
    private fun createUsuario(idusuario: Int){
        val preference = PreferenceHelper.defaultPrefs(this)
        preference["idusuario"] = idusuario
        Toast.makeText(applicationContext, "USUARIO"+idusuario, Toast.LENGTH_SHORT).show()
    }

    private fun obtenerUsuario() {
        CoroutineScope(Dispatchers.IO).launch {
            val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                .getUsuario(userName)
            val data = retrofit.body()
            runOnUiThread{
                if(data != null){
                    createUsuario(data!!.idusuario)
                }
            }
        }
    }


    private fun getSessionToken(): String? {
        val preference = PreferenceHelper.defaultPrefs(this)
        return preference["username", null]
    }

    private fun goListarMascota() {
        val listar = Intent(this,activity_listar::class.java)
        startActivity(listar)
    }

    private fun goAgregarMascota() {
        val agregar = Intent(this,activity_agregarMascota::class.java)
        startActivity(agregar)
    }

    private fun goToLogin() {
        val salir = Intent(this,MainActivity::class.java)
        startActivity(salir)
        finish()
    }

    private fun clearSessionPreference() {
        val preference = PreferenceHelper.defaultPrefs(this)
        preference["token"] = ""
    }


}