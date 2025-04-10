package com.jaro.wblconnection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Registration : AppCompatActivity() {

    private lateinit var backButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)

        val goToQRCanner = findViewById<Button>(R.id.button_qr_scanner)


        goToQRCanner.setOnClickListener {
            val intent = Intent(this, Registration_with_QR_code::class.java)
            startActivity(intent)
        }

        backButton = findViewById<Button>(R.id.button_back)
        backButton.setOnClickListener {
            finish() // ← Ez bezárja az aktuális Activity-t, és visszalép az előzőre
        }

    }
}