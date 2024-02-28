package com.example.petmatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.petmatch.io.ApiService
import com.example.petmatch.io.response.LoginResponse
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.get
import com.example.petmatch.utils.PreferenceHelper.set
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

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
        val ingresar =Intent(this,activity_menu::class.java)
        startActivity(ingresar)
        finish()

    }

    private fun createSessionPreference(token: String){
        val preference = PreferenceHelper.defaultPrefs(this)
        preference["token"] = token
        Toast.makeText(applicationContext, "TOKEN"+token, Toast.LENGTH_SHORT).show()
    }

    private fun performLogin(){
        val etUsername = findViewById<EditText>(R.id.txi_username).text.toString()
        val etPassword = findViewById<EditText>(R.id.txi_password).text.toString()

        val requestBody = mapOf(
            "username" to etUsername,
            "password" to etPassword
        )
        println(requestBody)

        val call = apiService.postLogin(requestBody)
        call.enqueue(object: Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                println("estoy en Onresponse")
                if(response.isSuccessful){
                    val loginResponse = response.body()
                    if(loginResponse == null){
                        Toast.makeText(applicationContext, "Se produjo un error en el servidor", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if(loginResponse != null){
                        createSessionPreference(loginResponse.token)
                        goToMenu()
                    }else{
                        Toast.makeText(applicationContext, "Las credenciales son incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(applicationContext, "Se produjo un error en el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Se produjo un error en el servidor xD", Toast.LENGTH_SHORT).show()
            }

        })

    }

}
