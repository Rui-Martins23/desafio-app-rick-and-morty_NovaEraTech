package com.example.rickandmortyapi

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapi.adapter.FavoritoAdapter
import com.example.rickandmortyapi.adapter.PersonagemAdapter
import com.example.rickandmortyapi.data.BancoDados
import com.example.rickandmortyapi.data.dao.PersonagemDAO
import com.example.rickandmortyapi.entities.PersonagemFavorita
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritosPersonagensActivity : AppCompatActivity() {

    private lateinit var rvFavoritos: RecyclerView
    private lateinit var favoritoAdapter: FavoritoAdapter
    private lateinit var bancoDados: BancoDados
    private lateinit var personagemDAO: PersonagemDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favoritos_personagens)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bancoDados = BancoDados.recuperarInstanciaRoom(this)
        personagemDAO = bancoDados.recuperarPersonagemDAO()


        // Para colocar as mesmas cores e gradientes do título da Main Activity
        val tituloFavoritos = findViewById<TextView>(R.id.tv_title_favoritos)

        val corInicial = "#1B232D".toColorInt() // Cinza escuro/Topo
        val corFinal = "#2E6B4F".toColorInt() // Verde esmeralda

        val paint = tituloFavoritos.paint
        val widthTitulo = paint.measureText(tituloFavoritos.text.toString())

        val gradienteTitulo = LinearGradient(
            0f, 0f, widthTitulo, 0f,
            intArrayOf(corInicial, corFinal),
            null,
            Shader.TileMode.CLAMP
        )

        paint.shader = gradienteTitulo

        // Chamada da função que renderiza os dados no Recycler View das personagens favoritas
        inicializarViewsFavoritos()

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

    private fun inicializarViewsFavoritos() {
        rvFavoritos = findViewById(R.id.rvFavoritos)
        favoritoAdapter = FavoritoAdapter()

        rvFavoritos.adapter = favoritoAdapter

        rvFavoritos.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(
            applicationContext,
            mensagem,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onStart() { // local onde faço a chamada ao Banco de Dados ??
        super.onStart()
        recuperarFavoritosFromBancoDados()
    }

    private fun recuperarFavoritosFromBancoDados() {
        // Lógica de recuperação das personagens favoritas guardadas no banco de dados
        CoroutineScope(Dispatchers.IO).launch {
            var respostaBancoDados: List<PersonagemFavorita>? = null

            try {
                respostaBancoDados = personagemDAO.listarPersonagens() // retorna uma List<PersonagemFavorita>
            } catch (e: Exception) {
                Log.i("info_db", "Erro no carregamento dos personagens favoritos")
            }

            if (respostaBancoDados != null && respostaBancoDados.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    favoritoAdapter.adicionarLista(respostaBancoDados)
                }
            } else {
                withContext(Dispatchers.Main) {
                    exibirMensagem("Nenhuma personagem adicionada aos Favoritos")
                }
            }
        }
    }

    /*private fun removerPersonagemFavorito(favorita: PersonagemFavorita) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val idPersonagem = personagemDAO.removerPersonagens(favorita.id)

                withContext(Dispatchers.Main) {
                    if (idPersonagem > 0) {
                        // favorita.isFavorito = false
                        favoritoAdapter.atualizarItem(favorita)
                        exibirMensagem("Personagem removida dos Favoritos")
                    } else {
                        exibirMensagem("Erro ao remover personagem dos Favoritos")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    exibirMensagem("Erro ao remover personagem dos Favoritos: ${e.message}")
                }
            }
        }
    }*/
}