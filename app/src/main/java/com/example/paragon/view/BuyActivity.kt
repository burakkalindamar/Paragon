package com.example.paragon.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.paragon.R
import com.example.paragon.databinding.ActivityBuyBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale

class BuyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuyBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding()
        fiyat_al()
        verileri_al()
        toplam_tutar_guncelle()
    }

    private fun binding(){
        binding= ActivityBuyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun geri(view: View){
        onBackPressed()
    }

    private fun fiyat_al() {
        val symbol = intent.getStringExtra("symbol")
        db.collection("Stocks").whereEqualTo("symbol", symbol).addSnapshotListener { snapshot, error ->
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
                                binding.buyPrice.text = price

                                val adet = binding.adet.text.toString()
                                var adet_int=adet.toIntOrNull()
                                if (adet_int==null){
                                    adet_int=0
                                }
                                    val price_dbl = price.toDouble()
                                    val toplam_tutar = (price_dbl * adet_int)
                                    val formattedTutar = String.format(Locale.US, "%.2f", toplam_tutar)
                                    binding.toplamTutar.text = formattedTutar
                            }
                        }
                    }
                }
            }
        }
    }//HİSSE VERİLERİNİ ALIR

    private fun verileri_al(){
        val symbol = intent.getStringExtra("symbol")
        val company = intent.getStringExtra("company")
        binding.name.text=company
        binding.symbol.text=symbol
    }

    private fun toplam_tutar_guncelle(){
        val editText = binding.adet

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Değişiklik yapılmadan önceki durumu yakalama
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Metin değiştiğinde yapılacak işlemi burada yapabilirsiniz
                // newText içindeki yeni metni kullanarak istediğiniz işlemleri gerçekleştirin
            }

            override fun afterTextChanged(s: Editable?) {
                // Değişiklik yapıldıktan sonra yapılacak işlemi burada yapabilirsiniz
                val adet = binding.adet.text.toString()
                var adet_int = adet.toIntOrNull()
                if (adet_int==null){
                    adet_int=0
                }
                val price = binding.buyPrice.text.toString()
                val price_dbl = price.toDouble()
                val toplam_tutar = (price_dbl * adet_int)
                val formattedTutar = String.format(Locale.US, "%.2f", toplam_tutar)
                binding.toplamTutar.text = formattedTutar
            }
        })
    }


}