package com.example.paragon.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paragon.R
import com.example.paragon.adapter.StocksAdapter
import com.example.paragon.databinding.ActivitySearchPageBinding
import com.example.paragon.model.StocksModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale

class Search_page : AppCompatActivity() {
    private val db = Firebase.firestore
    private lateinit var binding: ActivitySearchPageBinding
    private var stocklist = ArrayList<StocksModel>()
    private lateinit var stocksAdapter: StocksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        hisse_verilerinni_al()
        recyclerView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Geri tuşuna basıldığında yapılacak işlemler
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // İsterseniz bu sayfayı sonlandırabilirsiniz, isteğe bağlı.
    }


    fun go_balance(view: View){
        val go_balance = Intent(this, MainActivity::class.java)
        startActivity(go_balance)
        finish()
    } // anasayfaya gider



    fun binding(){
        binding=ActivitySearchPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun hisse_verilerinni_al(){
        db.collection("Stocks").addSnapshotListener { snapshot, error ->
            if (error!=null)
            {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }else{
                if (snapshot!=null){
                    if (!snapshot.isEmpty){
                        val stocks = snapshot.documents

                        stocklist.clear()
                        for ( stock in stocks){

                            val symbol = stock.get("symbol") as String ?:""
                            val price = stock.get("price") as String ?:""
                            val company = stock.get("company") as String
                            val opening_price = stock.get("opening_price") as String ?:""
                            val daily_highest = stock.get("daily_highest") as String ?:""
                            val daily_lowest = stock.get("daily_lowest") as String ?:""
                            val highest_price = stock.get("highest_price") as String ?:""
                            val lowest_price = stock.get("lowest_price") as String ?:""
                            val pe_ratio = stock.get("pe_ratio") as String ?:""

                            val change = degisim_hesapla(price,opening_price)

                            val alinanveri = StocksModel(symbol,price,company,change,daily_highest,daily_lowest,highest_price,lowest_price,pe_ratio,opening_price)
                            stocklist.add(alinanveri)
                        }
                        stocksAdapter.notifyDataSetChanged()

                    }
                }
            }
        }
    } //hisse verilerini alır

    fun recyclerView(){
        val recyclerView = binding.recyclerView
        val layoutManager=LinearLayoutManager(this)
        recyclerView.layoutManager=layoutManager
        stocksAdapter = StocksAdapter(stocklist)
        recyclerView.adapter=stocksAdapter
    } //recyclerviewe itemleri atar


    fun degisim_hesapla(price: String, opening_price: String): String {
        val x: String = price.replace(",", ".")
        val y: String = opening_price.replace(",", ".")

        val double_price = x.toDoubleOrNull() ?: return "Geçersiz fiyat formatı"
        val double_opening_price = y.toDoubleOrNull() ?: return "Geçersiz açılış fiyatı formatı"

        val a = double_price - double_opening_price
        val b = a / double_price
        val change = b * 100

        //tr formatında nokta yerinie virgül eklendiği için double çevrilirken hata veriyor.
        val formattedChange = String.format(Locale.US, "%.2f", change)
        return formattedChange
    } //günlük fiyat değişimini hesaplar

}