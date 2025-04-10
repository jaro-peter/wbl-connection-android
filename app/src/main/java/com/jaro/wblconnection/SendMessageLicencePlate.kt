package com.jaro.wblconnection

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.SaveCallback

class SendMessageLicencePlate : AppCompatActivity() {
    private lateinit var sendButton1 : Button
    private lateinit var sendButton2 : Button
    private lateinit var sendButton3 : Button
    private lateinit var sendButton4 : Button
    private lateinit var sendButton5 : Button
    private lateinit var sendButton6 : Button
    private lateinit var sendButton7 : Button
    private lateinit var sendButton8 : Button
    private lateinit var sendButton9 : Button
    private lateinit var sendButton10 : Button
    private lateinit var sendButton11 : Button
    private lateinit var sendButton12 : Button

    private lateinit var backButton : Button
    private lateinit var toLicencePlate : EditText
    private lateinit var messageText1 : TextView
    private lateinit var messageText2 : TextView
    private lateinit var messageText3 : TextView
    private lateinit var messageText4 : TextView
    private lateinit var messageText5 : TextView
    private lateinit var messageText6 : TextView
    private lateinit var messageText7 : TextView
    private lateinit var messageText8 : TextView
    private lateinit var messageText9 : TextView
    private lateinit var messageText10 : TextView
    private lateinit var messageText11 : TextView
    private lateinit var messageText12 : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_send_message_licence_plate)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        backButton = findViewById<Button>(R.id.buttonBack)
        backButton.setOnClickListener(){
            finish()

        }

        sendButton1 = findViewById(R.id.sendButton1)
        sendButton2 = findViewById(R.id.sendButton2)
        sendButton3 = findViewById(R.id.sendButton3)
        sendButton4 = findViewById(R.id.sendButton4)
        sendButton5 = findViewById(R.id.sendButton5)
        sendButton6 = findViewById(R.id.sendButton6)
        sendButton7 = findViewById(R.id.sendButton7)
        sendButton8 = findViewById(R.id.sendButton8)
        sendButton9 = findViewById(R.id.sendButton9)
        sendButton10 = findViewById(R.id.sendButton10)
        sendButton11 = findViewById(R.id.sendButton11)
        sendButton12 = findViewById(R.id.sendButton12)

        messageText1 = findViewById(R.id.message1)
        messageText2 = findViewById(R.id.message2)
        messageText3 = findViewById(R.id.message3)
        messageText4 = findViewById(R.id.message4)
        messageText5 = findViewById(R.id.message5)
        messageText6 = findViewById(R.id.message6)
        messageText7 = findViewById(R.id.message7)
        messageText8 = findViewById(R.id.message8)
        messageText9 = findViewById(R.id.message9)
        messageText10 = findViewById(R.id.message10)
        messageText11 = findViewById(R.id.message11)
        messageText12 = findViewById(R.id.message12)


        toLicencePlate = findViewById(R.id.editTextLicensePlateReceiver)
        sendButton1.setOnClickListener(){
           sendMessage(messageText1.text.toString())
        }
        sendButton2.setOnClickListener(){
           sendMessage(messageText2.text.toString())

        }
        sendButton3.setOnClickListener(){
            sendMessage(messageText3.text.toString())
        }
        sendButton4.setOnClickListener(){
            sendMessage(messageText4.text.toString())
        }
        sendButton5.setOnClickListener(){
            sendMessage(messageText5.text.toString())
        }
        sendButton6.setOnClickListener(){
            sendMessage(messageText6.text.toString())
        }
        sendButton7.setOnClickListener(){
            sendMessage(messageText7.text.toString())
        }
        sendButton8.setOnClickListener(){
            sendMessage(messageText8.text.toString())
        }
        sendButton9.setOnClickListener(){
            sendMessage(messageText9.text.toString())
        }
        sendButton10.setOnClickListener(){
            sendMessage(messageText10.text.toString())
        }


    }
    fun sendMessage(message :String){
        val messageObject = ParseObject("Messages")

        // Set the values for each column
        messageObject.put("From", this.intent.getStringExtra("licencePlate").toString()  )
        messageObject.put("To", toLicencePlate.text.toString())
        messageObject.put("Message", message)
        messageObject.put("Rating", false)

        // Save the object in background
        messageObject.saveInBackground(object : SaveCallback {
            override fun done(e: ParseException?) {
                if (e == null) {
                    // Success - message saved
                    println("Message saved successfully!")
                } else {
                    // Error occurred
                    println("Error saving message: ${e.message}")
                }
            }
        })
    }

}