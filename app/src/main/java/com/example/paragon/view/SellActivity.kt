package com.example.paragon.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.paragon.R
import com.example.paragon.databinding.ActivitySellBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale

class SellActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySellBinding
    private val db = Firebase.firestore
    lateinit var dbsql: SQLiteDatabase
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fiyat_al()
        verileri_al()
        toplam_tutar_guncelle()
        database()
        binding.sell.isEnabled = false
        binding.selladet.requestFocus()
    }

    private fun binding() {
        binding = ActivitySellBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    @SuppressLint("SetTextI18n")
    private fun fiyat_al() {
        val symbol = intent.getStringExtra("symbol")
        db.collection("Stocks").whereEqualTo("symbol", symbol)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
                } else {
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {
                            val stocks = snapshot.documents

                            // Eğer birden fazla belge varsa sadece ilk belgeyi kullanıyoruz
                            val firstStock = stocks.firstOrNull()

                            if (firstStock != null) {
                                val price = firstStock.getString("price") ?: ""

                                // UI güncelleme işlemi ana thread üzerinde gerçekleşmeli
                                runOnUiThread {
                                    binding.sellPrice.text = price

                                    val adet = binding.selladet.text.toString()
                                    var adet_int = adet.toIntOrNull()
                                    if (adet_int == null) {
                                        adet_int = 0
                                    }

                                    //hisse fiyatında değişklik olursa toplam tutarı günceller
                                    val price_dbl = price.toDouble()
                                    val toplam_tutar = (price_dbl * adet_int)
                                    val formattedTutar =
                                        String.format(Locale.US, "%.2f", toplam_tutar)
                                    binding.toplamTutar.text = "$$formattedTutar"
                                }
                            }
                        }
                    }
                }
            }
    }//HİSSE VERİLERİNİ ALIR

    private fun verileri_al() {
        val symbol = intent.getStringExtra("symbol")
        val company = intent.getStringExtra("company")
        binding.name.text = company
        binding.symbol.text = symbol
    }

    private fun toplam_tutar_guncelle() {
        val editText = binding.selladet

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Değişiklik yapılmadan önceki durumu yakalama
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Metin değiştiğinde yapılacak işlemi burada yapıır
            }

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                // Değişiklik yapıldıktan sonra yapılacak işlemi burada yapabilirsiniz

                val adet = binding.selladet.text.toString()
                var adet_int = adet.toIntOrNull()
                if (adet_int == null) {
                    binding.mevcutadet.setTextColor(ContextCompat.getColor(context, R.color.grey))
                    adet_int = 0
                }
                //Satılacak adet sayısını kontrol eder
                val symbol = binding.symbol.text.toString()
                val adetQuery = "SELECT shares FROM portfoy WHERE symbol='$symbol' LIMIT 1"
                val adetCursor = dbsql.rawQuery(adetQuery, null)
                var mevcutadet = 0

                if (adetCursor.moveToFirst()) {
                    mevcutadet = adetCursor.getInt(0)
                }
                adetCursor.close()

                if (adet_int <= mevcutadet) {
                    if (adet_int > 0) {
                        binding.sell.isEnabled = true
                        binding.mevcutadet.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.green
                            )
                        )
                    }
                } else {
                    binding.mevcutadet.setTextColor(ContextCompat.getColor(context, R.color.red))
                    binding.sell.isEnabled = false
                }

                //satılacak toplam tutarı yazar
                val price = binding.sellPrice.text.toString()
                val price_dbl = price.toDouble()
                val toplam_tutar = (price_dbl * adet_int)
                val formattedTutar = String.format(Locale.US, "%.2f", toplam_tutar)
                binding.toplamTutar.text = "$$formattedTutar"
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun database() {
        context = this
        dbsql = context.openOrCreateDatabase("testdb", Context.MODE_PRIVATE, null)
        //Satılacak hissenin adet sayısını satış sayfasına yazar
        val symbol = binding.symbol.text.toString()
        val adetQuery = "SELECT shares FROM portfoy WHERE symbol='$symbol' LIMIT 1"
        val adetCursor = dbsql.rawQuery(adetQuery, null)
        if (adetCursor.moveToFirst()) {
            val adet = adetCursor.getInt(0)
            binding.mevcutadet.text = adet.toString() + " Adet '$symbol' hisseniz var"
        } else {
            binding.mevcutadet.text = "0 Adet '$symbol' hisseniz var"
        }
        adetCursor.close()
    }

    @SuppressLint("Recycle")
    fun sat(view: View) {
        val stockname = binding.name.text.toString()
        val symbol = binding.symbol.text.toString()
        val price = binding.sellPrice.text.toString().toDouble()
        val shares = binding.selladet.text.toString().toInt()

        //Hissenin veri tabanında var mı kontrol eder
        val symbolExistsQuery = "SELECT 1 FROM portfoy WHERE symbol='$symbol' LIMIT 1"
        val cursor = dbsql.rawQuery(symbolExistsQuery, null)
        val symbolExists = cursor.count > 0
        cursor.close()

        if (symbolExists) {
            // Belirtilen sembol veritabanında bulunuyorise çalışır
            // Hissenin satışını yapar ve adet sayısını günceller
            val adetQuery = "SELECT shares FROM portfoy WHERE symbol='$symbol' LIMIT 1"
            val adetCursor = dbsql.rawQuery(adetQuery, null)
            var adet = 0

            if (adetCursor.moveToFirst()) {
                adet = adetCursor.getInt(0)
            }

            val yeniadet = (adet - shares)

            val satQuery = "UPDATE portfoy SET shares='$yeniadet' WHERE symbol='$symbol'"
            dbsql.execSQL(satQuery)

            val eskibakiyeQuery = "SELECT * FROM bakiye"
            val eskibakiyeCursor = dbsql.rawQuery(eskibakiyeQuery, null)
            var eskibakiye = 0.00
            if (eskibakiyeCursor.moveToFirst()) {
                eskibakiye = eskibakiyeCursor.getDouble(0)
            }
            eskibakiyeCursor.close()

            val topTutar = binding.toplamTutar.text.toString()
            val formatedtoplamtutar = topTutar.replace("$", "")
            val formatedtoplamtutar_dbl = formatedtoplamtutar.toDouble()
            val yeniBakiye = (eskibakiye + formatedtoplamtutar_dbl)
            val formattedYenibakiye = String.format(Locale.US, "%.2f", yeniBakiye).toDouble()
            val bakiyeGuncelleQuery = "UPDATE bakiye SET bakiye='$formattedYenibakiye'"
            dbsql.execSQL(bakiyeGuncelleQuery)

            Toast.makeText(this, "Satma İşlemi Başarılı", Toast.LENGTH_LONG).show()
            finish()
            val goHome = Intent(this, MainActivity::class.java)
            startActivity(goHome)


        }
    }

    fun geri(view: View) {
        onBackPressed()
    }
}