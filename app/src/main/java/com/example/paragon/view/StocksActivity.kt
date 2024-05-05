package com.example.paragon.view

import android.graphics.Color
import android.os.Bundle
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
        setContentView(R.layout.activity_stocks)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding()
        line_chart()
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









}