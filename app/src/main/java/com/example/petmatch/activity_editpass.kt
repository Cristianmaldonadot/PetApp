package com.example.petmatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.utils.widget.MotionButton
import com.example.petmatch.proxy.interfaces.ApiService
import com.example.petmatch.proxy.retrofit.MascotaRetrofit
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.get
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class activity_editpass : AppCompatActivity() {

    private lateinit var btnGuardar: MotionButton
    private lateinit var etContraOld: TextInputEditText
    private lateinit var etContraNew: TextInputEditText
    private lateinit var btnVer: ImageView
    private lateinit var btnVerAct: ImageView
    private var idUsuario: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editpass)

        btnGuardar = findViewById(R.id.btn_perfil_guardar)
        etContraOld = findViewById(R.id.et_editpass_contraact)
        etContraNew = findViewById(R.id.et_editpass_contranue)
        btnVer = findViewById(R.id.img_editpass_ver)
        btnVerAct = findViewById(R.id.img_editpass_veract)

        idUsuario = getIdUsuario()

        var isPasswordVisibleOld = false
        var isPasswordVisibleNew = false

        actualizarContrasenia()
        btnVer.setOnClickListener{
            isPasswordVisibleNew = !isPasswordVisibleNew
            if (isPasswordVisibleNew) {
                etContraNew.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etContraNew.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
        btnVerAct.setOnClickListener{
            isPasswordVisibleOld = !isPasswordVisibleOld
            if (isPasswordVisibleOld) {
                etContraOld.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                etContraOld.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }


    }

    private fun getIdUsuario(): Int {
        val preference = PreferenceHelper.defaultPrefs(this)
        return preference["idusuario", null]

    }

    private fun actualizarContrasenia() {
        btnGuardar.setOnClickListener {
            val passOld = etContraOld.text.toString().trim()
            val passNew = etContraNew.text.toString().trim()

            val passOldInt: Int = passOld.toInt()
            val passNewInt: Int = passNew.toInt()
            CoroutineScope(Dispatchers.IO).launch {
                val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                    .postCambiarContra(idUsuario, passOldInt, passNewInt)
                if(retrofit.isSuccessful){
                    val message = retrofit.message()
                    runOnUiThread{
                        Toast.makeText(this@activity_editpass, "Contraseña actualizada satisfactoriamente", Toast.LENGTH_SHORT).show()
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }
    }
}