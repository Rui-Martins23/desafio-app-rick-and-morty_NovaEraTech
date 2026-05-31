package com.example.rickandmortyapi.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rickandmortyapi.model.Location
import com.example.rickandmortyapi.model.Origin

@Entity("personagens_favoritas")
data class PersonagemFavorita(
    @PrimaryKey
    val id: Int,
    val name: String,
    val image: String,
    val species: String,
    val status: String,
    @Embedded(prefix = "origin_") // para desestruturar o objeto em colunas aqui na entity/tabela
    val origin: Origin,
    @Embedded(prefix = "location_") // ex: location_name e location_url
    val location: Location
    // var isFavorito: Boolean = false
)
