package com.example.roommateorganizer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roommateorganizer.model.UserDoc

class RoommateSearchAdapter(
    private val onClick: (UserDoc) -> Unit
) : ListAdapter<UserDoc, RoommateSearchAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<UserDoc>() {
            override fun areItemsTheSame(oldItem: UserDoc, newItem: UserDoc) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: UserDoc, newItem: UserDoc) = oldItem == newItem
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(android.R.id.text1)
        val subtitle: TextView = view.findViewById(android.R.id.text2)

        init {
            view.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onClick(getItem(pos))
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val u = getItem(position)
        holder.title.text = if (u.displayName.isNotBlank()) u.displayName else "(no name)"
        holder.subtitle.text = u.handle.ifBlank { "(no handle)" }
    }
}
