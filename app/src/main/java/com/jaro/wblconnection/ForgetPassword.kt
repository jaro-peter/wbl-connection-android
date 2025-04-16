package com.jaro.wblconnection

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.parse.ParseException
import com.parse.ParseUser


class ForgetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailAddress = findViewById<EditText>(R.id.editEmailAddress)
        val button = findViewById<Button>(R.id.btnConfirm)

        button.setOnClickListener(){
            if (emailAddress.text.isEmpty()){
                Toast.makeText(this, "The email field is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ParseUser.requestPasswordResetInBackground(emailAddress.text.toString()) { e->
                if (e == null) {
                    Toast.makeText(this, "Password reset email sent successfully.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, Basic::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "This email does not exist.", Toast.LENGTH_SHORT).show()
                }
            }


        }
        val back = findViewById<Button>(R.id.button_back)
        back.setOnClickListener(){
            val intent = Intent(this,Basic::class.java)
        startActivity(intent)
        }

    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}