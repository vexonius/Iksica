package com.tstudioz.iksica.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tstudioz.iksica.Data.Models.TransactionItem
import com.tstudioz.iksica.R

/**
 * Created by etino7 on 11-Oct-17.
 */
class TransactionDetailsAdapter(private var items: List<TransactionItem>?) : RecyclerView.Adapter<TransactionDetailsAdapter.DetailViewHolder>() {

    inner class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val quantity: TextView
        val name: TextView
        val price: TextView
        val subvention: TextView

        init {
            quantity = itemView.findViewById<View>(R.id.item_quantity) as TextView
            name = itemView.findViewById<View>(R.id.item_name) as TextView
            price = itemView.findViewById<View>(R.id.item_price) as TextView
            subvention = view.findViewById<View>(R.id.subvention_amount) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.billing_item_layout, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = items!![position]
        holder.quantity.text = item.quantity.toString() + "X"
        holder.name.text = item.name
        holder.price.text = item.total
        holder.subvention.text = "-" + item.itemSubvention
    }

    override fun getItemCount(): Int = items?.size ?: 0

    fun updateItems(items: List<TransactionItem>?) {
        this.items = items
        notifyDataSetChanged()
    }

}