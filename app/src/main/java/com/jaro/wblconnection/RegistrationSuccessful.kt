package com.jaro.wblconnection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrationSuccessful : AppCompatActivity() {

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration_successful)



        val goToBasicMenu = findViewById<Button>(R.id.btnConfirm)
        val emailText = findViewById<EditText>(R.id.etEmail)
         val licencePlate = findViewById<EditText>(R.id.etLicensePlate)
         emailText.setText(intent.getStringExtra("email address"))
         licencePlate.setText(intent.getStringExtra("licence plate"))


         goToBasicMenu.setOnClickListener {
            val intent = Intent(this, Basic::class.java)
            startActivity(intent)
        }

    }
}