package com.yunda.safe.plct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yunda.safe.plct.R
import com.yunda.safe.plct.database.entity.WebUri


class WebUriAdapter :
    ListAdapter<String, WebUriAdapter.ViewHolder>(object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }) {

    private lateinit var clickItem: (String) -> Unit
    private lateinit var clickItemChild: (String) -> Unit
    private lateinit var historyList: MutableList<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.dropdown_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = historyList[position]
        holder.bind(uri)
    }

    override fun getItemCount(): Int {
        return historyList.size;
    }

    fun setDatas(historyList: MutableList<String>) {
        this.historyList = historyList;
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(clickItem: (String) -> Unit) {
        this.clickItem = clickItem
    }

    fun setOnItemChildClickListener(clickItemChild: (String) -> Unit) {
        this.clickItemChild = clickItemChild
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var mUri: String = ""
        val textView: TextView = view.findViewById(R.id.item_text)
        val removeButton: ImageView = view.findViewById(R.id.remove_button)

        init {
            view.setOnClickListener {
                clickItem(mUri)
            }

            removeButton.setOnClickListener {
                clickItemChild(mUri)
            }
        }

        fun bind(uri: String) {
            mUri = uri
            textView.text = mUri
        }

        override fun onClick(v: View?) {

        }
    }
}

class WebUriAdapter2(
    context: Context, private val resourceId: Int,
    objects: List<WebUri>
) : ArrayAdapter<WebUri>(context, resourceId, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val webUri: WebUri? = getItem(position) // 获取当前项的Fruit实例
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resourceId, parent, false)
            viewHolder = ViewHolder()
//            viewHolder.fruitImage = view.findViewById<View>(R.id.fruit_image) as ImageView
//            viewHolder.fruitName = view.findViewById<View>(R.id.fruit_name) as TextView
            view.tag = viewHolder // 将ViewHolder存储在View中
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder // 重新获取ViewHolder
        }
//        viewHolder.fruitImage!!.setImageResource(fruit.getImageId())
//        viewHolder.fruitName.setText(fruit.getName())
        return view
    }

    internal inner class ViewHolder {
        var fruitImage: ImageView? = null

        var fruitName: TextView? = null
    }

}
