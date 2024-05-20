package com.example.paragon.view

import IslemModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paragon.R
import com.example.paragon.adapter.IslemAdapter
import com.example.paragon.databinding.ActivityIslemBinding
import java.text.SimpleDateFormat
import java.util.*

class IslemActivity : AppCompatActivity() {
    lateinit var dbsql: SQLiteDatabase
    lateinit var binding: ActivityIslemBinding
    lateinit var context: Context
    private var islemlist = ArrayList<IslemModel>()
    private lateinit var islemAdapter: IslemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding()
        islemAdapter = IslemAdapter(islemlist)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView()

        // Veritabanından mevcut işlemleri yükle
        listele()
    }

    private fun recyclerView() {
        val recyclerView = binding.islemlerim
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = islemAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun listele() {
        context = this
        dbsql = context.openOrCreateDatabase("testdb", Context.MODE_PRIVATE, null)
        val islemQuery = "SELECT * FROM islemler"
        val islemCursor = dbsql.rawQuery(islemQuery, null)

        islemlist.clear()
        while (islemCursor.moveToNext()) {
            val symbol = islemCursor.getString(0)
            val tarihString = islemCursor.getString(1)
            val adet = islemCursor.getInt(2)
            val fiyat = islemCursor.getDouble(3)
            val islem = islemCursor.getString(4)

            // Tarih stringini Date türüne çevir
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val tarih = dateFormat.parse(tarihString)

            val islemler = IslemModel(symbol, tarih!!, adet, fiyat, islem)
            islemlist.add(islemler)
        }

        islemCursor.close()

        // Tarihe göre ters sırala (büyükten küçüğe)
        islemlist.sortByDescending { it.tarih }

        // RecyclerView güncellemesi
        islemAdapter.notifyDataSetChanged() // Değişiklikleri bildir
    }

    fun binding() {
        binding = ActivityIslemBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun addNewIslem(view: View) {
        val newIslem = IslemModel("Yeni Symbol", Date(), 10, 100.0, "ALIM") // Örnek değerler
        islemlist.add(0, newIslem) // Yeni işlemi listenin en başına ekle
        islemAdapter.notifyItemInserted(0) // RecyclerView'a eklendiğini bildir
    }

    fun go_search(view: View) {
        finish()
        val go_search = Intent(this, Search_page::class.java)
        startActivity(go_search)
    }

    fun go_balance(view: View) {
        val go_balance = Intent(this, MainActivity::class.java)
        startActivity(go_balance)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Geri tuşuna basıldığında yapılacak işlemler
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
