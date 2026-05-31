package com.example.rickandmortyapi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rickandmortyapi.data.dao.PersonagemDAO
import com.example.rickandmortyapi.entities.PersonagemFavorita
import com.example.rickandmortyapi.model.Personagem

@Database(
    entities = [PersonagemFavorita::class],
    version = 1
)
abstract class BancoDados: RoomDatabase() {

    abstract fun recuperarPersonagemDAO(): PersonagemDAO

    companion object {
        private var INSTANCE: BancoDados? = null

        fun recuperarInstanciaRoom(context: Context): BancoDados {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    BancoDados::class.java,
                    "projeto.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}