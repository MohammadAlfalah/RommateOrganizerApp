package com.example.roommateorganizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.card.MaterialCardView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.CheckBox
import android.graphics.Typeface
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import android.widget.Toast

class GroceriesFragment : Fragment() {
    
    private val groceries = mutableListOf(
        GroceryItem("Milk", "Dairy", 2, false),
        GroceryItem("Bread", "Bakery", 1, false),
        GroceryItem("Eggs", "Dairy", 12, false),
        GroceryItem("Bananas", "Fruits", 6, false),
        GroceryItem("Chicken breast", "Meat", 2, false)
    )
    
    private lateinit var groceriesRecyclerView: RecyclerView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        
        // Create main container
        val mainContainer = ConstraintLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16)
        }

        // Title
        val titleText = TextView(context).apply {
            id = View.generateViewId()
            text = "Grocery List"
            textSize = 24f
            setTextColor(context.getColor(android.R.color.black))
            setTypeface(null, Typeface.BOLD)
        }

        // Groceries list
        groceriesRecyclerView = RecyclerView(context).apply {
            id = View.generateViewId()
            layoutManager = LinearLayoutManager(context)
            adapter = GroceriesAdapter(groceries) { grocery, action ->
                when (action) {
                    "edit" -> showEditGroceryDialog(grocery)
                    "delete" -> showDeleteGroceryDialog(grocery)
                }
            }
        }

        // Add grocery button
        val addGroceryButton = FloatingActionButton(context).apply {
            id = View.generateViewId()
            setImageResource(android.R.drawable.ic_input_add)
            setOnClickListener {
                showAddGroceryDialog()
            }
        }

        // Add views to container
        mainContainer.addView(titleText)
        mainContainer.addView(groceriesRecyclerView)
        mainContainer.addView(addGroceryButton)

        // Set constraints
        val constraintSet = ConstraintSet()
        constraintSet.clone(mainContainer)
        
        constraintSet.connect(titleText.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        constraintSet.connect(titleText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        constraintSet.connect(titleText.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        
        constraintSet.connect(groceriesRecyclerView.id, ConstraintSet.TOP, titleText.id, ConstraintSet.BOTTOM, 16)
        constraintSet.connect(groceriesRecyclerView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        constraintSet.connect(groceriesRecyclerView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        constraintSet.connect(groceriesRecyclerView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 80)
        
        constraintSet.connect(addGroceryButton.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 16)
        constraintSet.connect(addGroceryButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
        
        constraintSet.applyTo(mainContainer)
        
        return mainContainer
    }
    
    private fun showAddGroceryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_grocery, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.grocery_name_input)
        val categoryInput = dialogView.findViewById<EditText>(R.id.grocery_category_input)
        val quantityInput = dialogView.findViewById<EditText>(R.id.grocery_quantity_input)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Grocery Item")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString().trim()
                val category = categoryInput.text.toString().trim()
                val quantityStr = quantityInput.text.toString().trim()
                
                if (name.isNotEmpty() && category.isNotEmpty() && quantityStr.isNotEmpty()) {
                    try {
                        val quantity = quantityStr.toInt()
                        val newGrocery = GroceryItem(name, category, quantity, false)
                        groceries.add(newGrocery)
                        groceriesRecyclerView.adapter?.notifyItemInserted(groceries.size - 1)
                        Toast.makeText(context, "Grocery item added successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showEditGroceryDialog(grocery: GroceryItem) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_grocery, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.grocery_name_input)
        val categoryInput = dialogView.findViewById<EditText>(R.id.grocery_category_input)
        val quantityInput = dialogView.findViewById<EditText>(R.id.grocery_quantity_input)
        
        // Pre-fill with existing data
        nameInput.setText(grocery.name)
        categoryInput.setText(grocery.category)
        quantityInput.setText(grocery.quantity.toString())
        
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Grocery Item")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString().trim()
                val category = categoryInput.text.toString().trim()
                val quantityStr = quantityInput.text.toString().trim()
                
                if (name.isNotEmpty() && category.isNotEmpty() && quantityStr.isNotEmpty()) {
                    try {
                        val quantity = quantityStr.toInt()
                        grocery.name = name
                        grocery.category = category
                        grocery.quantity = quantity
                        groceriesRecyclerView.adapter?.notifyDataSetChanged()
                        Toast.makeText(context, "Grocery item updated successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showDeleteGroceryDialog(grocery: GroceryItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Grocery Item")
            .setMessage("Are you sure you want to delete '${grocery.name}'?")
            .setPositiveButton("Delete") { _, _ ->
                val position = groceries.indexOf(grocery)
                if (position != -1) {
                    groceries.removeAt(position)
                    groceriesRecyclerView.adapter?.notifyItemRemoved(position)
                    Toast.makeText(context, "Grocery item deleted successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    data class GroceryItem(
        var name: String,
        var category: String,
        var quantity: Int,
        var isPurchased: Boolean
    )
    
    inner class GroceriesAdapter(
        private val groceriesList: List<GroceryItem>,
        private val onActionClick: (GroceryItem, String) -> Unit
    ) : RecyclerView.Adapter<GroceriesAdapter.GroceryViewHolder>() {
        
        inner class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameText: TextView = itemView.findViewById(R.id.grocery_name)
            val categoryText: TextView = itemView.findViewById(R.id.grocery_category)
            val quantityText: TextView = itemView.findViewById(R.id.grocery_quantity)
            val purchasedCheck: CheckBox = itemView.findViewById(R.id.grocery_purchased)
            val editButton: Button = itemView.findViewById(R.id.grocery_edit_button)
            val deleteButton: Button = itemView.findViewById(R.id.grocery_delete_button)
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_grocery_enhanced, parent, false)
            return GroceryViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
            val grocery = groceriesList[position]
            holder.nameText.text = grocery.name
            holder.categoryText.text = grocery.category
            holder.quantityText.text = "Qty: ${grocery.quantity}"
            holder.purchasedCheck.isChecked = grocery.isPurchased
            
            holder.purchasedCheck.setOnCheckedChangeListener { _, isChecked ->
                grocery.isPurchased = isChecked
            }
            
            holder.editButton.setOnClickListener {
                onActionClick(grocery, "edit")
            }
            
            holder.deleteButton.setOnClickListener {
                onActionClick(grocery, "delete")
            }
        }
        
        override fun getItemCount() = groceriesList.size
    }
}
