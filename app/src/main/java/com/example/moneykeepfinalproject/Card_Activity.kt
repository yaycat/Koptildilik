package com.example.moneykeepfinalproject

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.Animator.AnimatorListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Card_Activity : AppCompatActivity() {
    private lateinit var frontTextView: TextView
    private lateinit var backTextView: TextView
    private var isFlipped = false
    private lateinit var flipIn: AnimatorSet
    private lateinit var flipOut: AnimatorSet
    private var currentIndex = 0
    private var nowword = 0
    private var lgLevel = "1"
    private lateinit var language: String
    private lateinit var topic: String
    private var words = mutableListOf<Pair<String, String>>() // Список слов
    private lateinit var database: DatabaseReference // База данных Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_page)

        backTextView = findViewById(R.id.backTextView)
        backTextView.isClickable = false;

        val languageTextView = findViewById<TextView>(R.id.languageTextView2)
        val topicTextView = findViewById<TextView>(R.id.topicTextView)
        frontTextView = findViewById(R.id.frontTextView)
        backTextView = findViewById(R.id.backTextView)


        language = intent.getStringExtra("language") ?: "no"
        topic = intent.getStringExtra("topic") ?: "no"
        lgLevel = intent.getStringExtra("level") ?: "-1"


        when (lgLevel) {
            "Level 1" -> lgLevel = "level1"
            "Level 2" -> lgLevel = "level2"
            "Level 3" -> lgLevel = "level3"
            else -> lgLevel
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

        Log.d("test", "test")


        database = FirebaseDatabase.getInstance().reference.child("languages").child("kazakh").child("topics") // Инициализируем базу данных

        loadWordsFromFirebase()


        languageTextView.text = getLanguageFlag(language)
        topicTextView.text = getTopicEmoji(topic)


        try {
            flipIn = AnimatorInflater.loadAnimator(this, R.animator.flip_in) as AnimatorSet
            flipOut = AnimatorInflater.loadAnimator(this, R.animator.flip_out) as AnimatorSet
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }

        frontTextView.setOnClickListener {
            Log.d("CardClick", "frontTextView Clicked. isFlipped: $isFlipped, currentIndex: $currentIndex")
            if (!isFlipped) { // Если карточка не перевернута (показываем перевод)
                Log.d("CardClick", "frontTextView: The card is not turned over. We show the translation.")
                flipCard(frontTextView, backTextView)
                isFlipped = true // Устанавливаем флаг, что карточка перевернута

                if (currentIndex == words.size - 1) {
                    Log.d("CardClick", "frontTextView: Последнее слово. Переход к goToNextLevel()")
                    goToNextLevel()
                }

            }
            Log.d("CardClick", "frontTextView: End of click processing. isFlipped: $isFlipped, currentIndex: $currentIndex")
        }

        backTextView.setOnClickListener {
            Log.d("CardClick", "backTextView Clicked. isFlipped: $isFlipped, currentIndex: $currentIndex")
            if (isFlipped) {
                Log.d("CardClick", "backTextView: The card is turned over. We move on to the next word..")
                nowword = currentIndex
                currentIndex++ // Переходим к следующему слову
                Log.d("CardClick", "backTextView: currentIndex after increment: $currentIndex")

                if (currentIndex < words.size) { // Проверяем, не вышли ли за пределы списка
                    Log.d("CardClick", "backTextView: currentIndex ($currentIndex) < words.size (${words.size}). Вызов showNextWord()")

                    flipCard(backTextView, frontTextView)
                    showNextWord()
                    isFlipped = false
                } else {
                    Log.d("CardClick", "backTextView: currentIndex ($currentIndex) >= words.size (${words.size}). Transition to goToNextLevel()")
                    goToNextLevel()
                }
            } else {
                Log.w("CardClick", "backTextView: Clicking on backTextView when the card is NOT flipped. This should not happen in a normal scenario.");
            }
            Log.d("CardClick", "backTextView: End of click processing. isFlipped: $isFlipped, currentIndex: $currentIndex")
        }
    }

    private fun loadWordsFromFirebase() {
        val databasePath = database.child(topic).child(lgLevel).toString()
        Log.d("Firebase", "Database Path: $databasePath")
        database.child(topic).child(lgLevel).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Firebase", "DataSnapshot: ${snapshot.value}")
                words.clear()
                for (wordSnapshot in snapshot.children) {
                    val kazakhWord = wordSnapshot.child("kazakh").getValue(String::class.java) ?: ""
                    val translatedWord = wordSnapshot.child(language).getValue(String::class.java) ?: ""
                    Log.d("Firebase", "Kazakh Word: $kazakhWord, Translated Word ($language): $translatedWord")

                    words.add(Pair(kazakhWord, translatedWord))
                }
                if (words.isNotEmpty()) {
                    showNextWord()
                }else {
                    Log.w("Firebase", "No words found for topic: $topic, level: $lgLevel")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                Log.e("Firebase", "DatabaseError: ${error.message}")
            }
        })
    }


    private fun showNextWord() {
        if (words.isNotEmpty()) {
            frontTextView.text = "( ${currentIndex + 1} / ${words.size} ) " + words[currentIndex].first
            backTextView.text = words[currentIndex].second
        }
    }

    private fun flipCard(from: View, to: View) {
        flipOut.setTarget(from)
        flipIn.setTarget(to)

        flipIn.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {
                from.isClickable = false;
                to.isClickable = false;
            }
            override fun onAnimationEnd(animation: android.animation.Animator) {
                to.isClickable = true;
                flipIn.removeListener(this)
            }
            override fun onAnimationCancel(animation: android.animation.Animator) {
                to.isClickable = false;
            }
            override fun onAnimationRepeat(animation: android.animation.Animator) {
            }
        })


        flipOut.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {
                to.isClickable = false;
                from.isClickable = false;
            }
            override fun onAnimationEnd(animation: android.animation.Animator) {
                from.isClickable = true;
                flipOut.removeListener(this)
            }
            override fun onAnimationCancel(animation: android.animation.Animator) {
                from.isClickable = false;
            }
            override fun onAnimationRepeat(animation: android.animation.Animator) {
            }
        })


        flipOut.start()
        flipIn.start()
    }

    private fun goToNextLevel() {
        val newLevel = when (lgLevel) {
            "level1" -> "level2"
            "level2" -> "level3"
            else -> "level1"
        }
        val intent = Intent(this, FourthActivity::class.java)
        intent.putExtra("level", newLevel)
        intent.putExtra("language", language) // Передаем язык дальше
        intent.putExtra("topic", topic) // Передаем тему дальше
        startActivitySlideTransition(intent)
        finish()
    }
    fun startActivitySlideTransition(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
    }
}

private fun getLanguageFlag(langCode: String): String {
    return when (langCode) {
        "russian" -> "\uD83C\uDDF7\uD83C\uDDFA"
        "english" -> "\uD83C\uDDEC\uD83C\uDDE7"
        "spanish" -> "\uD83C\uDDEA\uD83C\uDDF8"
        "german" -> "\uD83C\uDDE9\uD83C\uDDEA"
        else -> ""
    }
}

private fun getTopicEmoji(topicCode: String): String {
    return when (topicCode) {
        "general" -> "🌍"
        "travel" -> "✈️"
        "communication" -> "🤝"
        "\uD83D\uDCBC" -> "💼"
        "⭐" -> "⭐"
        else -> ""
    }
}
