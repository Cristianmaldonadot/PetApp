package com.example.petmatch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petmatch.R
import com.example.petmatch.model.Favorito
import com.example.petmatch.model.Mascota

class MascotaPubliAdapter(
    private val mascotaList: List<Mascota>,
    private val clickListener: (Mascota) -> Unit
):
    RecyclerView.Adapter<MascotaPubliViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaPubliViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val vh = MascotaPubliViewHolder(layoutInflater.inflate(R.layout.layout_mascotapubli, parent,false)){
            clickListener(mascotaList[it])
        }
        return vh
    }

    override fun getItemCount(): Int = mascotaList.size

    override fun onBindViewHolder(holder: MascotaPubliViewHolder, position: Int) {
        val itemMascota = mascotaList[position]
        holder.render(itemMascota)
    }

}