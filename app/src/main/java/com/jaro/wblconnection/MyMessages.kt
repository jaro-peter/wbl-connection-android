package com.jaro.wblconnection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.parse.ParseObject
import com.parse.ParseQuery

class MyMessages : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_messeges)
        val licencePlate = findViewById<EditText>(R.id.editTextMyLicensePlate)
        val messageList = findViewById<RecyclerView>(R.id.RecyclerMessageList)
        val back = findViewById<Button>(R.id.button_back)

        licencePlate.setText(intent.getStringExtra("licencePlate"))
        messageList.layoutManager = LinearLayoutManager(this)
        val adapter = MyAdapter(mutableListOf())
        messageList.adapter = adapter



        val query = ParseQuery.getQuery<ParseObject>("Messages")

// Filter by licensePlate
        query.whereEqualTo("To", intent.getStringExtra("licencePlate"))

// Order by date (descending: newest first)
        query.orderByDescending("createdAt") // or "yourDateColumn"

// Optionally, limit results (remove if you want all)
        query.setLimit(100)  // increase as needed

        query.findInBackground { objects, e ->
            if (e == null && objects != null) {
                for (obj in objects) {
                    adapter.addItem(obj)
                    Log.d("Parse", "Talált üzenetek száma: ${objects?.size ?: 0}")

                }
            } else {
                Log.e("Parse", "Error fetching data: ${e?.localizedMessage}")
            }
        }
        back.setOnClickListener(){
            val intent = Intent(this, LoggedIn::class.java)
            intent.putExtra("sessionToken", this.intent.getStringExtra("sessionToken"))
            intent.putExtra("licencePlate", this.intent.getStringExtra("licencePlate") )
            intent.putExtra("objID",this.intent.getStringExtra("objID"))
            Log.d("sessionToken",this.intent.getStringExtra("sessionToken")+"")
            Log.d("licencePlate", this.intent.getStringExtra("licencePlate")+"")
            Log.d("userID25", this.intent.getStringExtra("objID")+"")

            startActivity(intent)
        }





    }
}