package com.example.rickandmortyapi

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.rickandmortyapi.model.Personagem

class DetalhePersonagemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalhe_personagem)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imgPersonagem = findViewById<ImageView>(R.id.img_character)
        val tagEstado = findViewById<TextView>(R.id.tv_tag_estado)
        val tagEspecie = findViewById<TextView>(R.id.tv_tag_especie)
        val tagGenero = findViewById<TextView>(R.id.tv_tag_genero)
        val tvNome = findViewById<TextView>(R.id.tv_nome)
        val tvEstado = findViewById<TextView>(R.id.tv_status)
        val tvEspecie = findViewById<TextView>(R.id.tv_species)
        val tvGenero = findViewById<TextView>(R.id.tv_gender)
        val tvOrigem = findViewById<TextView>(R.id.tv_origin)
        val tvLocalizacao = findViewById<TextView>(R.id.tv_location)
        val tvNomePersonagemToolbar = findViewById<TextView>(R.id.tv_toolbar)

        val bundle = intent.extras

        if (bundle != null) {
            val personagemDetalhes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable("personagem", Personagem::class.java)
            } else {
                bundle.getParcelable("personagem") as? Personagem
            }

            if (personagemDetalhes != null) {
                tagEstado.text = personagemDetalhes.status
                tagEspecie.text = personagemDetalhes.species
                tagGenero.text = personagemDetalhes.gender
                tvNome.text = personagemDetalhes.name
                tvEstado.text = personagemDetalhes.status
                tvEspecie.text = personagemDetalhes.species
                tvGenero.text = personagemDetalhes.gender
                tvOrigem.text = personagemDetalhes.origin.name
                tvLocalizacao.text = personagemDetalhes.location.name
                tvNomePersonagemToolbar.text = personagemDetalhes.name

                // Utilizando Coil Image
                imgPersonagem.load(personagemDetalhes.image) {
                    crossfade(true)
                }
            }
        }

        // Implementação da ToolBar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Remove o título padrão da Toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}