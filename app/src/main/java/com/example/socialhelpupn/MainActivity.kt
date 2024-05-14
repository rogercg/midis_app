package com.example.socialhelpupn

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*

import org.json.JSONObject
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var fechaEditText: EditText
    private lateinit var fabLanguage: FloatingActionButton

    private lateinit var textViewDNI: TextView
    private lateinit var textViewFechaEmision: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ocultar la barra de acción
        supportActionBar?.hide()



        val ingresarButton: Button = findViewById(R.id.openWebButton)
        val dniEditText: EditText = findViewById(R.id.editTextNumber)
        fechaEditText = findViewById(R.id.editTextNumber2)

        fechaEditText.setOnClickListener {
            showDatePickerDialog()
        }

        val startDateEditText: EditText = findViewById(R.id.editTextNumber2)

        dniEditText.setText("12345678")
        startDateEditText.setText("20/10/2023")
        fechaEditText.setText("20/10/2023")

        ingresarButton.setOnClickListener {
            val dni = dniEditText.text.toString()
            val fecha = fechaEditText.text.toString()

            if (dni.isNotEmpty() && fecha.isNotEmpty()) {
                //val intent = Intent(this@MainActivity, MenuActivity::class.java)
                //startActivity(intent)
                consultarEndpoint(dni, fecha)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

        fabLanguage = findViewById(R.id.fab_language)
        fabLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }

        textViewDNI = findViewById(R.id.lblViewDNI)
        textViewFechaEmision = findViewById(R.id.lblViewFechaEmision)

        updateViews()
    }

    private fun updateLabels() {
        textViewDNI.text = getString(R.string.dni_label)
        textViewFechaEmision.text = getString(R.string.fecha_emision_label)
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

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        val sharedPreferences = getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selected_language", languageCode)
        editor.apply()

        // Reiniciar la actividad actual para aplicar los cambios de idioma
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun updateViews() {
        textViewDNI.text = getString(R.string.dni_label)
        textViewFechaEmision.text = getString(R.string.fecha_emision_label)
        findViewById<Button>(R.id.openWebButton).text = getString(R.string.ingresar)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            R.style.CustomDatePickerDialog,  // Aplicar el tema personalizado
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDay = if (selectedDay < 10) "0$selectedDay" else "$selectedDay"
                val formattedMonth = if (selectedMonth + 1 < 10) "0${selectedMonth + 1}" else "${selectedMonth + 1}"
                val selectedDate = "$formattedDay/$formattedMonth/$selectedYear"
                fechaEditText.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }


    private fun consultarEndpoint(dni: String, fecha: String) {
        val url = "https://midis-a59a1b452df6.herokuapp.com/api/users/login/"
        val client = OkHttpClient()

        // Crear el cuerpo de la solicitud usando MultipartBody
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("dni", dni)
            .addFormDataPart("start_date", fecha)
            .build()

        // Construir la solicitud
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // Realizar la solicitud
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity", "Error al conectar con el servidor", e)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful) {
                    if (responseData != null) {
                        val jsonResponse = JSONObject(responseData)
                        // Obtener los datos relevantes de la respuesta JSON
                        val id = jsonResponse.getString("_id")
                        val dni = jsonResponse.getString("dni")
                        val startDate = jsonResponse.getString("start_date")
                        val description = jsonResponse.getString("description")
                        val consultarAfiliacion = jsonResponse.getBoolean("consultar_afiliacion")
                        val consultarSisfo = jsonResponse.getBoolean("consultar_sisfo")
                        val postular = jsonResponse.getBoolean("postular")
                        val names = jsonResponse.getString("names")

                        // Guardar los datos en SharedPreferences
                        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("id", id)
                        editor.putString("dni", dni)
                        editor.putString("startDate", startDate)
                        editor.putString("description", description)
                        editor.putBoolean("consultarAfiliacion", consultarAfiliacion)
                        editor.putBoolean("consultarSisfo", consultarSisfo)
                        editor.putBoolean("postular", postular)
                        editor.putString("names", names)
                        editor.apply()

                        // Redirigir a la actividad del menú
                        val intent = Intent(this@MainActivity, MenuActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    runOnUiThread {
                        if (response.code == 404 && responseData != null) {
                            Toast.makeText(this@MainActivity, responseData, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

}