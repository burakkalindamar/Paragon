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


class StocksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStocksBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding()
        line_chart()
        verileriYaz()


    }

    fun binding(){
        binding= ActivityStocksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun line_chart() {
        // Veri noktalarını içeren liste
        val veriNoktalari = mutableListOf<Entry>()

        // Veri noktalarını doldurun (kullanıcının verilerini buraya ekleyin)
        veriNoktalari.add(Entry(1f, 30f))
        veriNoktalari.add(Entry(2f, 20f))
        veriNoktalari.add(Entry(3f, 15f))
        veriNoktalari.add(Entry(4f, 10f))
        veriNoktalari.add(Entry(5f, 20f))
        veriNoktalari.add(Entry(6f, 15f))

        // Hat grafiğinin görsel özelliklerini tanımlayan veri kümesi
        val veriSeti = LineDataSet(veriNoktalari, "")

        // Görsel Özelleştirme
        veriSeti.color = Color.BLACK
        veriSeti.lineWidth = 4f
        veriSeti.setDrawFilled(true)
        veriSeti.fillColor = Color.BLACK
        veriSeti.setCircleColor(Color.BLACK)
        veriSeti.valueTextSize=10f

        // Hat grafiğinin çizileceği veriyi temsil eden veri yapısı
        val hatVerisi = LineData(veriSeti)

        // Hat grafiğini görüntülemek için kullanılan görünüm referansı
        binding.lineChart.data = hatVerisi

        // Animasyonlar
        binding.lineChart.animateY(2000)

        // Gereksiz Öğelerin Kaldırılması
        binding.lineChart.setDrawGridBackground(false)
        binding.lineChart.xAxis.setDrawLabels(false)
        binding.lineChart.axisRight.setDrawLabels(false)
        binding.lineChart.legend.isEnabled = false
        binding.lineChart.setTouchEnabled(false)

        // **Y-ekseni ızgara çizgilerini kalınlaştır ve rengini siyah yap**
        val leftAxis = binding.lineChart.axisLeft
        leftAxis.textSize=13f

        binding.lineChart.xAxis.setDrawGridLines(false)
        binding.lineChart.axisLeft.setDrawGridLines(false)

        binding.lineChart.description.isEnabled=false

        // Grafiği güncelle
        binding.lineChart.invalidate()
    }

    @SuppressLint("SetTextI18n")
    fun verileriYaz() {
        val intent = intent
        val dolar = "$"
        val yuzde = "%"

        val company = intent.getStringExtra("company")
        binding.stockname.text = company.toString()

        val price = intent.getStringExtra("price")
        binding.stockPrice.text = dolar+price.toString()

        val dailyChange = intent.getStringExtra("dailyChange")
        binding.stockDailyChange.text = dailyChange.toString()+yuzde

        val dailyChangeDouble = dailyChange!!.toDouble()

        if (dailyChangeDouble>0){
            binding.stockDailyChange.setTextColor(Color.parseColor("#4CAF50")) //YEŞİL
        }else if (dailyChangeDouble<0){
            binding.stockDailyChange.setTextColor(Color.parseColor("#F44336")) //KIRMIZI
        }else{
            binding.stockDailyChange.setTextColor(Color.parseColor("#9A9A9A")) //GRİ
        }

        val openingPrice = intent.getStringExtra("openingPrice")
        binding.openingPrice.text = dolar+openingPrice.toString()

        val peRatio = intent.getStringExtra("peRatio")
        binding.peRatio.text = peRatio.toString()

        val dailyHighest = intent.getStringExtra("dailyHighest")
        binding.dailyHighest.text = dolar+dailyHighest.toString()

        val dailyLowest = intent.getStringExtra("dailyLowest")
        binding.dailyLowest.text = dolar+dailyLowest.toString()

        val highestPrice = intent.getStringExtra("highestPrice")
        binding.highestPrice.text = dolar+highestPrice.toString()

        val lowestPrice = intent.getStringExtra("highestPrice")
        binding.lowestPrice.text = dolar+lowestPrice.toString()

        val symbol = intent.getStringExtra("symbol")
        binding.stocksymbol.text = symbol.toString()

    }

    fun geri(view: View){
        onBackPressed()
    }

    fun go_sellActivity(view: View){
        val go_sellActivity = Intent(this, SellActivity::class.java)
        startActivity(go_sellActivity)
    }

    fun go_buyActivity(view: View){
        val symbol = intent.getStringExtra("symbol")
        val company = intent.getStringExtra("company")

        val go_buyActivity = Intent(this, BuyActivity::class.java)
        go_buyActivity.putExtra("symbol",symbol)
        go_buyActivity.putExtra("company",company)

        startActivity(go_buyActivity)
    }







}