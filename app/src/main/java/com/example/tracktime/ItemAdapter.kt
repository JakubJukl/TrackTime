package com.example.tracktime

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_custom_row.view.*


class ItemAdapter(val context: Context, val items: MutableList<Record>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    val NO_LEAVE = "You are still working."
    val NO_DURATION = "Working..."

    /**
     * Inflates the item views which is designed in xml layout file
     *
     * create a new
     * {@link ViewHolder} and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_custom_row,
                parent,
                false
            )
        )
    }

    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        var duration: String
        if (item.leaveTime != null) {
            val s = item.duration()
            duration = String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60))
        } else {
            duration = NO_DURATION
        }
        holder.tvDay.text = item.startTime.dayOfMonth.toString()
        holder.tvDuration.text = duration

        // Updating the background color according to the odd/even positions in list.
        if (position % 2 == 0) {
            holder.cardViewItem.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorLightGray
                )
            )
        } else {
            holder.cardViewItem.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.marshmallow
                )
            )
        }

        holder.cardViewItem.setOnClickListener {
            val intent = Intent(context,Activity2::class.java)
            intent.putExtra(Activity2.INTENT_START,item.startTime.format(DbHandler.formatter))
            intent.putExtra(Activity2.INTENT_END, (item.leaveTime?.format(DbHandler.formatter) ?: NO_LEAVE))
            intent.putExtra(Activity2.INTENT_DURATION, duration)
            context.startActivity(intent)
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each item to
        val tvDay = view.day
        val tvDuration = view.duration
        val cardViewItem = view.card_view_item
    }
}
