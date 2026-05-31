package com.example.rickandmortyapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.rickandmortyapi.R
import com.example.rickandmortyapi.api.RetrofitService
import com.example.rickandmortyapi.model.Personagem

class PersonagemAdapter(
    val onClick: (Personagem) -> Unit,
    val onFavoritoClick: (Personagem) -> Unit
): RecyclerView.Adapter<PersonagemAdapter.PersonagemViewHolder>() {
    private var listaPersonagens = mutableListOf<Personagem>()

    fun adicionarListaAtual(listaFiltrada: List<Personagem>) {
        this.listaPersonagens.clear()
        this.listaPersonagens.addAll(listaFiltrada)
        notifyDataSetChanged()
    }

    fun atualizarItem(personagem: Personagem) {
        val index = listaPersonagens.indexOfFirst { it.id == personagem.id }
        if (index != -1) { // Verifica se personagem existe
            listaPersonagens[index] = personagem
            notifyItemChanged(index) // Atualiza apenas o item, ao invés de toda a lista
        }
    }

    inner class PersonagemViewHolder(
        val cardView: View
    ): RecyclerView.ViewHolder(cardView) {
        val imgPersonagem = cardView.findViewById<ImageView>(R.id.img_personagem)
        val textNome = cardView.findViewById<TextView>(R.id.tv_nome_personagem)
        val textStatus = cardView.findViewById<TextView>(R.id.tv_estado_personagem)
        val textEspecie = cardView.findViewById<TextView>(R.id.tv_especie_personagem)
        val textEndereco = cardView.findViewById<TextView>(R.id.tv_endereco_personagem)
        val cardViewItem = cardView.findViewById<ConstraintLayout>(R.id.clItemPersonagem)
        val imgCoracao = cardView.findViewById<ImageView>(R.id.imgCoracao)

        fun bind(personagem: Personagem) {
            textNome.text = personagem.name
            textStatus.text = personagem.status
            textEspecie.text = personagem.species
            textEndereco.text = personagem.location.name

            if (personagem.isFavorito) {
                imgCoracao.setImageResource(R.drawable.ic_heart_filled) // ícone do coração preenchido
            } else {
                imgCoracao.setImageResource(R.drawable.ic_heart) // ícone original, de coração vazio
            }

            // Utilizando Coil Image
            imgPersonagem.load(personagem.image) {
                crossfade(true)
            }

            // Lógica para passar à próxima tela
            cardViewItem.setOnClickListener {
                onClick(personagem)
            }

            imgCoracao.setOnClickListener {
                onFavoritoClick(personagem)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PersonagemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cardView = layoutInflater.inflate(
            R.layout.card_view_item,
            parent,
            false
        )

        return PersonagemViewHolder(cardView)
    }

    override fun onBindViewHolder(
        holder: PersonagemViewHolder,
        position: Int
    ) {
        val personagem = listaPersonagens[position]
        holder.bind(personagem)
    }

    override fun getItemCount(): Int {
        return listaPersonagens.size
    }
}