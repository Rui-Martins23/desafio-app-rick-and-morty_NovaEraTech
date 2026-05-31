package com.example.rickandmortyapi.api

import com.example.rickandmortyapi.model.Personagem
import com.example.rickandmortyapi.model.PersonagensResposta
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PersonagemAPI {
    @GET("character")
    suspend fun recuperarPersonagens(
        @Query("page") pagina: Int
    ): Response<PersonagensResposta>

    @GET("character/{id}")
    suspend fun recuperarPersonagem(
        @Path("id") id: Int
    ): Response<Personagem>
}