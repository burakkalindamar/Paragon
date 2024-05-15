package com.example.paragon.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.paragon.R
import com.example.paragon.databinding.ActivityStocksBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale


class StocksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStocksBinding
    private val db = Firebase.firestore
    lateinit var symbol:String
    lateinit var company:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        symbol = intent.getStringExtra("symbol").toString()
        company = intent.getStringExtra("company").toString()
        enableEdgeToEdge()
        binding()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        line_chart()
        verileri_cek_yaz()

    }

    fun binding() {
        binding = ActivityStocksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun line_chart() {
        val symbol = intent.getStringExtra("symbol")
        val veriNoktalari = mutableListOf<Entry>()

        db.collection("Stocks").whereEqualTo("symbol", symbol).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val stocks = snapshot.documents
                    var fiyatlar: ArrayList<*>? = null
                    for (stock in stocks) {
                        fiyatlar = (stock.get("fiyatlar30") as? ArrayList<*>)!!
                    }

                    val uzunluk = fiyatlar!!.size

                    for (i in 0 until uzunluk)
                        veriNoktalari.add(Entry(i.toFloat(), (fiyatlar!![i] as Double).toFloat()))

                }

                // Veri noktalarını kullanarak LineDataSet oluşturun
                val veriSeti = LineDataSet(veriNoktalari, "")

                // Görsel Özelleştirme
                veriSeti.color = Color.parseColor("#1d6fdb")
                veriSeti.lineWidth = 4f
                veriSeti.setDrawFilled(true)
                veriSeti.fillColor = Color.parseColor("#1d6fdb")
                veriSeti.setDrawCircles(false) // Daireleri kaldırır
                veriSeti.setDrawValues(false)  // Üstlerindeki metinleri kaldırır

                // Bezier eğrileri kullanarak daha yumuşak bir eğri elde et
                veriSeti.mode = LineDataSet.Mode.CUBIC_BEZIER

                val hatVerisi = LineData(veriSeti)

                // Hat grafiğini görüntülemek için kullanılan görünüm referansı
                binding.lineChart.data = hatVerisi

                // Gereksiz Öğelerin Kaldırılması
                binding.lineChart.setDrawGridBackground(false)
                binding.lineChart.xAxis.setDrawLabels(false)
                binding.lineChart.axisRight.setDrawLabels(false)
                binding.lineChart.legend.isEnabled = false
                binding.lineChart.setTouchEnabled(false)

                // Y-ekseni ızgara çizgilerini kalınlaştır ve rengini siyah yap
                val leftAxis = binding.lineChart.axisLeft
                leftAxis.textSize = 13f

                binding.lineChart.xAxis.setDrawGridLines(false)
                binding.lineChart.axisLeft.setDrawGridLines(false)

                binding.lineChart.description.isEnabled = false

                // Grafiği güncelle
                binding.lineChart.invalidate()
            }
    }

    @SuppressLint("SetTextI18n")
    fun verileri_cek_yaz() {
        val symbol = intent.getStringExtra("symbol")
        var price: String
        var company: String
        var opening_price: String
        var daily_highest: String
        var daily_lowest: String
        var highest_price: String
        var lowest_price: String
        var pe_ratio: String

        db.collection("Stocks").whereEqualTo("symbol", symbol).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val stocks = documents.documents

                    for (stock in stocks) {
                        price = stock.getString("price") ?: ""
                        company = stock.getString("company") ?: ""
                        opening_price = stock.getString("opening_price") ?: ""
                        daily_highest = stock.getString("daily_highest") ?: ""
                        daily_lowest = stock.getString("daily_lowest") ?: ""
                        highest_price = stock.getString("highest_price") ?: ""
                        lowest_price = stock.getString("lowest_price") ?: ""
                        pe_ratio = stock.getString("pe_ratio") ?: ""

                        val change = degisim_hesapla(price, opening_price)
                        binding.stockname.text = company

                        binding.stockPrice.text = "$$price"

                        binding.stockDailyChange.text = "$change%"
                        val dailyChangeDouble = change.toDouble()
                        if (dailyChangeDouble > 0) {
                            binding.stockDailyChange.setTextColor(Color.parseColor("#4CAF50")) //YEŞİL
                        } else if (dailyChangeDouble < 0) {
                            binding.stockDailyChange.setTextColor(Color.parseColor("#F44336")) //KIRMIZI
                        } else {
                            binding.stockDailyChange.setTextColor(Color.parseColor("#9A9A9A")) //GRİ
                        }

                        binding.stocksymbol.text = symbol

                        binding.openingPrice.text = "$$opening_price"

                        binding.peRatio.text = pe_ratio

                        binding.dailyHighest.text = "$$daily_highest"

                        binding.dailyLowest.text = "$$daily_lowest"

                        binding.highestPrice.text = "$$highest_price"

                        binding.lowestPrice.text = "$$lowest_price"


                    }
                }
            }
    }

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

    fun geri(view: View) {
        onBackPressed()
    }

    fun go_sellActivity(view: View) {
        val go_sellActivity = Intent(this, SellActivity::class.java)
        go_sellActivity.putExtra("symbol", symbol)
        go_sellActivity.putExtra("company", company)
        startActivity(go_sellActivity)
    }

    fun go_buyActivity(view: View) {
        val go_buyActivity = Intent(this, BuyActivity::class.java)
        go_buyActivity.putExtra("symbol", symbol)
        go_buyActivity.putExtra("company", company)
        startActivity(go_buyActivity)
    }

    fun go_DataActivity(view: View) {
        val go_DataActivity = Intent(this, DataActivity::class.java)
        go_DataActivity.putExtra("symbol", symbol)
        go_DataActivity.putExtra("company", company)
        startActivity(go_DataActivity)
    }

}


