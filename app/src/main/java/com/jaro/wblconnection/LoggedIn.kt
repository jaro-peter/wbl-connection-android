package com.jaro.wblconnection

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.parse.ParseUser


class LoggedIn : AppCompatActivity() {
    interface UserQueryCallback {
        fun onSuccess(user: ParseUser)
        fun onError(errorMessage: String)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_logged_in)

        val licensePlateField = findViewById<EditText>(R.id.editTextLicensePlate)
        val sendMessageButton = findViewById<Button>(R.id.sendMessageButton)
        val myMessagesButton = findViewById<Button>(R.id.myMessagesButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val changePasswordText = findViewById<TextView>(R.id.changePasswordText)
        licensePlateField.setText(intent.getStringExtra("licencePlate"))


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sendMessageButton.setOnClickListener {


            val intent = Intent(this, SendMessageLicencePlate::class.java)
            intent.putExtra("sessionToken", this.intent.getStringExtra("sessionToken"))
            intent.putExtra("objID",this.intent.getStringExtra("objID"))
            intent.putExtra("licencePlate", this.intent.getStringExtra("licencePlate"))
            startActivity(intent)
        }



        settingsButton.setOnClickListener {
            val licensePlate =  intent.getStringExtra("licencePlate")+""

            getUserByUsername(licensePlate, object : UserQueryCallback {
                override fun onSuccess(user: ParseUser) {
                    Log.d("Parse", "objectId: ${user.objectId}")
                    Log.d("Parse", "username: ${user.username}")
                }

                override fun onError(errorMessage: String) {
                    Log.e("Parse", "Hiba: $errorMessage")
                }
            })
        }

        logoutButton.setOnClickListener {
            val intent = Intent(this, Basic::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            clearSessionData()
            finish()

        }

        changePasswordText.setOnClickListener {
            // TODO
        }
        myMessagesButton.setOnClickListener(){
         val intent = Intent(this, MyMessages::class.java)
            intent.putExtra("sessionToken", this.intent.getStringExtra("sessionToken"))
            intent.putExtra("licencePlate", this.intent.getStringExtra("licencePlate") )
            intent.putExtra("objID",this.intent.getStringExtra("objID"))
            startActivity(intent)


        }
    }

    // 👇 Ez legyen kívül az onCreate-ből!


    fun getUserByUsername(licencePlateField: String, callback: UserQueryCallback) {
        val query = ParseUser.getQuery()
        query.whereEqualTo("username", licencePlateField)

        query.getFirstInBackground { user, e ->
            if (e == null && user != null) {
                callback.onSuccess(user)
            } else {
                callback.onError(e?.message ?: "Ismeretlen hiba")
            }
        }
    }
    fun clearSessionData() {
        val prefs: SharedPreferences = getSharedPreferences("my_app_prefs", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.remove("session_token")
        editor.remove("object_id")
        editor.remove("licence_plate")
        editor.apply() // or .commit()
    }
}
