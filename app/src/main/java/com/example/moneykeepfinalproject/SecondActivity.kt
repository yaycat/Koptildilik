package com.example.moneykeepfinalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_page)

        val ruButton = findViewById<Button>(R.id.ru_btn)
        val enButton = findViewById<Button>(R.id.en_btn)
        val spButton = findViewById<Button>(R.id.sp_btn)
        val geButton = findViewById<Button>(R.id.ge_btn)

        val buttons = listOf(ruButton, enButton, spButton, geButton)

        buttons.forEach { button ->
            button.setOnClickListener {
                val language = button.text.toString()
                val intent = Intent(this, ThirdActivity::class.java)
                intent.putExtra("language", language)
                startActivitySlideTransition(intent)
            }
        }
    }

    fun startActivitySlideTransition(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
    }
}