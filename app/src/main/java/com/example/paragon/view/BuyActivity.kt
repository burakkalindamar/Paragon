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
    lateinit var dbsql : SQLiteDatabase
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
        binding.buy.isEnabled=false
        binding.adet.requestFocus()
    }

    private fun binding(){
        binding= ActivityBuyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun geri(view:View){
        onBackPressed()
    }

    @SuppressLint("SetTextI18n")
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
                                //hisse fiyatında değişklik olursa toplam tutarı günceller
                                val price_dbl = price.toDouble()
                                    val toplam_tutar = (price_dbl * adet_int)
                                    val formattedTutar = String.format(Locale.US, "%.2f", toplam_tutar)
                                    binding.toplamTutar.text = "$$formattedTutar"
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
                // Metin değiştiğinde yapılacak işlemi burada
            }

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable?) {
                // Değişiklik yapıldıktan sonra yapılacak işlemi burada yapabilirsiniz


                //Girilen adetin uygunluğunu kontrol edip buna göre butonun aktifliğini ayarlar
                val adet = binding.adet.text.toString()
                var adet_int = adet.toIntOrNull()
                if (adet_int==null){
                    adet_int=0
                }
                if (adet_int > 0){
                    binding.buy.isEnabled = true
                }else {
                    binding.buy.isEnabled = false
                }

                //alınacak toplam tutarı yazar
                val price = binding.buyPrice.text.toString()
                val price_dbl = price.toDouble()
                val toplam_tutar = (price_dbl * adet_int)
                val formattedTutar = String.format(Locale.US, "%.2f", toplam_tutar)
                binding.toplamTutar.text = "$$formattedTutar"
            }
        })
    }

    fun database(){
        context=this
        dbsql=context.openOrCreateDatabase("testdb",Context.MODE_PRIVATE,null)
    }

    @SuppressLint("Recycle")
    fun satinAl(view: View){
        val stockname = binding.name.text.toString()
        val symbol = binding.symbol.text.toString()
        val price = binding.buyPrice.text.toString().toDouble()
        val shares = binding.adet.text.toString().toInt()

        //hissenin portföyde olup olmadığını kontrol eder
        val symbolExistsQuery = "SELECT 1 FROM table3 WHERE symbol='$symbol' LIMIT 1"
        val cursor = dbsql.rawQuery(symbolExistsQuery, null)
        val symbolExists = cursor.count > 0
        cursor.close()

        if (symbolExists) {
            // Belirtilen sembol veritabanında bulunuyorise çalışır

            //Eğer portföyde varsa ortalama fiyatı günceller ve ekleme yapar
            val oldPriceQuery = "SELECT buying_price,shares FROM table3 WHERE symbol='$symbol' LIMIT 1"
            val cursorOldPrice = dbsql.rawQuery(oldPriceQuery,null)
            cursorOldPrice.moveToFirst()
            val eski_fiyat = cursorOldPrice.getDouble(0)
            val eski_adet = cursorOldPrice.getInt(1)
            val ortalamaFiyat = ((shares * price) + (eski_fiyat * eski_adet)) / (eski_adet + shares)
            val toplamAdet = (eski_adet + shares)
           val ekleQuery = "UPDATE table3 SET buying_price='$ortalamaFiyat', shares='$toplamAdet' WHERE symbol='$symbol' "
            dbsql.execSQL(ekleQuery)


            Toast.makeText(this,"Satın Alma Başarılı",Toast.LENGTH_LONG).show()
            finish()
            val goHome = Intent(this,MainActivity::class.java)
            startActivity(goHome)



        } else {
            // Belirtilen sembol veritabanında bulunmuyorise çalışır
            // Satın almayı veri tabanına kaydeder ve ana sayfaya döner
            val satinalQuery = "INSERT INTO table3 (stocks, symbol, buying_price, shares) VALUES ('$stockname', '$symbol', $price, $shares)"
            dbsql.execSQL(satinalQuery)

            Toast.makeText(this,"Satın Alma Başarılı",Toast.LENGTH_LONG).show()
            finish()
            val goHome = Intent(this,MainActivity::class.java)
            startActivity(goHome)

        }

    }
}