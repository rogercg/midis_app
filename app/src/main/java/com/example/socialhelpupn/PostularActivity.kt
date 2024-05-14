package com.example.socialhelpupn

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

class PostularActivity : AppCompatActivity() {
    private lateinit var fechaEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.postular)

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
                // val intent = Intent(this@PostularActivity, MenuActivity::class.java)
                // startActivity(intent)
                consultarEndpoint(dni, fecha)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
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
        val url = "https://midis-a59a1b452df6.herokuapp.com/api/users/postular/" // Reemplaza con la dirección IP de tu computadora
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
                    Toast.makeText(this@PostularActivity, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful) {
                    if (responseData != null) {
                        val jsonResponse = JSONObject(responseData)
                        // Obtener los datos relevantes de la respuesta JSON
                        val estado_postular = jsonResponse.getBoolean("postular")
                        if(estado_postular){
                            runOnUiThread {
                                val builder = AlertDialog.Builder(this@PostularActivity)
                                builder.setTitle("")
                                builder.setMessage("Postulación registrada")
                                builder.setPositiveButton(R.string.aceptar) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                val dialog = builder.create()
                                dialog.show()
                            }
                                // Redirigir a la actividad del menú
                            // val intent = Intent(this@PostularActivity, MenuActivity::class.java)
                            // startActivity(intent)
                        } else{
                            runOnUiThread {
                                val builder = AlertDialog.Builder(this@PostularActivity)
                                builder.setTitle("")
                                builder.setMessage("Usted ya se registró")
                                builder.setPositiveButton(R.string.aceptar) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                val dialog = builder.create()
                                dialog.show()
                            }

                        }


                    }
                } else {
                    runOnUiThread {
                        if (response.code == 404 && responseData != null) {
                            Toast.makeText(this@PostularActivity, responseData, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@PostularActivity, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

}