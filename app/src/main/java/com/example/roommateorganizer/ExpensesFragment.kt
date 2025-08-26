package com.example.roommateorganizer

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roommateorganizer.model.ExpenseDoc
import com.example.roommateorganizer.util.ExpenseAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ListenerRegistration

class ExpensesFragment : Fragment() {

    private lateinit var list: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: ExpenseAdapter

    private var sub: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_expenses, container, false)
        list = root.findViewById(R.id.expenses_list)
        fab = root.findViewById(R.id.fab_add_expense)

        adapter = ExpenseAdapter(
            onEdit = { item -> showEditDialog(item) },
            onDelete = { item -> deleteExpense(item) },
            onPaidToggle = { item, paid -> togglePaid(item, paid) }
        )
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(requireContext())

        fab.setOnClickListener { showAddDialog() }

        return root
    }

    override fun onStart() {
        super.onStart()
        val hh = F.householdId ?: return
        sub = F.db.collection("households").document(hh)
            .collection("expenses")
            .orderBy("createdAt")
            .addSnapshotListener { qs, err ->
                if (err != null || qs == null) return@addSnapshotListener
                val items = qs.documents.mapNotNull { d ->
                    d.toObject(ExpenseDoc::class.java)?.copy(id = d.id)
                }
                adapter.submitList(items)
            }
    }

    override fun onStop() {
        sub?.remove()
        sub = null
        super.onStop()
    }

    private fun showAddDialog() {
        val ctx = requireContext()
        val title = EditText(ctx).apply { hint = "Title" }
        val freq = EditText(ctx).apply { hint = "Frequency (e.g. Monthly)" }
        val amount = EditText(ctx).apply {
            hint = "Amount"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val container = LinearLayoutBuilder.vertical(ctx, 16, title, freq, amount)

        AlertDialog.Builder(ctx)
            .setTitle("Add Expense")
            .setView(container)
            .setPositiveButton("Add") { _, _ ->
                val t = title.text.toString().trim()
                val f = freq.text.toString().trim()
                val a = amount.text.toString().trim().toDoubleOrNull()
                if (t.isEmpty() || f.isEmpty() || a == null) {
                    Toast.makeText(ctx, "Fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val me = F.meUid ?: return@setPositiveButton
                val hh = F.householdId ?: return@setPositiveButton

                val doc = ExpenseDoc(
                    title = t,
                    frequency = f,
                    amount = a,
                    splitEvenly = true,
                    participants = listOf(me),
                    paid = false,
                    createdAt = F.now()
                )
                F.db.collection("households").document(hh)
                    .collection("expenses").add(doc)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(expense: ExpenseDoc) {
        val ctx = requireContext()
        val title = EditText(ctx).apply { setText(expense.title) }
        val freq = EditText(ctx).apply { setText(expense.frequency) }
        val amount = EditText(ctx).apply {
            setText(expense.amount.toString())
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val container = LinearLayoutBuilder.vertical(ctx, 16, title, freq, amount)

        AlertDialog.Builder(ctx)
            .setTitle("Edit Expense")
            .setView(container)
            .setPositiveButton("Save") { _, _ ->
                val hh = F.householdId ?: return@setPositiveButton
                val a = amount.text.toString().trim().toDoubleOrNull() ?: return@setPositiveButton
                val map = mapOf(
                    "title" to title.text.toString().trim(),
                    "frequency" to freq.text.toString().trim(),
                    "amount" to a
                )
                F.db.collection("households").document(hh)
                    .collection("expenses").document(expense.id!!)
                    .update(map)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun togglePaid(expense: ExpenseDoc, paid: Boolean) {
        val hh = F.householdId ?: return
        F.db.collection("households").document(hh)
            .collection("expenses").document(expense.id!!)
            .update("paid", paid)
    }

    private fun deleteExpense(expense: ExpenseDoc) {
        val hh = F.householdId ?: return
        F.db.collection("households").document(hh)
            .collection("expenses").document(expense.id!!).delete()
    }
}
