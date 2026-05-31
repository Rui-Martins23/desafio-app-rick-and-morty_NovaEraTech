package com.example.rickandmortyapi

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Colocar o título com o gradiente de cores
        val titulo = findViewById<TextView>(R.id.tv_title)

        val corInicial = "#1B232D".toColorInt() // Cinza escuro/Topo
        val corFinal = "#2E6B4F".toColorInt() // Verde esmeralda

        val paint = titulo.paint
        val widthTitulo = paint.measureText(titulo.text.toString())

        val gradienteTitulo = LinearGradient(
            0f, 0f, widthTitulo, 0f,
            intArrayOf(corInicial, corFinal),
            null,
            Shader.TileMode.CLAMP
        )

        paint.shader = gradienteTitulo

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, ListaPersonagensActivity::class.java)
            startActivity(intent)

            // Faz que com o utilizador não consiga voltar para a tela da MainActivity
            finish()
        }, 2000) // equivale a 2 segundos
    }
}