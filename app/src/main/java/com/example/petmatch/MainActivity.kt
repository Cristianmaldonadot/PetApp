package com.example.petmatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.petmatch.proxy.interfaces.ApiService
import com.example.petmatch.proxy.response.LoginResponse
import com.example.petmatch.proxy.retrofit.MascotaRetrofit
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.get
import com.example.petmatch.utils.PreferenceHelper.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.net.SocketTimeoutException

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = PreferenceHelper.defaultPrefs(this)
        if(preferences["token",""].contains("."))
            goToMenu()

        val btnMenu = findViewById<Button>(R.id.btn_ingresar)
        btnMenu.setOnClickListener{
            performLogin()
        }
        val btnRegistrarse = findViewById<TextView>(R.id.lbl_registrarse)
        btnRegistrarse.setOnClickListener{
            goSignIn()
        }
    }

    private fun goSignIn(){
        val registrarse =Intent(this,activity_signin::class.java)
        startActivity(registrarse)
    }

    private fun goToMenu() {
        val ingresar =Intent(this,activity_listar::class.java)
        startActivity(ingresar)
        finish()

    }

    private fun createSessionPreference(token: String){
        val preference = PreferenceHelper.defaultPrefs(this)
        preference["token"] = token
        //Toast.makeText(applicationContext, "TOKEN"+token, Toast.LENGTH_SHORT).show()
    }

    private fun performLogin() {
        val etUsername = findViewById<EditText>(R.id.txi_username).text.toString()
        val etPassword = findViewById<EditText>(R.id.txi_password).text.toString()

        val requestBody = mapOf(
            "username" to etUsername,
            "password" to etPassword
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                    .postLogin(requestBody)
                val loginResponse = retrofit.body()
                runOnUiThread {
                    if (retrofit.isSuccessful) {
                        if (loginResponse == null) {
                            Toast.makeText(applicationContext, "Se produjo un error en el servidor", Toast.LENGTH_SHORT).show()
                            return@runOnUiThread
                        }
                        createSessionPreference(loginResponse.token)
                        guardarUsuario(loginResponse.Username)
                        goToMenu()
                    } else {
                        Toast.makeText(applicationContext, "Usuario y/o Password incorrecto", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SocketTimeoutException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Tiempo de espera agotado al conectar con el servidor", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Error al conectar con el servidor: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun guardarUsuario(username: String) {
        val preference = PreferenceHelper.defaultPrefs(this)
        preference["username"] = username

    }

}
