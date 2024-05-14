package com.example.socialhelpupn

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PadronActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.padron)

        // Ocultar la barra de acción
        supportActionBar?.hide()


        // Configurar el botón para abrir una web
        val openWebButton: Button = findViewById(R.id.openWebButton)
        openWebButton.setOnClickListener {
            val url = "https://www.gob.pe/juntos"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}