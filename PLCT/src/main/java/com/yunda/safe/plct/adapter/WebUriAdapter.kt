package com.yunda.safe.plct.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yunda.safe.plct.R

class WebUriAdapter(
    private val historyList: MutableList<String>,
    private val clickItem: (String) -> Unit,
    private val removeItem: (String) -> Unit
//) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
) : ListAdapter<String, WebUriAdapter.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var mUri: String = ""
        val textView: TextView = view.findViewById(R.id.item_text)
        val removeButton: ImageView = view.findViewById(R.id.remove_button)

        init {
            view.setOnClickListener {
//                clickItem(historyList[adapterPosition])
                clickItem(mUri)
            }
            removeButton.setOnClickListener {
//                removeItem(historyList[adapterPosition])
                removeItem(mUri)
            }
        }

        fun bind(uri: String) {
            mUri = uri
            textView.text = mUri
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.dropdown_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val uri = getItem(position)
        val uri = historyList[position]
        holder.bind(uri)
    }

    override fun getItemCount(): Int = historyList.size

}