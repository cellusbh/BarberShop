package com.example.barbershop.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
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
    private var servicos: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAgendamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val servicos = listOf("Selecione um serviço", "Corte Masculino", "Barba e Bigode", "Lavagem Capilar", "Tratamento Capilar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, servicos)
        binding.spinnerServicos.adapter = adapter

        val servico = intent.extras?.getString("servico")
        if (servico != null) {
            val spinner = findViewById<Spinner>(R.id.spinner_servicos)
            val adapter = spinner.adapter as ArrayAdapter<String>
            val position = adapter.getPosition(servico)
            spinner.setSelection(position)
        }

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

            data = "$dia - $mes - $year"
        }

        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->

            val minuto: String

            if (minute < 10) {
                minuto = "0$minute"
            } else {
                minuto = minute.toString()
            }

            hora = "$hourOfDay : $minuto"
        }

        binding.timePicker.setIs24HourView(true)

        binding.buttonAgendar.setOnClickListener {

            val freddy = binding.Freddy
            val jason = binding.Jason
            val edward = binding.Edward

            val servicoSelecionadoPosicao = binding.spinnerServicos.selectedItemPosition
            val servicoSelecionadoNome = binding.spinnerServicos.selectedItem.toString()

            when {
                data.isEmpty() -> {
                    mensagem(it, "Selecione uma data!", "#E74C3C")
                }
                hora.isEmpty() -> {
                    mensagem(it, "Selecione um horário!", "#E74C3C")
                }
                hora <= "7:59" && hora >= "19:01" -> {
                    mensagem(it, "Barber Shop está fechado - horário de atendimento de 08:00 às 19:00!", "#E74C3C")
                }
                servicoSelecionadoPosicao == 0 -> {
                    mensagem(it, "Selecione um serviço!", "#E74C3C")
                }
                freddy.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    salvarAgendamento(it, nome, "Freddy Krueger", data, hora, servicoSelecionadoNome)
                }
                jason.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    salvarAgendamento(it, nome, "Jason Voorhees", data, hora, servicoSelecionadoNome)
                }
                edward.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    salvarAgendamento(it, nome, "Edward Scissorhands", data, hora, servicoSelecionadoNome)
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

    private fun salvarAgendamento(view: View, cliente: String, barbeiro: String, data: String, hora: String, servicos: String) {

        val db = FirebaseFirestore.getInstance()
        val dadosUsuario = hashMapOf(
            "cliente" to cliente,
            "barbeiro" to barbeiro,
            "data" to data,
            "hora" to hora,
            "servicos" to servicos
        )

        db.collection("agendamento").document(cliente).set(dadosUsuario).addOnCompleteListener {
            val data = Intent()
            data.putExtra("message", "Agendamento realizado com sucesso com $barbeiro!")
            data.putExtra("barbeiro", barbeiro)
            setResult(Activity.RESULT_OK, data)
            finish()
        }.addOnFailureListener {
            mensagem(view, "Erro no servidor!", "#E74C3C")
        }
    }
}