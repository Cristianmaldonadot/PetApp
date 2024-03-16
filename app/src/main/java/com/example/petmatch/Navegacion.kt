package com.example.petmatch

import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.set
import com.google.android.material.bottomnavigation.BottomNavigationView

class Navegacion(private val contexto: Context, private val navegador:BottomNavigationView) {


    init {

        navegador.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.action_inicio -> {
                    val intent = Intent(contexto, activity_listar::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(
                        contexto,
                        R.anim.slide_in_right,  // animación de entrada
                        R.anim.slide_out_left   // animación de salida
                    )
                    contexto.startActivity(intent, options.toBundle())
                    true
                }
                R.id.action_favoritos -> {
                    val intent = Intent(contexto, activity_favoritos::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(
                        contexto,
                        R.anim.slide_in_right,  // animación de entrada
                        R.anim.slide_out_left   // animación de salida
                    )
                    contexto.startActivity(intent, options.toBundle())
                    true
                }
                R.id.action_agregarpet -> {
                    val intent = Intent(contexto, activity_agregarMascota::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(
                        contexto,
                        R.anim.slide_in_right,  // animación de entrada
                        R.anim.slide_out_left   // animación de salida
                    )
                    contexto.startActivity(intent, options.toBundle())
                    true
                }
                R.id.action_perfil -> {
                    val intent = Intent(contexto, activity_perfil::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(
                        contexto,
                        R.anim.slide_in_right,  // animación de entrada
                        R.anim.slide_out_left   // animación de salida
                    )
                    contexto.startActivity(intent, options.toBundle())
                    true
                }
                R.id.action_logout -> {
                    clearSessionPreference()
                    val intent = Intent(contexto, MainActivity::class.java)
                    val options = ActivityOptionsCompat.makeCustomAnimation(
                        contexto,
                        R.anim.slide_in_right,  // animación de entrada
                        R.anim.slide_out_left   // animación de salida
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Esto limpia la pila de actividades
                    contexto.startActivity(intent, options.toBundle())
                    true
                }
                else -> false
            }
        }
    }
    private fun clearSessionPreference() {
        val preference = PreferenceHelper.defaultPrefs(contexto)
        preference["token"] = ""
    }

}