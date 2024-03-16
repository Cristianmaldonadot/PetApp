package com.example.petmatch.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petmatch.R
import com.example.petmatch.model.Mascota

class MascotaPubliViewHolder(view:View, position: (Int) -> Unit):RecyclerView.ViewHolder(view) {

    val lblNombre: TextView =view.findViewById(R.id.tv_publi_nombre)
    val lblRaza: TextView = view.findViewById(R.id.tv_publi_raza)
    val lblEstado: TextView = view.findViewById(R.id.tv_publi_estado)
    val lblSexo: TextView = view.findViewById(R.id.tv_publi_sexo)
    val imgMascota: ImageView =view.findViewById(R.id.img_publi_mascota)

    init {
        itemView.setOnClickListener{
            position(adapterPosition)
        }
    }


    fun render(mascota: Mascota) {
        lblNombre.text = mascota.nombre
        lblRaza.text = mascota.raza
        lblEstado.text = mascota.estado
        lblSexo.text = mascota.sexo
        Glide.with(itemView.context).load(mascota.imagen).into(imgMascota)
    }

}