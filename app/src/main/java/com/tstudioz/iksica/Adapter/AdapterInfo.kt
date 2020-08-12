package com.tstudioz.iksica.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tstudioz.iksica.Adapter.AdapterInfo.InfoItemViewHolder
import com.tstudioz.iksica.Data.Models.UserInfoItem
import com.tstudioz.iksica.R

class AdapterInfo(private var items: List<UserInfoItem>?) : RecyclerView.Adapter<InfoItemViewHolder>() {

    inner class InfoItemViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val title: TextView = view.findViewById<View>(R.id.info_title_text) as TextView
        val desc: TextView = view.findViewById<View>(R.id.info_desc_text) as TextView


        init {
            view.setOnClickListener(this)
        }

        override fun onClick(view: View) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): InfoItemViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.user_item_info_row, parent, false)
        return InfoItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoItemViewHolder, position: Int) {
        val item = items!![position]
        holder.title.text = item.label
        holder.desc.text = item.item
    }

    fun updateData(items: List<UserInfoItem>?) {
        this.items = items
    }

    override fun getItemCount(): Int = items?.size ?: 0

}