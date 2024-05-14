package com.example.paragon.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paragon.R
import com.example.paragon.adapter.PortfoyAdapter
import com.example.paragon.databinding.ActivityMainBinding
import com.example.paragon.model.PortfoyModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var dbsql : SQLiteDatabase
    lateinit var context: Context
    private val db = Firebase.firestore
    private var portfoylist = ArrayList<PortfoyModel>()
    private lateinit var PortfoyAdapter: PortfoyAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding() //binding işlemlemleri
        database()
        veritabanitemizle()
        PortfoyAdapter = PortfoyAdapter(portfoylist)
        portfoyListele()
        

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView()
    }

    private fun recyclerView() {
        val recyclerView = binding.portfoyum
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = PortfoyAdapter // Sınıf düzeyindeki PortfoyAdapter'ı kullan
    }

    fun go_search(view: View){
        val go_search = Intent(this, Search_page::class.java)
        startActivity(go_search)
    }

    fun refresh_balance(view: View){
        finish()
        val refresh_balance = Intent(this, MainActivity::class.java)
        startActivity(refresh_balance)
    }

    fun binding(){
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    @SuppressLint("SetTextI18n")
    fun database() {
        context = this
        dbsql = context.openOrCreateDatabase("testdb", Context.MODE_PRIVATE, null)
        dbsql.execSQL("CREATE TABLE IF NOT EXISTS table3(stocks TEXT, symbol TEXT, buying_price REAL, shares INTEGER)")
        dbsql.execSQL("CREATE TABLE IF NOT EXISTS testbakiye1 (bakiye REAL)")
        val bakiye_kontrolQuery = "SELECT bakiye FROM testbakiye1"
        val bakiyeCursor = dbsql.rawQuery(bakiye_kontrolQuery, null)
        var bakiye: Double = 0.0

        if (bakiyeCursor.moveToFirst()) {
            bakiye = bakiyeCursor.getDouble(0)
        } else {
            // Tabloda veri yok, yeni bir kayıt ekleyin
            val bakiye_ekleQuery = "INSERT INTO testbakiye1 (bakiye) VALUES (50000.00)"
            dbsql.execSQL(bakiye_ekleQuery)
            bakiye = 50000.00 // Yeni değeri atayın
        }
        binding.bakiye.text = "$$bakiye"
        bakiyeCursor.close()
    }



    @SuppressLint("Recycle", "SetTextI18n", "NotifyDataSetChanged")
    fun portfoyListele() {
        //portföydeki toplam hisse sayısını alır
        val countQuery = "SELECT COUNT(*) FROM table3"
        val countCursor = dbsql.rawQuery(countQuery, null)
        var toplam = 0
        if (countCursor.moveToFirst()) {
            toplam = countCursor.getInt(0)
        }
        countCursor.close()

        val portfoyQuery = "SELECT * FROM table3"
        val porfoyCursor = dbsql.rawQuery(portfoyQuery, null)
        var toplamdeger = 0.00
        binding.deger.text = "$0.00"

        while (porfoyCursor.moveToNext()) {
            val company = porfoyCursor.getString(0)
            val symbol = porfoyCursor.getString(1)
            val price = porfoyCursor.getString(2)
            val shares = porfoyCursor.getString(3)

            // Firebase'den anlık fiyatı alır
            db.collection("Stocks").whereEqualTo("symbol", symbol)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val firstStock = documents.first()
                        val anlik_fiyat = firstStock.getString("price") ?: ""
                        val anlik = anlik_fiyat.toDoubleOrNull() ?: 0.0
                        val shares_dbl = shares.toDoubleOrNull() ?: 0.0
                        val deger = anlik * shares_dbl

                        val veri = PortfoyModel(company, anlik_fiyat, price, shares,symbol)
                        portfoylist.add(veri)
                        PortfoyAdapter.notifyDataSetChanged()

                        //Toplam portföy değerini hesaplayıp yazar
                        toplamdeger += deger
                        val formattedDeger = String.format(Locale.US, "%.2f", toplamdeger)
                        binding.deger.text = "$$formattedDeger"
                    }
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                }
        }
        porfoyCursor.close()
    }

    @SuppressLint("Recycle")
    fun veritabanitemizle(){
        //Bütün adetleri satılan hisseleri portföyden siler
        val portfoyQuery = "SELECT * FROM table3"
        val portfoyCursor = dbsql.rawQuery(portfoyQuery,null)

        if(portfoyCursor.moveToFirst()){
            val silQuery = "DELETE FROM table3 WHERE shares=0"
            dbsql.execSQL(silQuery)
        }

    }

}