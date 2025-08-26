package com.example.roommateorganizer

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class UpcomingAdapter(private var items: List<Task>) :
    RecyclerView.Adapter<UpcomingAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(android.R.id.text1)
        val sub  : TextView = v.findViewById(android.R.id.text2)
    }

    fun submit(newItems: List<Task>) { items = newItems; notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val t = items[pos]
        h.title.text = t.title
        val fmt = SimpleDateFormat("EEE, MMM d • HH:mm", Locale.getDefault())
        h.sub.text = "Due: ${fmt.format(Date(t.dueAt))}"
    }

    override fun getItemCount() = items.size
}