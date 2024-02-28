package com.example.petmatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.petmatch.io.ApiService
import com.example.petmatch.io.response.Usuario
import com.example.petmatch.model.CreateUserDTO
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class activity_signin : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val btnRegistrarse = findViewById<Button>(R.id.btn_registrarse)
        btnRegistrarse.setOnClickListener{
            performSignIn()
        }
    }

    private fun goToMain() {
        val main = Intent(this,MainActivity::class.java)
        startActivity(main)
        finish()

    }

    private fun performSignIn() {
        val etUsername = findViewById<TextInputEditText>(R.id.et_registrarse_usuario).text.toString()
        val etEmail = findViewById<TextInputEditText>(R.id.et_registrarse_email).text.toString()
        val etPassword = findViewById<TextInputEditText>(R.id.et_registrarse_password).text.toString()
        val etCelular = findViewById<TextInputEditText>(R.id.et_registrarse_celular).text.toString()
        val roles = listOf("USER")

        val requestBody = CreateUserDTO(
            etUsername,
            etEmail,
            etPassword,
            roles,
            etCelular
        )

        val call = apiService.postSignIn(requestBody)
        call.enqueue(object: Callback<Usuario>{
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
               if(response.isSuccessful){
                    val usuario = response.body()
                   if(usuario == null){
                       Toast.makeText(applicationContext, "Se produjo un error en el servidor", Toast.LENGTH_SHORT).show()
                       return
                   }
                   if(usuario != null){
                       Toast.makeText(applicationContext, "Ok, Usuario Registrado", Toast.LENGTH_SHORT).show()
                       goToMain()
                   }else{
                       Toast.makeText(applicationContext, "Las credenciales son incorrectas", Toast.LENGTH_SHORT).show()
                   }
               }else{
                Toast.makeText(applicationContext, "Se produjo un error en el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(applicationContext, "Se produjo un error en el servidor xD", Toast.LENGTH_SHORT).show()
            }

        })
    }
}