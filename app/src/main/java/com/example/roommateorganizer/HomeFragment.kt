package com.example.roommateorganizer

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roommateorganizer.model.ChoreDoc
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var list: RecyclerView
    private lateinit var empty: TextView
    private val adapter = HomeListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        list = v.findViewById(R.id.home_list)
        empty = v.findViewById(R.id.home_empty)
        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid = F.meUid ?: return
        F.db.collection("users").document(uid).get()
            .addOnSuccessListener { user ->
                val householdId = user.getString("householdId") ?: return@addOnSuccessListener
                F.db.collection("households").document(householdId)
                    .collection("chores")
                    .orderBy("dueAt", Query.Direction.ASCENDING)
                    .addSnapshotListener { qs, _ ->
                        val items = qs?.documents?.map { d ->
                            val c = d.toObject(ChoreDoc::class.java) ?: ChoreDoc()
                            c.copy(id = d.id)
                        } ?: emptyList()
                        adapter.submitList(items)
                        empty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                    }
            }
    }
}

private class HomeListAdapter :
    androidx.recyclerview.widget.ListAdapter<ChoreDoc, HomeListAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : androidx.recyclerview.widget.DiffUtil.ItemCallback<ChoreDoc>() {
            override fun areItemsTheSame(oldItem: ChoreDoc, newItem: ChoreDoc) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ChoreDoc, newItem: ChoreDoc) = oldItem == newItem
        }
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val t1: TextView = v.findViewById(android.R.id.text1)
        val t2: TextView = v.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = getItem(position)
        holder.t1.text = c.title
        holder.t2.text = "${c.location} • ${c.frequency} ${if (c.completed) "• Done" else ""}"
    }
}
