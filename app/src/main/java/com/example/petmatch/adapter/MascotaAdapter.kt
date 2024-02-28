package com.example.petmatch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petmatch.R
import com.example.petmatch.model.Mascota

class MascotaAdapter(private val mascotaList: List<Mascota>):
        RecyclerView.Adapter<MascotaViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MascotaViewHolder(layoutInflater.inflate(R.layout.layout_mascota, parent, false))
    }

    override fun getItemCount(): Int = mascotaList.size

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val itemMascota = mascotaList[position]
        holder.render(itemMascota)
    }
}