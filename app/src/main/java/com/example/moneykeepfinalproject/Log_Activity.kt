package com.example.moneykeepfinalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.SecureRandom
import android.util.Base64

class Log_Activity : ComponentActivity() {
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        val sign_btn = findViewById<Button>(R.id.sign_btn)
        val login_text = findViewById<EditText>(R.id.LoginEdit)
        val pass_text = findViewById<EditText>(R.id.PassEdit)

        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts")

        sign_btn.setOnClickListener {
            val email = login_text.text.toString()
            val password = pass_text.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginAccount(email, password)
            } else {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginAccount(email: String, password: String) {
        Log.d("LoginActivity", "loginAccount START - Email: $email")
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("LoginActivity", "onDataChange - snapshot.exists(): ${snapshot.exists()}")
                    if (snapshot.exists()) {
                        var accountFound = false
                        for (accountSnapshot in snapshot.children) {
                            val storedEmail = accountSnapshot.child("email").value as? String
                            val storedPasswordHashString = accountSnapshot.child("passwordHash").value as? String
                            val storedSaltString = accountSnapshot.child("salt").value as? String
                            val language = accountSnapshot.child("language").value as? String ?: "english"
                            val topic = accountSnapshot.child("topic").value as? String ?: "career"

                            Log.d("LoginActivity", "Data from Firebase - email: $storedEmail, hash: $storedPasswordHashString, salt: $storedSaltString") // Логируем данные, полученные из Firebase

                            if (storedPasswordHashString != null && storedSaltString != null) {
                                try {
                                    val storedSalt = Base64.decode(storedSaltString, Base64.DEFAULT)
                                    val enteredPasswordHash = hashPassword(password, storedSalt)
                                    val enteredPasswordHashString = Base64.encodeToString(enteredPasswordHash, Base64.DEFAULT)


                                    if (enteredPasswordHashString == storedPasswordHashString) {
                                        accountFound = true
                                        Toast.makeText(this@Log_Activity, "Successful login!", Toast.LENGTH_SHORT).show()
                                        navigateToCardActivity(language, topic)
                                        return
                                    } else {
                                        Log.d("LoginActivity", "Passwords do NOT match!")
                                    }
                                } catch (e: NoSuchAlgorithmException) {
                                    Log.e("HashingError", "NoSuchAlgorithmException: ", e)
                                } catch (e: InvalidKeySpecException) {
                                    Log.e("HashingError", "InvalidKeySpecException: ", e)
                                }
                            } else {
                            }
                        }
                        if (!accountFound) {
                            Toast.makeText(this@Log_Activity, "Incorrect password", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this@Log_Activity, "Account not found", Toast.LENGTH_SHORT).show()
                        Log.d("LoginActivity", "snapshot.exists() is false - Account not found Toast shown")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Log_Activity, "Error connecting to database", Toast.LENGTH_SHORT).show()
                    Log.e("Firebase", "Database query error: ", error.toException())
                    Log.d("LoginActivity", "onCancelled - Connection error Toast shown")
                }
            })
        Log.d("LoginActivity", "loginAccount END") // Логируем конец функции
    }

    private fun navigateToCardActivity(language: String, topic: String) {
        val intent = Intent(this, FourthActivity::class.java)
        intent.putExtra("language", language)
        intent.putExtra("topic", topic)
        intent.putExtra("level", "Level 1") // Передаем уровень "Level 1"
        startActivitySlideTransition(intent)
        finish() // Закрываем Log_Activity
    }


    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    private fun hashPassword(password: String, salt: ByteArray): ByteArray {
        val keyLength = 256
        val iterations = 10000
        val passwordChars = password.toCharArray()

        val spec = PBEKeySpec(passwordChars, salt, iterations, keyLength)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = factory.generateSecret(spec)
        return key.encoded
    }

    fun startActivitySlideTransition(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
    }
}