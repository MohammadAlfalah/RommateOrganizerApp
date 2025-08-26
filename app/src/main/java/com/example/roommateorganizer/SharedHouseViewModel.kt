package com.example.roommateorganizer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

data class Expense(
    var title: String,
    var frequency: String,
    var amount: Double,
    var split: String,
    var isPaid: Boolean = false
)

data class Chore(
    var title: String,
    var frequency: String,
    var location: String,
    var isCompleted: Boolean = false
)

/**
 * Activity-scoped store for house data.
 * Survives fragment swaps and rotation.
 */
class SharedHouseViewModel(app: Application) : AndroidViewModel(app) {

    // Seed data (edit however you like)
    private val initialExpenses = mutableListOf(
        Expense("Rent", "Monthly", 1200.0, "All"),
        Expense("Electricity", "Monthly", 150.0, "All"),
        Expense("Internet", "Monthly", 80.0, "All")
    )
    private val initialChores = mutableListOf(
        Chore("Clean kitchen", "Daily", "Kitchen"),
        Chore("Take out trash", "Weekly", "Common areas"),
        Chore("Vacuum living room", "Weekly", "Living room")
    )

    // Using MutableLiveData of MUTABLE lists so we can edit in place,
    // but always re-post a new copy to notify observers.
    val expenses = MutableLiveData<MutableList<Expense>>(initialExpenses)
    val chores   = MutableLiveData<MutableList<Chore>>(initialChores)

    // ---- Expenses ops ----
    fun addExpense(e: Expense) {
        val list = expenses.value ?: mutableListOf()
        list.add(e)
        expenses.value = list.toMutableList()
    }

    fun updateExpense(index: Int, updater: (Expense) -> Unit) {
        val list = expenses.value ?: return
        if (index in list.indices) {
            updater(list[index])
            expenses.value = list.toMutableList()
        }
    }

    fun deleteExpense(index: Int) {
        val list = expenses.value ?: return
        if (index in list.indices) {
            list.removeAt(index)
            expenses.value = list.toMutableList()
        }
    }

    // ---- Chores ops ----
    fun addChore(c: Chore) {
        val list = chores.value ?: mutableListOf()
        list.add(c)
        chores.value = list.toMutableList()
    }

    fun updateChore(index: Int, updater: (Chore) -> Unit) {
        val list = chores.value ?: return
        if (index in list.indices) {
            updater(list[index])
            chores.value = list.toMutableList()
        }
    }

    fun deleteChore(index: Int) {
        val list = chores.value ?: return
        if (index in list.indices) {
            list.removeAt(index)
            chores.value = list.toMutableList()
        }
    }
}
