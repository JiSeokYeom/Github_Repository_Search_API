package com.example.afreecatv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RvAdapter : RecyclerView.Adapter<RvAdapter.ViewHolder>() {
    var mData = mutableListOf<Item>()

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
         val item_avatar = itemView.findViewById<ImageView>(R.id.item_avatar)
         val item_full_name = itemView.findViewById<TextView>(R.id.item_full_name)
         val item_language = itemView.findViewById<TextView>(R.id.item_language)

        fun setData(data : Item){
                Glide.with(itemView.context)
                    .load(data.owner.avatar_url)
                    .into(item_avatar)
                item_full_name.text = data.full_name
                item_language.text = data.language
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_rv_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val item = mData[position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}