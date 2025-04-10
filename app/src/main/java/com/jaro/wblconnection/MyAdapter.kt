package com.jaro.wblconnection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.parse.ParseObject

// Adapter for RecyclerView
class MyAdapter(private val items: MutableList<MessageView>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // ViewHolder class to bind data to views
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val licensePlate: TextView = itemView.findViewById(R.id.licensePlateTextView)
        val createdAt: TextView = itemView.findViewById(R.id.createdAtTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
      holder.licensePlate.setText(item.message)
        holder.createdAt.setText(item.date)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // Add new row (or item) to the list
    fun addItem(item: ParseObject) {
        var message : MessageView = MessageView(item.createdAt?.toString() ?: "N/A",item.getString("Message")+"")

        items.add(message)
        notifyItemInserted(items.size - 1)
    }



}