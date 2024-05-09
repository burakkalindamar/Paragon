package com.example.paragon.view

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.paragon.R
import com.example.paragon.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var dbsql : SQLiteDatabase
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding() //binding işlemlemleri


        //database() // db ve tablo oluştur
        //db.execSQL("INSERT INTO table1 VALUES ('APPLE' , 'APLL', 170.38 , 10.38)")
        //db.execSQL("INSERT INTO table1 VALUES ('BANK OF AMERİCA' , 'BAC', 32.62 , 38.57)"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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

    fun database(){
        context=this
        dbsql=context.openOrCreateDatabase("testdb",Context.MODE_PRIVATE,null)
        dbsql.execSQL("CREATE TABLE IF NOT EXISTS table1(stocks TEXT, symbol TEXT, buying_price REAL, shares REAL)")
    }



}