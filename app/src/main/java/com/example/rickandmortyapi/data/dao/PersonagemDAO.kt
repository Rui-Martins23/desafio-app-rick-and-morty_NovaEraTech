package com.example.rickandmortyapi.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.rickandmortyapi.entities.PersonagemFavorita

@Dao
interface PersonagemDAO {
    @Insert
    suspend fun salvarPersonagem(personagemFavorita: PersonagemFavorita): Long

    @Query("SELECT * FROM personagens_favoritas")
    suspend fun listarPersonagens(): List<PersonagemFavorita>

    @Query("DELETE FROM personagens_favoritas WHERE id = :idPersonagem")
    suspend fun removerPersonagens(idPersonagem: Int): Int
}