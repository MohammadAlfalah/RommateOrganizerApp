package com.example.roommateorganizer.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roommateorganizer.R
import com.example.roommateorganizer.model.ChatMsg

class ChatAdapter : ListAdapter<ChatMsg, ChatAdapter.VH>(DIFF) {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(android.R.id.text1)
        private val subtitle: TextView = view.findViewById(android.R.id.text2)

        fun bind(item: ChatMsg) {
            title.text = item.name
            subtitle.text = item.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false) // your 2-line layout
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<ChatMsg>() {
            override fun areItemsTheSame(a: ChatMsg, b: ChatMsg) = a.id == b.id
            override fun areContentsTheSame(a: ChatMsg, b: ChatMsg) = a == b
        }
    }
}
