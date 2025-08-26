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
import android.graphics.Typeface
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import android.widget.Toast

class RulesFragment : Fragment() {
    
    private val rules = mutableListOf(
        HouseRule("Quiet hours", "10 PM - 8 AM", "Respect quiet hours for everyone's sleep", "Common courtesy"),
        HouseRule("Kitchen cleanup", "After each use", "Clean up after cooking and eating", "Hygiene"),
        HouseRule("Laundry schedule", "Assigned days", "Use assigned laundry days to avoid conflicts", "Organization"),
        HouseRule("Guest policy", "24-hour notice", "Inform roommates before inviting guests", "Communication")
    )
    
    private lateinit var rulesRecyclerView: RecyclerView
    
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
            text = "House Rules"
            textSize = 24f
            setTextColor(context.getColor(android.R.color.black))
            setTypeface(null, Typeface.BOLD)
        }

        // Rules list
        rulesRecyclerView = RecyclerView(context).apply {
            id = View.generateViewId()
            layoutManager = LinearLayoutManager(context)
            adapter = RulesAdapter(rules) { rule, action ->
                when (action) {
                    "edit" -> showEditRuleDialog(rule)
                    "delete" -> showDeleteRuleDialog(rule)
                }
            }
        }

        // Add rule button
        val addRuleButton = FloatingActionButton(context).apply {
            id = View.generateViewId()
            setImageResource(android.R.drawable.ic_input_add)
            setOnClickListener {
                showAddRuleDialog()
            }
        }

        // Add views to container
        mainContainer.addView(titleText)
        mainContainer.addView(rulesRecyclerView)
        mainContainer.addView(addRuleButton)

        // Set constraints
        val constraintSet = ConstraintSet()
        constraintSet.clone(mainContainer)
        
        constraintSet.connect(titleText.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0)
        constraintSet.connect(titleText.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        constraintSet.connect(titleText.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        
        constraintSet.connect(rulesRecyclerView.id, ConstraintSet.TOP, titleText.id, ConstraintSet.BOTTOM, 16)
        constraintSet.connect(rulesRecyclerView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0)
        constraintSet.connect(rulesRecyclerView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0)
        constraintSet.connect(rulesRecyclerView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 80)
        
        constraintSet.connect(addRuleButton.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 16)
        constraintSet.connect(addRuleButton.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 16)
        
        constraintSet.applyTo(mainContainer)
        
        return mainContainer
    }
    
    private fun showAddRuleDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_rule, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.rule_title_input)
        val frequencyInput = dialogView.findViewById<EditText>(R.id.rule_frequency_input)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.rule_description_input)
        val categoryInput = dialogView.findViewById<EditText>(R.id.rule_category_input)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Rule")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString().trim()
                val frequency = frequencyInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()
                val category = categoryInput.text.toString().trim()
                
                if (title.isNotEmpty() && frequency.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty()) {
                    val newRule = HouseRule(title, frequency, description, category)
                    rules.add(newRule)
                    rulesRecyclerView.adapter?.notifyItemInserted(rules.size - 1)
                    Toast.makeText(context, "Rule added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showEditRuleDialog(rule: HouseRule) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_rule, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.rule_title_input)
        val frequencyInput = dialogView.findViewById<EditText>(R.id.rule_frequency_input)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.rule_description_input)
        val categoryInput = dialogView.findViewById<EditText>(R.id.rule_category_input)
        
        // Pre-fill with existing data
        titleInput.setText(rule.title)
        frequencyInput.setText(rule.frequency)
        descriptionInput.setText(rule.description)
        categoryInput.setText(rule.category)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Rule")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = titleInput.text.toString().trim()
                val frequency = frequencyInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()
                val category = categoryInput.text.toString().trim()
                
                if (title.isNotEmpty() && frequency.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty()) {
                    rule.title = title
                    rule.frequency = frequency
                    rule.description = description
                    rule.category = category
                    rulesRecyclerView.adapter?.notifyDataSetChanged()
                    Toast.makeText(context, "Rule updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showDeleteRuleDialog(rule: HouseRule) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Rule")
            .setMessage("Are you sure you want to delete '${rule.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                val position = rules.indexOf(rule)
                if (position != -1) {
                    rules.removeAt(position)
                    rulesRecyclerView.adapter?.notifyItemRemoved(position)
                    Toast.makeText(context, "Rule deleted successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    data class HouseRule(
        var title: String,
        var frequency: String,
        var description: String,
        var category: String
    )
    
    inner class RulesAdapter(
        private val rulesList: List<HouseRule>,
        private val onActionClick: (HouseRule, String) -> Unit
    ) : RecyclerView.Adapter<RulesAdapter.RuleViewHolder>() {
        
        inner class RuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleText: TextView = itemView.findViewById(R.id.rule_title)
            val frequencyText: TextView = itemView.findViewById(R.id.rule_frequency)
            val descriptionText: TextView = itemView.findViewById(R.id.rule_description)
            val categoryText: TextView = itemView.findViewById(R.id.rule_category)
            val editButton: Button = itemView.findViewById(R.id.rule_edit_button)
            val deleteButton: Button = itemView.findViewById(R.id.rule_delete_button)
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RuleViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_rule_enhanced, parent, false)
            return RuleViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: RuleViewHolder, position: Int) {
            val rule = rulesList[position]
            holder.titleText.text = rule.title
            holder.frequencyText.text = rule.frequency
            holder.descriptionText.text = rule.description
            holder.categoryText.text = rule.category
            
            holder.editButton.setOnClickListener {
                onActionClick(rule, "edit")
            }
            
            holder.deleteButton.setOnClickListener {
                onActionClick(rule, "delete")
            }
        }
        
        override fun getItemCount() = rulesList.size
    }
}
