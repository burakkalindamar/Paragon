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
import com.example.paragon.R.layout.recycler_row
import com.example.paragon.model.StocksModel
import com.example.paragon.view.StocksActivity

class StocksAdapter(private val stocklist:ArrayList<StocksModel>) : RecyclerView.Adapter<StocksAdapter.StockHolder>() {

    class StockHolder (itemView : View):RecyclerView.ViewHolder(itemView){
        val symbol: TextView = itemView.findViewById(R.id.symbol)
        val price: TextView = itemView.findViewById(R.id.price)
        val company: TextView = itemView.findViewById(R.id.company)
        val daily_cahange: TextView = itemView.findViewById(R.id.daily_change)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(recycler_row, parent, false)
        return StockHolder(view)
    }

    override fun getItemCount(): Int {
        return stocklist.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StockHolder, position: Int) {
        holder.symbol.text = stocklist[position].symbol
        holder.price.text = "$"+stocklist[position].price
        holder.company.text = stocklist[position].company

        //günlük değişimin rengini belirler
        val color = if (stocklist[position].change.toDouble() < 0.00) {
            "#F44336" // Kırmızı
        } else if (stocklist[position].change.toDouble() == 0.00) {
            "#9A9A9A" // Gri
        } else {
            "#4CAF50" // Yeşil
        }
        holder.daily_cahange.setTextColor(Color.parseColor(color))
        holder.daily_cahange.text = stocklist[position].change+"%"


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, StocksActivity::class.java)

            intent.putExtra("company",stocklist[position].company)

            intent.putExtra("symbol",stocklist[position].symbol)


            holder.itemView.context.startActivity(intent)
        }

    }
}