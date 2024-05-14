package com.example.socialhelpupn

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MenuActivity : AppCompatActivity() {
    private lateinit var gridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)



        // Ocultar la barra de acción
        supportActionBar?.hide()



        // Obtener textos del menú desde strings.xml
        val textos = arrayOf(
            getString(R.string.mi_juntos),
            getString(R.string.postular),
            getString(R.string.chat),
            getString(R.string.consultar_sisfoh),
            getString(R.string.consultar_afiliacion),
                    getString(R.string.salir),
        )

        // Iconos del menú
        val imagenes = intArrayOf(
            R.drawable.icon_mi_juntos,
            R.drawable.icon_mi_postular,
            R.drawable.icon_mi_chat,
            R.drawable.icon_mi_sisfoh,
            R.drawable.icon_mi_consultar,
                    R.drawable.salir
        )

        // Configurar el adaptador del GridView
        val adapter = MenuAdapter(this, textos, imagenes)
        gridView = findViewById(R.id.gridMenu)
        gridView.adapter = adapter

        // Configurar el logotipo y los datos del usuario
        val logoImageView: ImageView = findViewById(R.id.logoImageView)
        val userImageView: ImageView = findViewById(R.id.userImageView)
        val userNameTextView: TextView = findViewById(R.id.userNameTextView)

        // Cargar el logotipo
        logoImageView.setImageResource(R.drawable.logo_black)

        // Obtener datos del usuario (aquí puedes obtener los datos reales, si están disponibles)
        // Obtener el nombre del usuario de SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("names", "") ?: ""

// Configurar el nombre del usuario en el TextView
        userNameTextView.text = userName
        val userImageResId = R.drawable.profile

        // Configurar la imagen y nombre del usuario
        userImageView.setImageResource(userImageResId)
        userNameTextView.text = userName


        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> redirigirAMiJuntos()
                1 -> redirigirAPostular()
                2 -> redirigirAChat()
                3 -> mostrarModalConsultarSISFOH()
                4 -> redirigirAConsultarAfiliacion()
                5 -> redirigirAProcesoDelPadron()
                else -> Toast.makeText(this, "Opción no reconocida", Toast.LENGTH_SHORT).show()
            }
        }


        val fabLanguage: FloatingActionButton = findViewById(R.id.fab_language)
        fabLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }

        applySelectedLanguage()
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf(getString(R.string.spanish), getString(R.string.quechua))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.select_language))
        builder.setItems(languages) { dialog, which ->
            when (which) {
                0 -> setLocale("es")
                1 -> setLocale("que")
            }
        }
        builder.create().show()
    }

    private fun applySelectedLanguage() {
        val sharedPreferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPreferences.getString("selected_language", "es") ?: "es"
        val locale = Locale(selectedLanguage)
        Locale.setDefault(locale)
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }


    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // Reiniciar la actividad actual para aplicar los cambios de idioma
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun redirigirAMiJuntos() {
        val intent = Intent(this, JuntosActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_language, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_spanish -> {
                setLocale("es")
                true
            }
            R.id.action_quechua -> {
                setLocale("que")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun redirigirAPostular() {
        val intent = Intent(this, PostularActivity::class.java)
        startActivity(intent)
    }

    private fun redirigirAChat() {
        //val intent = Intent(this, ChatActivity::class.java)
        //startActivity(intent)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.development_title)
        builder.setMessage(R.string.development_message)
        builder.setPositiveButton(R.string.aceptar) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun mostrarModalConsultarSISFOH() {
        val intent = Intent(this, SisfohActivity::class.java)
        startActivity(intent)
    }

    private fun redirigirAProcesoDelPadron() {
        //val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        //val editor = sharedPreferences.edit()
        //editor.clear()
        //editor.apply()


        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun redirigirAConsultarAfiliacion() {
        val intent = Intent(this, AfiliacionActivity::class.java)
        startActivity(intent)
    }
}
