package com.example.barbershop.view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.barbershop.R
import com.example.barbershop.databinding.ActivityAgendamentoBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class Agendamento : AppCompatActivity() {

    private lateinit var binding: ActivityAgendamentoBinding
    private val calendar: Calendar = Calendar.getInstance()
    private var data: String = ""
    private var hora: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAgendamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val nome = intent.extras?.getString("nome").toString()

        val datePicker = binding.datePicker
        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            var dia = dayOfMonth.toString()
            val mes: String

            if (dayOfMonth < 10) {
                dia = "0$dayOfMonth"
            }
            if (monthOfYear < 10) {
                mes = "" + (monthOfYear + 1)
            } else {
                mes = (monthOfYear + 1).toString()
            }

            data = "$dia / $mes / $year"
        }

        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->

            val minuto: String

            if (minute < 10) {
                minuto = "0$minute"
            } else {
                minuto = minute.toString()
            }

            hora = "$hourOfDay:$minuto"
        }

        binding.timePicker.setIs24HourView(true)

        binding.buttonAgendar.setOnClickListener {

            val freddy = binding.Freddy
            val jason = binding.Jason
            val edward = binding.Edward

            when {
                hora.isEmpty() -> {
                    mensagem(it, "Selecione um horário!", "#E74C3C")
                }
                hora <= "7:59" && hora >= "19:01" -> {
                    mensagem(it, "Barber Shop está fechado - horário de atendimento de 08:00 às 19:00!", "#E74C3C")
                }
                data.isEmpty() -> {
                    mensagem(it, "Selecione uma data!", "#E74C3C")
                }
                freddy.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    salvarAgendamento(it, nome, "Freddy Krueger", data, hora)
                }
                jason.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    salvarAgendamento(it, nome, "Jason Voorhees", data, hora)
                }
                edward.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    salvarAgendamento(it, nome, "Edward Scissorhands", data, hora)
                }
                else -> {
                    mensagem(it, "Escolha um barbeiro!", "#E74C3C")
                }
            }
        }

        val checkBoxFreddy = findViewById<CheckBox>(R.id.Freddy)
        val checkBoxJason = findViewById<CheckBox>(R.id.Jason)
        val checkBoxEdward = findViewById<CheckBox>(R.id.Edward)

        checkBoxFreddy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxJason.isChecked = false
                checkBoxEdward.isChecked = false
            }
        }

        checkBoxJason.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxFreddy.isChecked = false
                checkBoxEdward.isChecked = false
            }
        }

        checkBoxEdward.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxFreddy.isChecked = false
                checkBoxJason.isChecked = false
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun mensagem(view: View, mensagem: String, cor: String) {
        val snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(Color.parseColor(cor))
        snackbar.setTextColor(Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

    private fun salvarAgendamento(view: View, cliente: String, barbeiro: String, data: String, hora: String) {

        val db = FirebaseFirestore.getInstance()
        val dadosUsuario = hashMapOf(
            "cliente" to cliente,
            "barbeiro" to barbeiro,
            "data" to data,
            "hora" to hora
        )

        db.collection("agendamento").document(cliente).set(dadosUsuario).addOnCompleteListener {
            mensagem(view, "Agendamento realizado com sucesso com $barbeiro!", "#FF03DAC5")
        }.addOnFailureListener {
            mensagem(view, "Erro no servidor!", "#E74C3C")
        }
    }
}