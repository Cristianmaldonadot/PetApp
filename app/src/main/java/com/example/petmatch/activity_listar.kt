package com.example.petmatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petmatch.adapter.MascotaAdapter
import com.example.petmatch.io.ApiService
import com.example.petmatch.model.Mascota
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class activity_listar : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    private var listaMascotasfinal = mutableListOf<Mascota>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar)
        obtenerListMascotas()
    }

    private fun obtenerListMascotas() {
        val call = apiService.postListMascotas()
        call.enqueue(object: Callback<List<Mascota>>{
            override fun onResponse(call: Call<List<Mascota>>, response: Response<List<Mascota>>) {
                if(response.isSuccessful){
                    val listMascotas = response.body()
                    if(listMascotas == null){
                        Toast.makeText(applicationContext, "La respuesta es nula", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if(listMascotas != null){
                        listaMascotasfinal.addAll(listMascotas)
                        ejecutarSiguienteBloque()
                    }else{
                        Toast.makeText(applicationContext, "Las credenciales son incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(applicationContext, "Se produjo un error en el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Mascota>>, t: Throwable) {
                Toast.makeText(applicationContext, "Se produjo un error en el servidor xD", Toast.LENGTH_SHORT).show()
            }

        })
    }
    private fun ejecutarSiguienteBloque() {
        // Aquí puedes colocar el código que deseas ejecutar después de recibir la respuesta
        println("Estas son las mascotas : "+ listaMascotasfinal)
        // Configuramos el RecyclerView con la lista de mascotas
        val recycledviewMascota: RecyclerView =findViewById(R.id.recycle_mascotas)
        recycledviewMascota.layoutManager = LinearLayoutManager(this@activity_listar)
        recycledviewMascota.adapter = MascotaAdapter(listaMascotasfinal)
    }
}