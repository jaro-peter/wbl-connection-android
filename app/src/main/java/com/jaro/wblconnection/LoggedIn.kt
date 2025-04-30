package com.jaro.wblconnection

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.*
import kotlin.concurrent.fixedRateTimer




class LoggedIn : AppCompatActivity() {
    interface UserQueryCallback {
        fun onSuccess(user: ParseUser)
        fun onError(errorMessage: String)
    }
    private var messageTimer: Timer? = null




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

        val licencePlate = intent.getStringExtra("licencePlate") ?: return
        messageTimer = fixedRateTimer("messageChecker", true, 0L, 10000L) {
            checkForNewMessages(licencePlate)
        }

        sendMessageButton.setOnClickListener {


            val intent = Intent(this, SendMessageLicencePlate::class.java)
            intent.putExtra("sessionToken", this.intent.getStringExtra("sessionToken"))
            intent.putExtra("objID",this.intent.getStringExtra("objID"))
            intent.putExtra("licencePlate", this.intent.getStringExtra("licencePlate"))
            startActivity(intent)
        }



        settingsButton.setOnClickListener {
            val licensePlate = intent.getStringExtra("licencePlate") ?: return@setOnClickListener

            getUserByUsername(licensePlate, object : UserQueryCallback {
                override fun onSuccess(user: ParseUser) {
                    val intent = Intent(this@LoggedIn, UserSettings::class.java)
                    intent.putExtra("licencePlate", user.username)
                    intent.putExtra("objID", user.objectId)
                    intent.putExtra("sessionToken", user.sessionToken)
                    startActivity(intent)
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

        }
        myMessagesButton.setOnClickListener(){
         val intent = Intent(this, MyMessages::class.java)
            intent.putExtra("sessionToken", this.intent.getStringExtra("sessionToken"))
            intent.putExtra("licencePlate", this.intent.getStringExtra("licencePlate") )
            intent.putExtra("objID",this.intent.getStringExtra("objID"))
            startActivity(intent)


        }
    }
    override fun onDestroy() {
        super.onDestroy()
        messageTimer?.cancel()
        messageTimer = null
    }


    fun checkForNewMessages(licencePlate: String) {
        val query = ParseQuery.getQuery<ParseObject>("Messages")
        query.whereEqualTo("To", licencePlate)
        query.orderByDescending("createdAt")
        query.setLimit(1)

        query.findInBackground { messages, e ->
            if (e == null && messages.isNotEmpty()) {
                val latestMessage = messages.first()
                val seen = latestMessage.getBoolean("Seen")

                if (!seen) {
                    latestMessage.put("Seen", true)
                    latestMessage.saveInBackground()

                    val prefs = getSharedPreferences("my_app_prefs", MODE_PRIVATE)
                    val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)
                    if (notificationsEnabled) {
                        runOnUiThread {
                            playNotificationSound(this)
                        }
                    }
                }
            }
        }
    }


    fun playNotificationSound(context: Context) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.notification_beep2)
        mediaPlayer.start()
    }


    fun getUserByUsername(licencePlateField: String, callback: UserQueryCallback) {
        val query = ParseUser.getQuery()
        query.whereEqualTo("username", licencePlateField)

        query.getFirstInBackground { user, e ->
            if (e == null && user != null) {
                callback.onSuccess(user)
            } else {
                callback.onError(e?.message ?: getString(R.string.unknown_error))
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
