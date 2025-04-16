package com.jaro.wblconnection

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.jaro.wblconnection.Basic
import com.jaro.wblconnection.LoggedIn
import com.jaro.wblconnection.R

import com.parse.ParseUser


class SendMessageSuccessful : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message_successful)

        val licence = intent.getStringExtra("receiverPlate")
        val message = intent.getStringExtra("message")

        val licenceField = findViewById<EditText>(R.id.editTextLicensePlateReceiver)
        val messageField = findViewById<EditText>(R.id.etLicensePlate)

        licenceField.setText(licence)
        messageField.setText(message)

        val confirmButton = findViewById<Button>(R.id.btnConfirm)
        confirmButton.setOnClickListener {
            navigateAfterConfirmation()

        }
    }
    private fun navigateAfterConfirmation() {
        val sessionToken = intent.getStringExtra("sessionToken")

        val intent = if (sessionToken != null ) {
            val currentUser = ParseUser.getCurrentUser()
            // Bejelentkezett felhasználó
            Intent(this, LoggedIn::class.java).apply {
                putExtra("sessionToken", currentUser.sessionToken)
                putExtra("licencePlate", currentUser.username)
                putExtra("objID", currentUser.objectId)
            }
        } else {
            // Nincs bejelentkezett felhasználó
            Intent(this, Basic::class.java)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
