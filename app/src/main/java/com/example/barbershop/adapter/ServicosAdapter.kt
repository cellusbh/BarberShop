package com.example.barbershop.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.barbershop.databinding.ServicosItemBinding
import com.example.barbershop.model.Servicos
import com.example.barbershop.view.Agendamento

class ServicosAdapter(
    private val context: Context,
    private val listaServicos: MutableList<Servicos>,
    private val nome: String?,
    private val startForResult: ActivityResultLauncher<Intent>
) :
    RecyclerView.Adapter<ServicosAdapter.ServicosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicosViewHolder {
        val itemLista = ServicosItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ServicosViewHolder(itemLista)
    }

    override fun getItemCount() = listaServicos.size

    override fun onBindViewHolder(holder: ServicosViewHolder, position: Int) {
        holder.imgServico.setImageResource(listaServicos[position].img!!)
        holder.textServico.text = listaServicos[position].nome
        holder.textServico.setOnClickListener {
            val intent = Intent(context, Agendamento::class.java)
            intent.putExtra("servico", listaServicos[position].nome)
            intent.putExtra("nome", nome)
            startForResult.launch(intent)
        }
        holder.imgServico.setOnClickListener {
            val intent = Intent(context, Agendamento::class.java)
            intent.putExtra("servico", listaServicos[position].nome)
            intent.putExtra("nome", nome)
            startForResult.launch(intent)
        }
    }

    inner class ServicosViewHolder(binding: ServicosItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val imgServico = binding.imageServico
        val textServico = binding.textServico
    }
}