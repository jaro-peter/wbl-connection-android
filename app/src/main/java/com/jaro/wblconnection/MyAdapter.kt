package com.jaro.wblconnection

import android.app.AlertDialog
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

        val liked = parseMessage.getBoolean("rating")
        holder.likeButton.setBackgroundResource(
            if (liked) R.drawable.like else R.drawable.like_off
        )

        holder.likeButton.setOnClickListener {
            val newLiked = !parseMessage.getBoolean("rating")
            parseMessage.put("rating", newLiked)
            parseMessage.saveInBackground()

            holder.likeButton.setBackgroundResource(
                if (newLiked) R.drawable.like else R.drawable.like_off
            )

            // Küldés szerveroldali Cloud Functionnek
            val delta = if (newLiked) 1 else -1

            val params = hashMapOf(
                "userId" to item.parseObject.getString("From"),
                "delta" to delta
            )

            com.parse.ParseCloud.callFunctionInBackground<String>("updateUserRating", params) { response, e ->
                if (e == null) {
                    Log.d("rating", "✅ Sikeres mentés szerveren: $response")
                } else {
                    Log.e("rating", "❌ Hiba a szerveren: ${e.localizedMessage}")
                }
            }
        }
        holder.binButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this message? Once deleted, likes and dislikes can no longer be changed ")
                .setPositiveButton("Yes") { _, _ ->
                    item.parseObject.deleteInBackground { e ->
                        if (e == null) {
                            items.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, items.size)
                        } else {
                            Log.e("Delete", "❌ Failed to delete: ${e.localizedMessage}")
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
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