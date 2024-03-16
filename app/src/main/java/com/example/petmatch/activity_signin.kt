package com.example.petmatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.petmatch.proxy.interfaces.ApiService
import com.example.petmatch.proxy.response.Usuario
import com.example.petmatch.model.CreateUserDTO
import com.example.petmatch.proxy.retrofit.MascotaRetrofit
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class activity_signin : AppCompatActivity() {

    private lateinit var etUsername:TextInputEditText
    private lateinit var etEmail:TextInputEditText
    private lateinit var etPassword:TextInputEditText
    private lateinit var etCelular:TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val btnRegistrarse = findViewById<Button>(R.id.btn_registrarse)
        btnRegistrarse.setOnClickListener{
            performSignIn()
        }

        config()
    }

    private fun config() {
        etUsername = findViewById(R.id.et_registrarse_usuario)
        etEmail = findViewById(R.id.et_registrarse_email)
        etPassword = findViewById(R.id.et_registrarse_password)
        etCelular = findViewById(R.id.et_registrarse_celular)
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if(etUsername.text.toString().isBlank() || etEmail.text.toString().isBlank() || etPassword.text.toString().isBlank() ||
            etCelular.text.toString().isBlank() ){
            isValid = false
            Toast.makeText(this@activity_signin, "LLene todos los campos", Toast.LENGTH_LONG).show()
        }

        return isValid
    }

    private fun goToMain() {
        val main = Intent(this,MainActivity::class.java)
        startActivity(main)
        finish()

    }

    private fun performSignIn() {
        if (validateForm()) {
            val username = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val celular = etCelular.text.toString()

            val roles = listOf("USER")

            val requestBody = CreateUserDTO(
                username,
                email,
                password,
                roles,
                celular
            )

            CoroutineScope(Dispatchers.IO).launch {
                val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                    .postSignIn(requestBody)
                val data = retrofit.body()
                runOnUiThread {
                    Toast.makeText(
                        this@activity_signin,
                        "Usuario agregado correctamente",
                        Toast.LENGTH_LONG
                    ).show()
                    val irAlLogin = Intent(this@activity_signin, MainActivity::class.java)
                    startActivity(irAlLogin)
                }
            }


        }
    }
}