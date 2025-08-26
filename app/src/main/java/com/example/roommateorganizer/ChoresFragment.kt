package com.example.roommateorganizer

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roommateorganizer.model.ChoreDoc
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ListenerRegistration
import com.example.roommateorganizer.util.ChoreAdapter

class ChoresFragment : Fragment() {

    private lateinit var list: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: ChoreAdapter

    private var sub: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_chores, container, false)
        list = root.findViewById(R.id.chores_list)
        fab = root.findViewById(R.id.fab_add_chore)

        adapter = ChoreAdapter(
            onEdit = { item -> showEdit(item) },
            onDelete = { item -> remove(item) },
            onCompletedToggle = { item, done -> toggle(item, done) }
        )
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(requireContext())
        fab.setOnClickListener { showAdd() }
        return root
    }

    override fun onStart() {
        super.onStart()
        val hh = F.householdId ?: return
        sub = F.db.collection("households").document(hh)
            .collection("chores")
            .addSnapshotListener { qs, _ ->
                if (qs == null) return@addSnapshotListener
                val items = qs.documents.mapNotNull { d ->
                    d.toObject(ChoreDoc::class.java)?.copy(id = d.id)
                }
                adapter.submitList(items)
            }
    }

    override fun onStop() {
        sub?.remove()
        sub = null
        super.onStop()
    }

    private fun showAdd() {
        val ctx = requireContext()
        val title = EditText(ctx).apply { hint = "Title" }
        val freq = EditText(ctx).apply { hint = "Frequency" }
        val loc = EditText(ctx).apply { hint = "Location" }
        val container = LinearLayoutBuilder.vertical(ctx, 16, title, freq, loc)

        AlertDialog.Builder(ctx)
            .setTitle("Add Chore")
            .setView(container)
            .setPositiveButton("Add") { _, _ ->
                val t = title.text.toString().trim()
                val f = freq.text.toString().trim()
                val l = loc.text.toString().trim()
                if (t.isEmpty() || f.isEmpty() || l.isEmpty()) {
                    Toast.makeText(ctx, "Fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val hh = F.householdId ?: return@setPositiveButton
                val doc = ChoreDoc(title = t, frequency = f, location = l, dueAt = null, assigneeUid = null, completed = false)
                F.db.collection("households").document(hh)
                    .collection("chores").add(doc)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEdit(item: ChoreDoc) {
        val ctx = requireContext()
        val title = EditText(ctx).apply { setText(item.title) }
        val freq = EditText(ctx).apply { setText(item.frequency) }
        val loc = EditText(ctx).apply { setText(item.location) }
        val container = LinearLayoutBuilder.vertical(ctx, 16, title, freq, loc)

        AlertDialog.Builder(ctx)
            .setTitle("Edit Chore")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val hh = F.householdId ?: return@setPositiveButton
                F.db.collection("households").document(hh)
                    .collection("chores").document(item.id!!)
                    .update(
                        mapOf(
                            "title" to title.text.toString().trim(),
                            "frequency" to freq.text.toString().trim(),
                            "location" to loc.text.toString().trim()
                        )
                    )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun remove(item: ChoreDoc) {
        val hh = F.householdId ?: return
        F.db.collection("households").document(hh)
            .collection("chores").document(item.id!!).delete()
    }

    private fun toggle(item: ChoreDoc, done: Boolean) {
        val hh = F.householdId ?: return
        F.db.collection("households").document(hh)
            .collection("chores").document(item.id!!)
            .update("completed", done)
    }
}
