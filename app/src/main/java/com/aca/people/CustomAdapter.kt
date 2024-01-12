package com.aca.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CustomAdapter(val list:List<People>, val listener:viewActions) : RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parentView: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parentView.context)
            .inflate(R.layout.row, parentView, false)

        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(list[position], listener)
    }

    override fun getItemCount(): Int= list.size

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(people: People, listener: viewActions) {
            val imageView = itemView.findViewById<ImageView>(R.id.image)
            itemView.findViewById<TextView>(R.id.title).text= people.name
            itemView.findViewById<TextView>(R.id.email).text= people.email
            itemView.findViewById<ImageView>(R.id.image).setImageResource(android.R.drawable.btn_star)
            //Glide.with(itemView.context).load(people.image).into(imageView)
            itemView.setOnClickListener {listener.onItemClick(people)}
        }

    }

}
interface viewActions{
    fun onItemClick(item:People)
}
