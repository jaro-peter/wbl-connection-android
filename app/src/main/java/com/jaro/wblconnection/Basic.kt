package com.jaro.wblconnection

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseObject
import com.parse.ParseUser


class Basic : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_basic)

        val (token, objId, licencePlate) = getData()

        if (token.isNotEmpty()) {
            val intent = Intent(this, LoggedIn::class.java)
            intent.putExtra("sessionToken", token)
            intent.putExtra("objID", objId)
            intent.putExtra("licencePlate", licencePlate)
            startActivity(intent)
            finish()
        }
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val sendMessageLicencePlate = findViewById<Button>(R.id.sendMessageButton)


        signUpButton.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }

        sendMessageLicencePlate.setOnClickListener{
            val intent = Intent(this,SendMessageLicencePlate::class.java)
            startActivity(intent)

        }

        val signInButton =findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener{
            val licencePlate = findViewById<EditText>(R.id.licensePlate)
            val password = findViewById<EditText>(R.id.password)
            ParseUser.logInInBackground(licencePlate.text.toString(),
                password.text.toString()
            ) { user, e ->
                if (e == null && user != null) {
                    val sessionToken = user.sessionToken
                    val objID = user.objectId
                    val licencePlate= user.username
                    savedSessionData(sessionToken,objID,licencePlate)
                    Toast.makeText(this, "✅ Sikeres bejelentkezés", Toast.LENGTH_SHORT).show()
                    Log.d("SessionToken", "Session ID: $sessionToken")

                    // You can now navigate to the next screen and pass sessionToken if needed
                    val intent = Intent(this, LoggedIn::class.java)
                    intent.putExtra("sessionToken", sessionToken)
                    intent.putExtra("objID",objID)
                    intent.putExtra("licencePlate", licencePlate)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Valamiért sikertelen volt a bejelentkezés", Toast.LENGTH_LONG).show()
                }
            }





        }
      /*  val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener{
            val obj = ParseObject("Messages")
            obj.put("From", "AndroidApp") // nagybetűs!
            obj.put("To", "UserXYZ")
            obj.put("Message", "Minden király: ${System.currentTimeMillis()}")
            obj.saveInBackground { e ->
                if (e == null) {
                    Log.d("ParseDebug", "✅ Sikeresen mentve")
                } else {
                    Log.e("ParseDebug", "❌ Hiba: ${e.localizedMessage}")
                }
            }

        }*/
    }
    fun savedSessionData(savingToken: String,objectId:String, licencePlate:String) {
        val prefs : SharedPreferences = getSharedPreferences ("my_app_prefs",MODE_PRIVATE );
        val editor :SharedPreferences.Editor  = prefs.edit();
        editor.putString("session_token", savingToken);
        editor.putString("object_id",objectId)
        editor.putString("licence_plate",licencePlate)
        editor.apply(); // or .commit()

    }
    fun getData(): Triple<String, String, String> {
        val prefs = getSharedPreferences("my_app_prefs", MODE_PRIVATE)
        val token = prefs.getString("session_token", "") ?: ""
        val objId = prefs.getString("object_id", "") ?: ""
        val licencePlate = prefs.getString("licence_plate", "") ?: ""
        return Triple(token, objId, licencePlate)
    }




}