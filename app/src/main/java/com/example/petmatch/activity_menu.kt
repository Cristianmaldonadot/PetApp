package com.example.petmatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.get
import com.example.petmatch.utils.PreferenceHelper.set

class activity_menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

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