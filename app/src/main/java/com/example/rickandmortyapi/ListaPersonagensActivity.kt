package com.example.rickandmortyapi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapi.adapter.PersonagemAdapter
import com.example.rickandmortyapi.api.RetrofitService
import com.example.rickandmortyapi.data.BancoDados
import com.example.rickandmortyapi.data.dao.PersonagemDAO
import com.example.rickandmortyapi.entities.PersonagemFavorita
import com.example.rickandmortyapi.model.Personagem
import com.example.rickandmortyapi.model.PersonagensResposta
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ListaPersonagensActivity : AppCompatActivity() {
    private var paginaAtual = 1
    private lateinit var rvPersonagens: RecyclerView
    private lateinit var personagemAdapter: PersonagemAdapter

    // Componentes de interface
    private lateinit var progressBar: ProgressBar
    private lateinit var cgStatus: ChipGroup
    private lateinit var cgGenero: ChipGroup
    private lateinit var edtTextBuscarPersonagem: TextInputEditText

    // Variáveis para a filtragem em memória dos personagens
    private val listaPersonagensParaApresentar = mutableListOf<Personagem>()
    private var statusAtual: String? = null
    private var generoAtual: String? = null
    private var nomeAtual: String? = null


    // Variável para chamada ao API
    private val personagensAPI by lazy {
        RetrofitService.personagensAPI
    }

    var jobPersonagens: Job? = null

    private lateinit var bancoDados: BancoDados
    private lateinit var personagemDAO: PersonagemDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_personagens)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progressBar = findViewById(R.id.progressBar)
        cgStatus = findViewById(R.id.cg_status)
        cgGenero = findViewById(R.id.cg_genero)
        edtTextBuscarPersonagem = findViewById(R.id.edt_text_buscar_personagem)

        val btnCoracaoFavoritos = findViewById<ImageButton>(R.id.btn_coracao_topbar)

        btnCoracaoFavoritos.setOnClickListener {
            val intent = Intent(this, FavoritosPersonagensActivity::class.java)
            startActivity(intent)
        }

        bancoDados = BancoDados.recuperarInstanciaRoom(this)

        personagemDAO = bancoDados.recuperarPersonagemDAO()

        inicializarAdapterViews()
        // listeners dos Chips, que atualiza o estado das variáveis cgStatus e cgGenero e chama a função aplicarFiltros
        configurarFiltros()

        // Listener para Enter do eddText de Busca de Personagem
        edtTextBuscarPersonagem.setOnEditorActionListener { v, actionId, event ->
            val textoDigitado = edtTextBuscarPersonagem.text.toString().trim()

            // Se o utilizador escreveu algo guardamos o texto, caso contrário limpa-se o filtro de nome
            nomeAtual = if (textoDigitado.isNotEmpty()) textoDigitado.lowercase() else null

            // Aplica-se todos os filtros em conjunto (Status + Género + Nome)
            aplicarFiltros()

            true // Indica que o evento foi processado
        }

        // Chamada à API
        recuperarPersonagens(paginaAtual)
    }

    private fun inicializarAdapterViews() {
        rvPersonagens = findViewById(R.id.rvPersonagens)
        personagemAdapter = PersonagemAdapter(
            { personagem ->
                val intent = Intent(this, DetalhePersonagemActivity::class.java)
                intent.putExtra("personagem", personagem)
                startActivity(intent)
            },
            { personagem ->
                if (!personagem.isFavorito) {
                    salvarPersonagemFavorito(personagem)
                } else {
                    removerPersonagemFavorito(personagem)
                }
            }
        )

        rvPersonagens.adapter = personagemAdapter

        rvPersonagens.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        rvPersonagens.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // super.onScrolled(recyclerView, dx, dy)

                val canScrollDown = recyclerView.canScrollVertically(1) // positivo significa descer no scroll

                // Verificar quando se chega ao final da lista
                if (!canScrollDown) {
                    // Carregar mais 20 itens, através da página seguinte
                    Log.i("recycler_api", "paginaAtual: $paginaAtual")
                    recuperarPersonagensProximaPagina()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()

        aplicarFiltros()
    }

    override fun onDestroy() {
        super.onDestroy()
        jobPersonagens?.cancel()
    }

    override fun onStop() {
        super.onStop()
        jobPersonagens?.cancel()
    }

    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(
            applicationContext,
            mensagem,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun recuperarPersonagensProximaPagina() {
        if (paginaAtual < 42) {
            paginaAtual++
            recuperarPersonagens(paginaAtual)
        }
    }

    private fun recuperarPersonagens(pagina: Int = 1) {
        jobPersonagens = CoroutineScope(Dispatchers.IO).launch {
            // Ativa a barra de progresso na Main Thread antes do início da chamada à API
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.VISIBLE
            }

            var resposta: Response<PersonagensResposta>? = null

            try {
                resposta = personagensAPI.recuperarPersonagens(pagina)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    exibirMensagem("Erro ao fazer requisição: ${e.message}")
                }
            }


            // Inicio aqui o processo de verificação dos personagens que estão marcados como Favoritos
            var listaProcessada: List<Personagem>? = null

            if (resposta != null && resposta.isSuccessful) {
                val listaPersonagens = resposta.body()?.results

                if (listaPersonagens != null && listaPersonagens.isNotEmpty()) {
                    try {
                        val favoritosLocais = personagemDAO.listarPersonagens() // Retorna List<PersonagemFavorita>

                        // Reduzir a lista para um set para que a pesquisa seja mais rápida
                        val idsFavoritados = favoritosLocais.map { it.id }.toSet()

                        // Verificar os Ids dos Favoritos que correspondem aos IDs da lista de personagens carregada no API
                        listaPersonagens.forEach { personagem ->
                            if (idsFavoritados.contains(personagem.id)) {
                                personagem.isFavorito = true // para que o icon do coração seja o vermelho
                            }
                        }

                        listaProcessada = listaPersonagens

                    } catch (e: Exception) {
                        Log.i("info_db", "Erro ao cruzar dados com o Banco de Dados: ${e.message}")
                        // Se a chamada ao banco de dados falhar, usamos a lista original da API de qualquer forma
                        listaProcessada = listaPersonagens
                    }
                }
            }

            // 4. Voltar à Main Thread para atualizar a Interface Gráfica (UI)
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE

                if (resposta != null) {
                    if (resposta.isSuccessful) {
                        if (listaProcessada != null && listaProcessada.isNotEmpty()) {

                            // Personagens já com o estado de favorito atualizado à lista global
                            listaPersonagensParaApresentar.addAll(listaProcessada)

                            // Aplicar os filtros ativos e atualizar o adaptador
                            aplicarFiltros()

                        } else {
                            exibirMensagem("Lista vazia")
                        }
                    } else {
                        exibirMensagem("Problema ao fazer a requisição CÓDIGO: ${resposta.code()}")
                    }
                } else {
                    exibirMensagem("Não foi possível fazer a requisição")
                }
            }
        }
    }

    private fun salvarPersonagemFavorito(personagem: Personagem) {
        val instanciaPersonagemFavorita = PersonagemFavorita(
            personagem.id,
            personagem.name,
            personagem.image,
            personagem.species,
            personagem.status,
            personagem.origin,
            personagem.location
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val idPersonagemFavorita = personagemDAO.salvarPersonagem(instanciaPersonagemFavorita)

                withContext(Dispatchers.Main) {
                    if (idPersonagemFavorita > 0L) {
                        personagem.isFavorito = true
                        personagemAdapter.atualizarItem(personagem)
                        exibirMensagem("Personagem adicionada aos Favoritos")
                    } else {
                        exibirMensagem("Erro ao adicionar personagem")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    exibirMensagem("Erro ao salvar personagem nos Favoritos: ${e.message}")
                }
            }
        }
    }

    private fun removerPersonagemFavorito(personagem: Personagem) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val idPersonagem = personagemDAO.removerPersonagens(personagem.id)

                withContext(Dispatchers.Main) {
                    if (idPersonagem > 0) {
                        personagem.isFavorito = false
                        personagemAdapter.atualizarItem(personagem)
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
    }

    private fun configurarFiltros() {
        cgStatus.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val checkedChipId = checkedIds.first()
                val chip = group.findViewById<com.google.android.material.chip.Chip>(checkedChipId)
                statusAtual = chip.text.toString().lowercase()
            } else {
                statusAtual = null
            }
            aplicarFiltros() // Atualiza a lista na tela
        }

        cgGenero.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val checkedChipId = checkedIds.first()
                val chip = group.findViewById<com.google.android.material.chip.Chip>(checkedChipId)
                generoAtual = chip.text.toString().lowercase()
            } else {
                generoAtual = null
            }
            aplicarFiltros() // Atualiza a lista na tela
        }
    }

    // Função interna que realiza o filtro na memória local do dispositivo
    fun aplicarFiltros() {
        val listaFiltrada = listaPersonagensParaApresentar.filter { personagem ->
                    // 1. Verifica o Filtro de Status
                    (statusAtual == null || personagem.status.lowercase() == statusAtual) &&
                    // 2. Verifica o Filtro de Género
                    (generoAtual == null || personagem.gender.lowercase() == generoAtual) &&
                    // 3. Verifica o Filtro de Nome
                    (nomeAtual == null || personagem.name.lowercase().contains(nomeAtual!!))
        }

        // Substitui a lista antiga/original no Adapter pela nova filtrada
        personagemAdapter.adicionarListaAtual(listaFiltrada)
    }
}