package com.jaro.wblconnection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.SaveCallback
import androidx.core.content.ContextCompat




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
        toLicencePlate = findViewById(R.id.editTextLicensePlateReceiver)
        val existenceText = findViewById<TextView>(R.id.editTextLicencePlateExist)

        // Automatikus nagybetűssé alakítás
        toLicencePlate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                val upper = text.uppercase()
                if (text != upper) {
                    toLicencePlate.setText(upper)
                    toLicencePlate.setSelection(upper.length)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        toLicencePlate.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                // ha elvesztette a fókuszt, rejtsük el a billentyűzetet
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        val color = ContextCompat.getColor(this, R.color.red)
        existenceText.setTextColor(color)
        toLicencePlate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val plate = s.toString().trim().uppercase()

                if (plate.length >= 1) {
                    Log.d("PlateDebug", "Indul a lekérdezés: $plate")
                    val query = com.parse.ParseUser.getQuery()
                    query.whereStartsWith("username", plate)

                    query.getFirstInBackground { user, e ->
                        if (e == null && user != null && user.username.equals(plate)) {
                            Log.d("PlateDebug", "✅ Sikeres lekérdezés - Hozzáférés engedélyezve a felhasználóhoz: ${user.username}")
                            existenceText.text = getString(R.string.exist)
                            existenceText.setTextColor(ContextCompat.getColor(this@SendMessageLicencePlate, R.color.green))
                        } else if(e == null && user != null && user.username.startsWith(plate))  {
                            if (e != null) {
                                Log.e("PlateDebug", "❌ Hiba történt: ${e.localizedMessage}")
                            } else {
                                Log.w("PlateDebug", "⚠️ Nincs találat, vagy nincs hozzáférés (user == null)")
                            }

                            existenceText.text = getString(R.string.keep_writting)
                            existenceText.setTextColor(ContextCompat.getColor(this@SendMessageLicencePlate, R.color.yellow))

                        }else {
                            existenceText.text = getString(R.string.doesn_t_exist2)
                            existenceText.setTextColor(ContextCompat.getColor(this@SendMessageLicencePlate, R.color.red))

                            Log.w("PlateDebug","Nincs egyezés")
                        }
                    }
                } else {
                    Log.d("PlateDebug", "⛔ Nem indítunk lekérdezést, túl rövid: $plate")
                    existenceText.text = "❌ doesn't exist"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })




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



        sendButton1.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
           sendMessage(messageText1.text.toString())
                Toast.makeText(this,
                    getString(R.string.message_successfully_sent_to, toLicencePlate.text), Toast.LENGTH_SHORT).show()
             } else {
                Toast.makeText(this,
                    getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }
        sendButton2.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
                sendMessage(messageText2.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }
        sendButton3.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
                sendMessage(messageText3.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }
        sendButton4.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
                sendMessage(messageText4.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }
        sendButton5.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
                sendMessage(messageText5.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }
        sendButton6.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
                sendMessage(messageText6.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }
        sendButton7.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
                sendMessage(messageText7.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }
        sendButton8.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
                sendMessage(messageText8.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }
        sendButton9.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
                sendMessage(messageText9.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }
        sendButton10.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
                sendMessage(messageText10.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }
        sendButton11.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
                sendMessage(messageText11.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }
        sendButton12.setOnClickListener(){
            if (existenceText.text.toString().contains("✅")) {
                sendMessage(messageText12.text.toString())
            } else {
                Toast.makeText(this, getString(R.string.messages_can_only_be_sent_to_existing_license_plates), Toast.LENGTH_SHORT).show()
            }
        }


    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
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
                    navigateToSuccess(message)

                } else {
                    // Error occurred
                    println("Error saving message: ${e.message}")
                }
            }
        })
    }
    fun navigateToSuccess(message: String) {
        val intent = Intent(this, SendMessageSuccessful::class.java)
        intent.putExtra("receiverPlate", toLicencePlate.text.toString())
        intent.putExtra("message", message)

        // Bejelentkezett felhasználó adatai
        intent.putExtra("licencePlate", intent.getStringExtra("licencePlate"))
        intent.putExtra("sessionToken", intent.getStringExtra("sessionToken"))
        intent.putExtra("objID", intent.getStringExtra("objID"))

        startActivity(intent)
    }

}