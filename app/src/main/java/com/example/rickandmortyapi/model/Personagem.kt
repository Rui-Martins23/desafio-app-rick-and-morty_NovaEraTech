package com.example.rickandmortyapi.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Personagem(
    val id: Int,
    val gender: String,
    val image: String,
    val origin: Origin,
    val location: Location,
    val name: String,
    val species: String,
    val status: String,
    val type: String,
    val url: String,
    var isFavorito: Boolean = false
): Parcelable
