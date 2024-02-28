package com.example.petmatch.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petmatch.R
import com.example.petmatch.model.Mascota
import com.bumptech.glide.Glide

class MascotaViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val lblNombre: TextView =view.findViewById(R.id.tv_listar_nombre)
    val lblRaza: TextView = view.findViewById(R.id.tv_listar_raza)
    val imgMascota: ImageView =view.findViewById(R.id.img_mascota)

    fun render(mascota:Mascota){
        lblNombre.text = mascota.nombre
        lblRaza.text = "Raza : "+mascota.raza

        Glide.with(itemView.context).load(mascota.imagen).into(imgMascota)

    }
}