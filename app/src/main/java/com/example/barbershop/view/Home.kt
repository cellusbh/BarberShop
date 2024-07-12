package com.example.barbershop.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.barbershop.R
import com.example.barbershop.adapter.ServicosAdapter
import com.example.barbershop.databinding.ActivityHomeBinding
import com.example.barbershop.model.Servicos
import com.google.android.material.snackbar.Snackbar

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var servicosAdapter: ServicosAdapter
    private val listaServicos: MutableList<Servicos> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Exemplo de como recuperar um dado passado por outra activity
        val nome = intent.extras?.getString("nome")
        binding.textWelcome.text = "Bem-vindo(a), $nome!"

        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val message = result.data?.getStringExtra("message")
                    val barbeiro = result.data?.getStringExtra("barbeiro")
                    if (message != null && barbeiro != null) {
                        val fullMessage = "Agendamento realizado com sucesso com $barbeiro!"
                        val snackbar = Snackbar.make(binding.root, fullMessage, Snackbar.LENGTH_LONG)
                        snackbar.setBackgroundTint(Color.parseColor("#FF03DAC5"))
                        snackbar.show()
                    }
                }
            }

        binding.buttonAgendar.setOnClickListener {
            val intent = Intent(this, Agendamento::class.java)
            intent.putExtra("nome", nome)
            startForResult.launch(intent)
        }

        val recyclerViewServicos = binding.recyclerViewAll
        recyclerViewServicos.layoutManager = GridLayoutManager(this, 2)
        servicosAdapter = ServicosAdapter(this, listaServicos)
        recyclerViewServicos.setHasFixedSize(true)
        recyclerViewServicos.adapter = servicosAdapter
        getServicos()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getServicos() {

        val servico1 = Servicos(R.drawable.img1, "Corte Masculino")
        listaServicos.add(servico1)

        val servico2 = Servicos(R.drawable.img2, "Barba e Bigode")
        listaServicos.add(servico2)

        val servico3 = Servicos(R.drawable.img3, "Lavagem Capilar")
        listaServicos.add(servico3)

        val servico4 = Servicos(R.drawable.img4, "Tratamento Capilar")
        listaServicos.add(servico4)
    }

}