package com.example.rickandmortyapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.rickandmortyapi.R
import com.example.rickandmortyapi.entities.PersonagemFavorita

class FavoritoAdapter(): RecyclerView.Adapter<FavoritoAdapter.FavoritoViewHolder>() {
    private var listaFavoritos = mutableListOf<PersonagemFavorita>()

    fun adicionarLista(lista: List<PersonagemFavorita>) {
        this.listaFavoritos.addAll(lista)
        notifyDataSetChanged()
    }

    inner class FavoritoViewHolder(
        val cardView: View
    ): RecyclerView.ViewHolder(cardView) {
        val imgPersonagem = cardView.findViewById<ImageView>(R.id.img_personagem)
        val textNome = cardView.findViewById<TextView>(R.id.tv_nome_personagem)
        val textStatus = cardView.findViewById<TextView>(R.id.tv_estado_personagem)
        val textEspecie = cardView.findViewById<TextView>(R.id.tv_especie_personagem)
        val imgCoracao = cardView.findViewById<ImageView>(R.id.imgCoracao)

        fun bind(favorito: PersonagemFavorita) {
            textNome.text = favorito.name
            textStatus.text = favorito.status
            textEspecie.text = favorito.species
            imgCoracao.visibility = View.GONE // ou View.INVISIBLE

            // Utilizando Coil Image
            imgPersonagem.load(favorito.image) {
                crossfade(true)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cardView = layoutInflater.inflate(
            R.layout.card_view_item,
            parent,
            false
        )

        return FavoritoViewHolder(cardView)
    }

    override fun onBindViewHolder(
        holder: FavoritoViewHolder,
        position: Int
    ) {
        val favorito = listaFavoritos[position]
        holder.bind(favorito)
    }

    override fun getItemCount(): Int {
        return listaFavoritos.size
    }
}