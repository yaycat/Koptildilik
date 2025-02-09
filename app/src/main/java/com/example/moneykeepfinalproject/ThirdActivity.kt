package com.example.moneykeepfinalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.therd_page)

        val languageTextView = findViewById<TextView>(R.id.languageTextView)
        val language = intent.getStringExtra("language")

        languageTextView.text = language

        val allButton = findViewById<Button>(R.id.all_btn)
        val travelButton = findViewById<Button>(R.id.travel_btn)
        val talkButton = findViewById<Button>(R.id.talk_btn)
        val careerButton = findViewById<Button>(R.id.career_btn)
        val selfButton = findViewById<Button>(R.id.self_btn)

        val buttons = listOf(allButton, travelButton, talkButton, careerButton, selfButton)

        buttons.forEach { button ->
            button.setOnClickListener {
                val topic = button.text.toString()
                val intent = Intent(this, Signup_Actitvity::class.java)
                intent.putExtra("language", language) // Передаем язык
                intent.putExtra("topic", topic) // Передаем тему
                intent.putExtra("level", "1") // Передаем уровень
                startActivitySlideTransition(intent)
            }
        }
    }

    fun startActivitySlideTransition(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
    }
}