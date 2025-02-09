package com.example.moneykeepfinalproject

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import android.util.Base64

class Signup_Actitvity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_page)

        val signup_btn = findViewById<Button>(R.id.signup_btn)
        val signup_login_text = findViewById<EditText>(R.id.signupLoginEdit)
        val signup_email_text = findViewById<EditText>(R.id.signupEmailEdit)
        val signup_pass_text = findViewById<EditText>(R.id.signupPassEdit)

        val topic = intent.getStringExtra("topic") ?: "career" // Get topic from Intent
        val language = intent.getStringExtra("language") ?: "english"

        databaseReference = FirebaseDatabase.getInstance().getReference("Accounts")

        signup_btn.setOnClickListener {
            val login = signup_login_text.text.toString()
            val email = signup_email_text.text.toString()
            val password = signup_pass_text.text.toString()

            if (login.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) { // Check for empty fields
                signupAccount(login, email, password, language, topic) // Calling the registration function
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun signupAccount(login: String, email: String, password: String, language: String, topic: String) {
        databaseReference.child(login).get().addOnSuccessListener {
            if (it.exists()) {
                Toast.makeText(this@Signup_Actitvity, "Login is busy. Choose another login.", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val salt = generateSalt()
                    val passwordHash = hashPassword(password, salt)
                    val saltString = Base64.encodeToString(salt, Base64.DEFAULT)
                    val passwordHashString = Base64.encodeToString(passwordHash, Base64.DEFAULT)

                    val userData = HashMap<String, Any>()
                    userData["login"] = login
                    userData["email"] = email
                    userData["passwordHash"] = passwordHashString
                    userData["salt"] = saltString
                    userData["language"] = language
                    userData["topic"] = topic

                    databaseReference.child(login).setValue(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this@Signup_Actitvity, "Account successfully registered!", Toast.LENGTH_SHORT).show()
                            navigateToFourthActivity(topic, language)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@Signup_Actitvity, "Registration error. Try again..", Toast.LENGTH_SHORT).show()
                            Log.e("Firebase", "Error writing registration data: ", it)
                        }

                } catch (e: NoSuchAlgorithmException) {
                    Toast.makeText(this@Signup_Actitvity, "Password hashing error.", Toast.LENGTH_SHORT).show()
                    Log.e("HashingError", "NoSuchAlgorithmException: ", e)
                } catch (e: InvalidKeySpecException) {
                    Toast.makeText(this@Signup_Actitvity, "Password hashing error.", Toast.LENGTH_SHORT).show()
                    Log.e("HashingError", "InvalidKeySpecException: ", e)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this@Signup_Actitvity, "Database connection error.", Toast.LENGTH_SHORT).show()
            Log.e("Firebase", "Login verification error: ", it)
        }
    }

    private fun navigateToFourthActivity(topic: String, language: String) {
        val intent = Intent(this, FourthActivity::class.java)
        intent.putExtra("topic", topic)
        intent.putExtra("language", language)
        startActivitySlideTransition(intent)
        finish()
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