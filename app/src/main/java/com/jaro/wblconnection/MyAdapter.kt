package com.jaro.wblconnection

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.parse.ParseObject
import com.parse.ParseUser
import kotlin.math.log

// Adapter for RecyclerView
class MyAdapter(private val items: MutableList<MessageView>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // ViewHolder class to bind data to views
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val likeButton : ImageButton = itemView.findViewById(R.id.ImageButtonLikeButton)
        val binButton  : ImageButton = itemView.findViewById(R.id.ImageButtonBinButton)
        val licensePlate: TextView = itemView.findViewById(R.id.licensePlateTextView)
        val createdAt: TextView = itemView.findViewById(R.id.createdAtTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        val parseMessage = item.parseObject

        holder.licensePlate.text = item.message
        holder.createdAt.text = item.date

        val liked = parseMessage.getBoolean("Rating")
        holder.likeButton.setBackgroundResource(
            if (liked) R.drawable.like else R.drawable.like_off
        )

        holder.likeButton.setOnClickListener {
            val newLiked = !parseMessage.getBoolean("Rating")
            parseMessage.put("Rating", newLiked)
            parseMessage.saveInBackground()

            holder.likeButton.setBackgroundResource(
                if (newLiked) R.drawable.like else R.drawable.like_off
            )

            // User értékelés frissítése
            val query = ParseUser.getQuery()
            query.whereEqualTo("username", item.username)
            Log.d("username", item.username)
            query.getFirstInBackground { user, e ->
                if (user != null) {
                    Log.e("user", user.toString() )
                }
                if (e != null) {
                    Log.e("error",e.toString())

                }
                if (e == null && user != null) {
                    val currentRating = user.getInt("rating")
                    val updatedRating = if (!newLiked) currentRating + 1 else currentRating - 1
                    user.put("rating", updatedRating)
                    user.saveInBackground{e ->
                        if (e == null) {
                            Log.d("rating", "Sikeres mentés: ${user.getInt("rating")}")
                        } else {
                            Log.e("rating", "Mentés sikertelen: ${e.localizedMessage}")
                        }
                        Log.e("rating",user.getString("username").toString())
                        Log.e("rating", user.getInt("rating").toString())
                    }

                }
            }
        }
    }


    override fun getItemCount(): Int {
        return items.size
    }

    // Add new row (or item) to the list
    fun addItem(item: ParseObject) {
        val message = MessageView(
            item.createdAt?.toString() ?: "N/A",
            item.getString("Message") ?: "",
            item.getString("From") ?: "",
            item
        )

        items.add(message)
        notifyItemInserted(items.size - 1)
    }





}