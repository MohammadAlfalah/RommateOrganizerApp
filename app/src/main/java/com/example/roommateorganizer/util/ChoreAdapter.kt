// com/example/roommateorganizer/util/ChoreAdapter.kt
package com.example.roommateorganizer.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roommateorganizer.R
import com.example.roommateorganizer.model.ChoreDoc

class ChoreAdapter(
    val onEdit: (ChoreDoc) -> Unit,
    val onDelete: (ChoreDoc) -> Unit,
    val onCompletedToggle: (ChoreDoc, Boolean) -> Unit
) : ListAdapter<ChoreDoc, ChoreAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ChoreDoc>() {
            override fun areItemsTheSame(a: ChoreDoc, b: ChoreDoc) = a.id == b.id
            override fun areContentsTheSame(a: ChoreDoc, b: ChoreDoc) = a == b
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val done: CheckBox = view.findViewById(R.id.done)
        private val edit: ImageButton = view.findViewById(R.id.edit)
        private val del: ImageButton = view.findViewById(R.id.delete)

        fun bind(item: ChoreDoc) {
            title.text = item.title
            done.isChecked = item.done == true

            done.setOnCheckedChangeListener { _, isChecked ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) onCompletedToggle(item, isChecked)
            }
            edit.setOnClickListener { onEdit(item) }
            del.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_chore, parent, false) // make sure you have this row layout
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}
