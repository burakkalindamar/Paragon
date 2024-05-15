package com.example.paragon.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.paragon.R
import com.example.paragon.databinding.ActivityDataBinding
import com.example.paragon.databinding.ActivitySearchPageBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class DataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataBinding
    private val db = Firebase.firestore
    val symbol = intent.getStringExtra("symbol")
    val company = intent.getStringExtra("company")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tahminleri_al()
        temettuleri_al()

    }

    fun binding() {
        binding = ActivityDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    @SuppressLint("SetTextI18n")
    fun tahminleri_al() {
        val symbol = intent.getStringExtra("symbol")
        val company = intent.getStringExtra("company")
        binding.stock.text = company
        val docRef = db.collection("Data").document(symbol!!)
        var al = 0
        var sat = 0
        var tut = 0

        docRef.get()
            .addOnSuccessListener { document ->
                val tahminler = document.get("tahminler") as? ArrayList<Any>
                if (tahminler != null) {
                    al = (tahminler[0] as Long).toInt() + (tahminler[1] as Long).toInt()
                    tut = (tahminler[2] as Long).toInt()
                    sat = (tahminler[3] as Long).toInt() + (tahminler[4] as Long).toInt()

                    val toplam = al + sat + tut

                    binding.albar.max = toplam
                    binding.albar.progress = al
                    binding.alsayi.text = "($al Analist)"

                    binding.satbar.max = toplam
                    binding.satbar.progress = sat
                    binding.satsayi.text = "($sat Analist)"

                    binding.tutbar.max = toplam
                    binding.tutbar.progress = tut
                    binding.tutsayi.text = "($tut Analist)"
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Veriler Alınırken Hata Oluştu: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    @SuppressLint("SetTextI18n")
    fun temettuleri_al() {
        val symbol = intent.getStringExtra("symbol")

        if (symbol != null) {
            val docRef = db.collection("Data").document(symbol)
            docRef.get()
                .addOnSuccessListener { document ->
                    val temettuler = document["dividends"] as? ArrayList<HashMap<String, Any>>
                    if (temettuler != null) {
                        for (i in 0 until temettuler.size) {
                            val fiyat = temettuler[i]["Fiyat"] as? Double
                            val tarih = temettuler[i]["Tarih"] as? String

                            val fiyatTextView = findViewById<TextView>(
                                resources.getIdentifier(
                                    "fiyat${i + 1}",
                                    "id",
                                    packageName
                                )
                            )
                            val tarihTextView = findViewById<TextView>(
                                resources.getIdentifier(
                                    "temettü${i + 1}",
                                    "id",
                                    packageName
                                )
                            )

                            fiyatTextView?.text = "$${fiyat.toString()}"
                            tarihTextView?.text = tarih
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this,
                        "Veriler Alınırken Hata Oluştu: ${exception.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            Toast.makeText(this, "Sembol bilgisi yok", Toast.LENGTH_LONG).show()
        }
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

    fun geri(view: View) {
        onBackPressed()
    }

}

