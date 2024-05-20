package com.example.paragon.adapter

import IslemModel
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paragon.R
import java.util.Locale

class IslemAdapter(private val islemlist: ArrayList<IslemModel>):
    RecyclerView.Adapter<IslemAdapter.IslemHolder>(){
    class IslemHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val islemSymbol: TextView = itemView.findViewById(R.id.islemSymbol)
        val islemDate: TextView = itemView.findViewById(R.id.islemDate)
        val islemAdet: TextView = itemView.findViewById(R.id.islemAdet)
        val islemFiyat: TextView = itemView.findViewById(R.id.islemFiyat)
        val islem: TextView = itemView.findViewById(R.id.islem)
        val islemToplam: TextView = itemView.findViewById(R.id.islemToplam)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IslemAdapter.IslemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.islem_row, parent, false)
        return IslemAdapter.IslemHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: IslemAdapter.IslemHolder, position: Int) {
        val islem = islemlist[position]

        holder.islemSymbol.text = islem.symbol
        holder.islemDate.text = islem.getFormattedTarih()
        holder.islemAdet.text = "${islem.adet} Adet"
        holder.islemFiyat.text = "$${islem.fiyat}"
        holder.islem.text = islem.islem

        val toplamtutar = islem.fiyat * islem.adet
        val formattedtoplamtutar = String.format(Locale.US, "%.2f", toplamtutar)
        holder.islemToplam.text = "$$formattedtoplamtutar"

        when (islem.islem) {
            "ALIM" -> holder.islem.setBackgroundResource(R.drawable.buybutton)
            "SATIM" -> holder.islem.setBackgroundResource(R.drawable.sellbutton)
            else -> holder.itemView.setBackgroundResource(0) // Varsayılan arka plan
        }
    }


    override fun getItemCount(): Int {
        return islemlist.size
    }


}