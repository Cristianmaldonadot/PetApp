package com.example.petmatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.utils.widget.MotionButton
import com.example.petmatch.model.UpdateUserDTO
import com.example.petmatch.model.Usuario
import com.example.petmatch.proxy.interfaces.ApiService
import com.example.petmatch.proxy.retrofit.MascotaRetrofit
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.create

class activity_perfil : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etNombre: TextInputEditText
    private lateinit var etApellido: TextInputEditText
    private lateinit var etCelular: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etDireccion: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var usuario: String
    private var idUsuario: Int = 0
    private lateinit var btnGuardar: MotionButton
    private lateinit var navegador: BottomNavigationView
    private lateinit var btnEditPass: ImageView
    private lateinit var rol: String
    private lateinit var btnPubli:MotionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        navegador = findViewById(R.id.navega_bar)
        navegador.selectedItemId = R.id.action_perfil

        Navegacion(this,navegador)

        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDiff = rootView.rootView.height - rootView.height
            if (heightDiff > 500) { // Si la diferencia de altura es mayor a 100 píxeles, el teclado está visible
                navegador.visibility = View.GONE
            } else {
                navegador.visibility = View.VISIBLE
            }
        }

        etUsername = findViewById(R.id.et_perfil_username)
        etNombre = findViewById(R.id.et_perfil_nombre)
        etApellido = findViewById(R.id.et_perfil_apellido)
        etCelular = findViewById(R.id.et_perfil_celular)
        etEmail = findViewById(R.id.et_perfil_email)
        etDireccion = findViewById(R.id.et_perfil_direccion)
        etPassword = findViewById(R.id.et_perfil_password)
        btnGuardar = findViewById(R.id.btn_guardar)
        btnEditPass = findViewById(R.id.btn_perfil_edit)
        btnPubli = findViewById(R.id.btn_btnpubli)

        btnEditPass.setOnClickListener{
            val editarPass = Intent(this, activity_editpass::class.java)
            startActivity(editarPass)
        }
        btnPubli.setOnClickListener {
            val verPubli = Intent(this,activity_publicaciones::class.java)
            startActivity(verPubli)
        }

        usuario = getSessionUsername()!!
        rol = getSessionRol()!!

        verMenu()
        obtenerData()
        actualizarUsuario()
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

    private fun actualizarUsuario() {
        btnGuardar.setOnClickListener{
            val usuario = UpdateUserDTO(
                etCelular.text.toString().trim(),
                etEmail.text.toString().trim(),
                etNombre.text.toString().trim(),
                etApellido.text.toString().trim(),
                etDireccion.text.toString().trim()
            )

            CoroutineScope(Dispatchers.IO).launch {
                val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                    .postUpdateUser(idUsuario, usuario)
                val datanew =retrofit.body()
                    runOnUiThread{
                        Toast.makeText(this@activity_perfil,"Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                    }
            }

        }
    }

    private fun getSessionUsername(): String? {
        val preference = PreferenceHelper.defaultPrefs(this)
        return preference["username", null]
    }

    private fun obtenerData() {
        CoroutineScope(Dispatchers.IO).launch {
            val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                .getUsuario(usuario)
            val data = retrofit.body()
            runOnUiThread{
                if(retrofit.isSuccessful){
                    idUsuario = data!!.idusuario
                    etUsername.setText(data!!.username)
                    etNombre.setText(data!!.nombre)
                    etApellido.setText(data!!.appaterno)
                    etCelular.setText(data!!.celular)
                    etEmail.setText(data!!.email)
                    etDireccion.setText(data!!.direccion)
                    etPassword.setText("·········")
                }
            }
        }
    }
}