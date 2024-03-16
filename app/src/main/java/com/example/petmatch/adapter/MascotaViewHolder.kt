package com.example.petmatch.adapter

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petmatch.R
import com.example.petmatch.model.Mascota
import com.bumptech.glide.Glide
import com.example.petmatch.model.Favorito
import com.example.petmatch.model.Usuario
import com.example.petmatch.utils.PreferenceHelper
import com.example.petmatch.utils.PreferenceHelper.get

class MascotaViewHolder(view: View): RecyclerView.ViewHolder(view) {

    val lblNombre: TextView =view.findViewById(R.id.tv_listar_nombre)
    val lblRaza: TextView = view.findViewById(R.id.tv_listar_raza)
    val imgMascota: ImageView =view.findViewById(R.id.img_mascota)
    val lblEstado: TextView = view.findViewById(R.id.tv_listar_estado)
    val imgSexo: ImageView = view.findViewById(R.id.img_listar_sexo)
    val btnFavorities: Button = view.findViewById(R.id.btn_add_favorities)
    val ivFavorities: ImageView = view.findViewById(R.id.iv_listar_heart)
    val tvVisitas: TextView = view.findViewById(R.id.tv_listar_visitas)


    fun render(mascota:Mascota, favorito:List<Favorito>, onFavoritiesButtonClick: (Mascota) -> Unit, onDetallesButtonClick: (Mascota) -> Unit){
        lblNombre.text = mascota.nombre
        lblRaza.text = mascota.raza
        lblEstado.text = mascota.estado
        tvVisitas.text = mascota.visitas.toString()
        var toLong = mascota.idmascota.toLong()

        ivFavorities.setImageResource(R.drawable.star)
        for(elemento in favorito){
            if(elemento.idmascota == toLong){
                if(mascota.favorito){
                    ivFavorities.setImageResource(R.drawable.delete)
                }else{
                    ivFavorities.setImageResource(R.drawable.starclick)
                    ivFavorities.isEnabled = false
                }

            }
        }

        when(mascota.sexo){
            "Macho" -> imgSexo.setImageResource(R.drawable.male)
            "Hembra" -> imgSexo.setImageResource(R.drawable.famele)
        }

        Glide.with(itemView.context).load(mascota.imagen).into(imgMascota)

        btnFavorities.setOnClickListener {
            onFavoritiesButtonClick(mascota)
        }
        ivFavorities.setOnClickListener {
            onDetallesButtonClick(mascota)
            ivFavorities.isEnabled = false
            if(mascota.favorito){
                ivFavorities.setImageResource(R.drawable.deleteselect)
            }else{
                ivFavorities.setImageResource(R.drawable.starclick)
            }


        }
    }

}