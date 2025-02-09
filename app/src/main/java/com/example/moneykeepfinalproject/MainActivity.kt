package com.example.moneykeepfinalproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.moneykeepfinalproject.ui.theme.MoneyKeepFinalProjectTheme
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_page)

        val startButton = findViewById<Button>(R.id.next_btn)
        val loginbtn = findViewById<Button>(R.id.login_button)


        loginbtn.setOnClickListener {
            val intent = Intent(this, Log_Activity::class.java)
            startActivitySlideTransition(intent)
        }

        startButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivitySlideTransition(intent)
        }

    }

    fun startActivitySlideTransition(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
    }

}