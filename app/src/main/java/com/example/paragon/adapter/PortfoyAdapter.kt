package com.example.paragon.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paragon.R
import com.example.paragon.model.PortfoyModel
import com.example.paragon.view.StocksActivity
import java.util.Locale

class PortfoyAdapter(private val portfoylist:ArrayList<PortfoyModel>) :
    RecyclerView.Adapter<PortfoyAdapter.PortfoyHolder>() {

    class PortfoyHolder (itemView: View) : RecyclerView.ViewHolder(itemView){

        val name: TextView = itemView.findViewById(R.id.companyname)
        val price: TextView = itemView.findViewById(R.id.portfoyStockPrice)
        val shares: TextView = itemView.findViewById(R.id.shares)
        val toplam_tutar: TextView = itemView.findViewById(R.id.portfoytoplamtutar)
        val degisim: TextView = itemView.findViewById(R.id.degisim)
        val maliyet: TextView = itemView.findViewById(R.id.maliyet)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PortfoyHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.portfoy_row, parent, false)
        return PortfoyAdapter.PortfoyHolder(view)
    }

    override fun getItemCount(): Int {
        return portfoylist.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PortfoyHolder, position: Int) {
        holder.name.text = portfoylist[position].company
        holder.price.text = "$" + portfoylist[position].anlik_fiyat
        holder.shares.text = portfoylist[position].shares
        holder.maliyet.text = "$" + portfoylist[position].price

        val anlikFiyat = portfoylist[position].anlik_fiyat.toDoubleOrNull()
        val shares = portfoylist[position].shares.toIntOrNull()

        if (anlikFiyat != null && shares != null) {
            val toplam = anlikFiyat * shares
            val formattedtoplamtutar = String.format(Locale.US, "%.2f", toplam)
            holder.toplam_tutar.text = "$$formattedtoplamtutar"



            val ortalamaFiyat = portfoylist[position].price.replace(",", ".").toDoubleOrNull()
            if (ortalamaFiyat != null) {
                val change = ((anlikFiyat - ortalamaFiyat) / anlikFiyat) * 100
                val formattedChange = String.format(Locale.US, "%.2f", change)

                val color = if (formattedChange.toDouble() < 0.00) {
                    "#F44336" // Kırmızı
                } else if (formattedChange.toDouble() == 0.00) {
                    "#9A9A9A" // Gri
                } else {
                    "#4CAF50" // Yeşil
                }
                holder.degisim.setTextColor(Color.parseColor(color))
                holder.degisim.text = "$formattedChange%"
            } else {
                holder.degisim.text = "0.00"
            }
        } else {
            holder.toplam_tutar.text = "0.00"
            holder.degisim.text = "0.00"
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, StocksActivity::class.java)
            intent.putExtra("symbol",portfoylist[position].symbol)
            intent.putExtra("company",portfoylist[position].company)
            holder.itemView.context.startActivity(intent)

        }


    }


}