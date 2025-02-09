package com.example.moneykeepfinalproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FourthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.language_term_template)

        val languageTextView = findViewById<TextView>(R.id.languageTextView4)
        val topicTextView = findViewById<TextView>(R.id.topicTextView4)
        val user_level = findViewById<TextView>(R.id.user_level)

        var language = intent.getStringExtra("language")
        var topic = intent.getStringExtra("topic")
        val userlevel = intent.getStringExtra("level")

        var language1 = language
        var topic1 = topic

        when (topic1) {
            "🌍 Барлығы үшін" -> topic1 = "general"
            "✈️ Саяхат үшін" -> topic1 = "travel"
            "🤝 Адамдармен сөйлесу"-> topic1 = "communication"
            "💼 Мансаптық өсу" -> topic1 = "career"
            "⭐ Өзін-өзі тәрбиелеу" -> topic1 = "self"
            else -> topic1
        }
        when (language1) {
            "🇷🇺 Орыс тілі" -> {
                language1 = "russian"
            }
            "🇬🇧 Ағылшын тілі" -> {
                language1 = "english"
            }
            "🇪🇸 Испан тілі" -> {
                language1 = "spanish"
            }
            "🇩🇪 Неміс тілі" -> {
                language1 = "german"
            } else -> {
            language1
            }
        }
        when (topic) { // оставляем только эмодзи выбранной темы
            "🌍 Барлығы үшін" -> topic = "\uD83C\uDF0D"
            "✈️ Саяхат үшін" -> topic = "✈\uFE0F"
            "🤝 Адамдармен сөйлесу"-> topic = "\uD83E\uDD1D"
            "💼 Мансаптық өсу" -> topic = "\uD83D\uDCBC"
            "⭐ Өзін-өзі тәрбиелеу" -> topic = "⭐"
            else -> topic
        }
        when (language) { // оставляем только флаг изучаемого языка
            "🇷🇺 Орыс тілі" -> {
                language = "\uD83C\uDDF7\uD83C\uDDFA"
            }
            "🇬🇧 Ағылшын тілі" -> {
                language = "\uD83C\uDDEC\uD83C\uDDE7"
            }
            "🇪🇸 Испан тілі" -> {
                language = "\uD83C\uDDEA\uD83C\uDDF8"
            }
            "🇩🇪 Неміс тілі" -> {
                language = "\uD83C\uDDE9\uD83C\uDDEA"
            } else -> {
                language
            }
        }
        var userlevel1 = userlevel
        when(userlevel1) {
            "level1" -> userlevel1 = "1"
            "level2" -> userlevel1 = "2"
            "level3" -> userlevel1 = "3"
            else -> userlevel1
        }

        user_level.text = "You're level is: " + userlevel1
        languageTextView.text = language
        topicTextView.text = topic

        val level1Button = findViewById<Button>(R.id.level1_btn)
        val level2Button = findViewById<Button>(R.id.level2_btn)
        val level3Button = findViewById<Button>(R.id.level3_btn)

        val buttons = listOf(level1Button, level2Button, level3Button)

        buttons.forEach { button ->
            button.setOnClickListener {
                val lg_level = button.text.toString()
                val intent = Intent(this, Card_Activity::class.java)
                intent.putExtra("language", language1) // pass the language
                intent.putExtra("topic", topic1) // pass the term
                intent.putExtra("level", lg_level) // pass the level
                startActivitySlideTransition(intent)
            }
        }


    }

    fun startActivitySlideTransition(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
    }
}