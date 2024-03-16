package com.example.petmatch

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.utils.widget.MotionButton
import com.bumptech.glide.Glide
import com.example.petmatch.model.Mascota
import com.example.petmatch.proxy.interfaces.ApiService
import com.example.petmatch.proxy.retrofit.MascotaRetrofit
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URLEncoder

class activity_editpublicacion : AppCompatActivity() {

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if(uri !=null){
            ivImagen.setImageURI(uri)
            imageUri = uri
        }
    }


    lateinit var btnCargarImage: Button
    lateinit var ivImagen: ImageView
    lateinit var imageUri: Uri
    private lateinit var etNombre: TextInputEditText
    private lateinit var etRaza: TextInputEditText
    private lateinit var etColor: TextInputEditText
    private lateinit var etEdad: TextInputEditText
    private lateinit var etEstado: TextInputEditText
    private lateinit var spnSexo: Spinner
    private lateinit var spnTamanio: Spinner
    private lateinit var spnTipo: Spinner
    private var indexSexo: String? = null
    private var indexTamanio: String? = null
    private var indexTipo: String? = null
    private lateinit var btnEliminar: MotionButton
    private lateinit var btnActualizar: MotionButton
    private lateinit var navegador: BottomNavigationView
    private var idMascota: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editpublicacion)

        navegador = findViewById(R.id.navega_bar)
        navegador.selectedItemId = R.id.action_perfil

        Navegacion(this,navegador)

        btnCargarImage = findViewById(R.id.btn_update_btnimagen)
        ivImagen = findViewById(R.id.iv_mostrar_imagen)

        btnCargarImage.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnActualizar = findViewById(R.id.btn_update_mascota)
        btnActualizar.setOnClickListener{
            //val archivo = obtenerArchivoDesdeUri(imageUri)
            //if (archivo != null) {
                actualizarPublicacion()
           // }
        }

        configuracion()
        obtenerData()
        crearSpinners()
        eliminarPublicacion()
    }

    private fun obtenerArchivoDesdeUri(uri: Uri): File? {
        val numeroAleatorio = generarNumeroAleatorio(584,1869)
        val inputStream = contentResolver.openInputStream(uri)
        val archivo = File(cacheDir, "imagen_${numeroAleatorio}.jpg")
        inputStream?.use { input ->
            archivo.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return archivo
    }

    private fun crearSpinners() {
        spnSexo = findViewById(R.id.spn_update_sexo)
        val opcionesSexo = resources.getStringArray(R.array.spnSexo)
        val adapterSexo = ArrayAdapter(this,R.layout.custom_spinner_dropdown_item, opcionesSexo)
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnSexo.adapter = adapterSexo

        spnSexo.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                indexSexo = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        spnTamanio = findViewById(R.id.spn_update_tamanio)
        val opcionesTamanio = resources.getStringArray(R.array.spnTamanio)
        val adapterTamanio = ArrayAdapter(this,R.layout.custom_spinner_dropdown_item, opcionesTamanio)
        adapterTamanio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTamanio.adapter = adapterTamanio

        spnTamanio.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                indexTamanio = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        spnTipo = findViewById(R.id.spn_update_tipo)
        val opcionesTipo = resources.getStringArray(R.array.spnTipo)
        val adapterTipo = ArrayAdapter(this,R.layout.custom_spinner_dropdown_item, opcionesTipo)
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTipo.adapter = adapterTipo

        spnTipo.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                indexTipo = p0?.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

    }


    private fun configuracion() {
        etNombre = findViewById(R.id.et_update_nombre)
        etRaza = findViewById(R.id.et_update_raza)
        etColor = findViewById(R.id.et_update_color)
        etEdad = findViewById(R.id.et_update_edad)
        etEstado = findViewById(R.id.et_update_estado)
        btnEliminar = findViewById(R.id.btn_update_eliminar)
        btnActualizar = findViewById(R.id.btn_update_mascota)
    }

    private fun obtenerData() {
        val extras =intent.extras
        if(extras != null){
            idMascota =extras.getInt("idMascota")
            CoroutineScope(Dispatchers.IO).launch {
                val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                    .getMascota(idMascota)
                val data = retrofit.body()
                runOnUiThread{
                    if(retrofit.isSuccessful){
                        etNombre.setText(data!!.nombre)
                        etRaza.setText(data!!.raza)
                        etColor.setText(data!!.color)
                        etEdad.setText(data!!.edad.toString())
                        etEstado.setText(data!!.estado)
                        val positionSexo  = resources.getStringArray(R.array.spnSexo).indexOf(data!!.sexo)
                        spnSexo.setSelection(positionSexo)
                        val positionTamanio  = resources.getStringArray(R.array.spnTamanio).indexOf(data!!.tamanio)
                        spnTamanio.setSelection(positionTamanio)
                        val positionTipo  = resources.getStringArray(R.array.spnTipo).indexOf(data!!.tipo)
                        spnTipo.setSelection(positionTipo)


                    }
                }
            }

        }
    }

    private fun actualizarPublicacion() {

                var id = idMascota
                val nombre = etNombre.text.toString().trim()
                val raza = etRaza.text.toString().trim()
                val color = etColor.text.toString().trim()
                val edad = etEdad.text.toString().trim()
                val estado = etEstado.text.toString().trim()
                val sexo = indexSexo
                val tamanio = indexTamanio
                val tipo = indexTipo
                val edadInt = edad.toInt()

                /*val tipoContenido = "image/jpeg".toMediaTypeOrNull()
                val cuerpoArchivo = archivo.asRequestBody(tipoContenido)

                val imagenParte = MultipartBody.Part.createFormData("file", archivo.name, cuerpoArchivo)*/

                val alertDialog = AlertDialog.Builder(this)

                alertDialog.setMessage("¿Estas seguro que quieres actualizar el publicación ${idMascota}?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Si",
                        DialogInterface.OnClickListener{ dialogInterface, i ->

                            CoroutineScope(Dispatchers.IO).launch {
                                val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                                    .updateMascota(
                                        id,
                                        nombre,
                                        raza,
                                        color,
                                        edadInt,
                                        0.0,
                                        0.0,
                                        1,
                                        estado,
                                        sexo,
                                        tamanio,
                                        tipo
                                    )
                                val message = retrofit.message()
                                runOnUiThread{
                                    Toast.makeText(this@activity_editpublicacion,"Car actualizado correctamente", Toast.LENGTH_LONG).show()
                                    val irLista = Intent(this@activity_editpublicacion, activity_publicaciones::class.java)
                                    startActivity(irLista)
                                }

                            }
                        }
                    )
                    .setNegativeButton("No",
                        DialogInterface.OnClickListener{dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                alertDialog.create().show()

    }

    private fun eliminarPublicacion() {
        btnEliminar.setOnClickListener{
            val extras = intent.extras
            if(extras!=null){

                val MascotaId = extras.getInt("idMascota")
                val alertDialog = AlertDialog.Builder(this)

                alertDialog.setMessage("¿Estas seguro que quieres eliminar el Carro con ID ${MascotaId}?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Si",
                        DialogInterface.OnClickListener{ dialogInterface, i ->

                            CoroutineScope(Dispatchers.IO).launch {
                                val retrofit = MascotaRetrofit.getRetrofit().create(ApiService::class.java)
                                    .deleteMascota(MascotaId)
                                val message = retrofit.message()

                                runOnUiThread{
                                    val intent = Intent(this@activity_editpublicacion, MainActivity::class.java)
                                    Toast.makeText(this@activity_editpublicacion, "Publicacion eliminada satisfactorimente", Toast.LENGTH_SHORT).show()

                                    startActivity(intent)
                                }
                            }
                        }
                    )
                    .setNegativeButton("No",
                        DialogInterface.OnClickListener{ dialogInterface, i ->
                            dialogInterface.cancel()
                        })
                alertDialog.create().show()


            }
        }
    }
    fun generarNumeroAleatorio(min: Int, max: Int): Int {
        require(min < max) { "El valor mínimo debe ser menor que el valor máximo" }
        return (min..max).random()
    }
}