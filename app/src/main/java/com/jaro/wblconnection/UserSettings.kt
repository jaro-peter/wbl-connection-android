package com.jaro.wblconnection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
 import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseUser
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.widget.SwitchCompat




class UserSettings : AppCompatActivity() {
    private lateinit var switchVolume: SwitchCompat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_settings)

        val backButton = findViewById<Button>(R.id.button_back)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)
        switchVolume = findViewById(R.id.switchVolume)

        val licencePlateText = findViewById<TextView>(R.id.editTextUserLicencePlate)
        val ratingText = findViewById<TextView>(R.id.editTextUserRating)

        val licencePlate = intent.getStringExtra("licencePlate") ?: "N/A"
        licencePlateText.text = licencePlate

        // Lekérjük a felhasználó értékelését
        val query = ParseUser.getQuery()
        query.whereEqualTo("username", licencePlate)
        query.getFirstInBackground { user, e ->
            if (e == null && user != null) {
                val rating = user.getInt("rating")
                ratingText.text = rating.toString()
            } else {
                Log.e("UserSettings", "Error retrieving user: ${e?.message}")
                ratingText.text = "0"
            }
        }

        // ⬇️ Betöltés SharedPreferences-ből
        val prefs = getSharedPreferences("my_app_prefs", MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("notifications_enabled", true)
        switchVolume.isChecked = isEnabled
        updateSwitchColor(isEnabled)

        // ⬇️ Kapcsoló változásának kezelése
        switchVolume.setOnCheckedChangeListener { _, isChecked ->
            updateSwitchColor(isChecked)
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply()
        }

        // ⬇️ Vissza a főmenübe
        backButton.setOnClickListener {
            val intent = Intent(this, LoggedIn::class.java)
            intent.putExtra("licencePlate", licencePlate)
            intent.putExtra("sessionToken", intent.getStringExtra("sessionToken"))
            intent.putExtra("objID", intent.getStringExtra("objID"))
            startActivity(intent)
            finish()
        }

        // OK gomb funkció
        btnConfirm.setOnClickListener {
            finish()
        }
    }


    fun updateSwitchColor(isChecked: Boolean) {
        val thumbColor = if (isChecked) Color.GREEN else Color.RED
        val trackColor = if (isChecked) Color.parseColor("#AAFFAA") else Color.parseColor("#FFAAAA")

        switchVolume.thumbTintList = ColorStateList.valueOf(thumbColor)
        switchVolume.trackTintList = ColorStateList.valueOf(trackColor)
    }
}
