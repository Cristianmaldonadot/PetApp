package com.example.petmatch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petmatch.R
import com.example.petmatch.model.Favorito
import com.example.petmatch.model.Mascota

class MascotaAdapter(
    private val mascotaList: List<Mascota>,
    private val favoritoList: List<Favorito>,
    private val onFavoritiesButtonClick: (Mascota) -> Unit,
    private val onDetallesButtonClick: (Mascota) -> Unit
):
        RecyclerView.Adapter<MascotaViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_mascota2, parent, false)
        return MascotaViewHolder(view)
    }

    override fun getItemCount(): Int = mascotaList.size

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val itemMascota = mascotaList[position]
        val itemsUsuario = favoritoList
        holder.render(itemMascota, itemsUsuario,  onFavoritiesButtonClick, onDetallesButtonClick)
    }

}