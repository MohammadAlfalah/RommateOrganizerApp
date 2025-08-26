package com.example.roommateorganizer.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roommateorganizer.R
import com.example.roommateorganizer.model.ExpenseDoc

class ExpenseAdapter(
    private val onEdit: (ExpenseDoc) -> Unit,
    private val onDelete: (ExpenseDoc) -> Unit,
    private val onPaidToggle: (ExpenseDoc, Boolean) -> Unit
) : ListAdapter<ExpenseDoc, ExpenseAdapter.VH>(DIFF) {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.expense_title)
        private val frequency: TextView = view.findViewById(R.id.expense_frequency)
        private val amount: TextView = view.findViewById(R.id.expense_amount)
        private val split: TextView = view.findViewById(R.id.expense_split)
        private val paid: CheckBox = view.findViewById(R.id.expense_paid)
        private val edit: Button = view.findViewById(R.id.expense_edit_button)
        private val del: Button = view.findViewById(R.id.expense_delete_button)

        fun bind(item: ExpenseDoc) {
            title.text = item.title ?: ""
            frequency.text = item.frequency ?: ""
            amount.text = "$${item.amount ?: 0.0}"
            split.text = if (item.splitEvenly == true) "Split evenly" else
                "Participants: ${(item.participants ?: emptyList()).size}"
            paid.isChecked = item.paid == true

            // Guard adapter position to avoid crashes after list updates.
            paid.setOnCheckedChangeListener { _, isChecked ->
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) onPaidToggle(item, isChecked)
            }
            edit.setOnClickListener { onEdit(item) }
            del.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense_enhanced, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ExpenseDoc>() {
            override fun areItemsTheSame(oldItem: ExpenseDoc, newItem: ExpenseDoc) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ExpenseDoc, newItem: ExpenseDoc) =
                oldItem == newItem
        }
    }
}
