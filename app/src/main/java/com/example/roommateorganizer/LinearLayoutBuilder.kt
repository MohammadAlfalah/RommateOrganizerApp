package com.example.roommateorganizer

import android.content.Context
import android.view.View
import android.widget.LinearLayout

object LinearLayoutBuilder {
    fun vertical(ctx: Context, paddingDp: Int, vararg children: View): LinearLayout {
        val ll = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            val pad = (paddingDp * resources.displayMetrics.density).toInt()
            setPadding(pad, pad, pad, pad)
        }
        children.forEach { v ->
            ll.addView(v, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ))
        }
        return ll
    }
}
