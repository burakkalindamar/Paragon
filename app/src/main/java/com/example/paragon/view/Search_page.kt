package com.example.paragon.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.paragon.R
import com.example.paragon.databinding.ActivitySearchPageBinding

class Search_page : AppCompatActivity() {
    lateinit var binding: ActivitySearchPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun go_balance(view: View){
        val go_balance = Intent(this, MainActivity::class.java)
        startActivity(go_balance)
    }

    fun refresh_search(view: View){
        finish()
        val refresh_search = Intent(this, Search_page::class.java)
        startActivity(refresh_search)
    }

    fun binding(){
        binding=ActivitySearchPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }



}