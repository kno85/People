package com.aca.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aca.people.domain.User
import com.bumptech.glide.Glide


class UserAdapter(val listener:viewActions) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val users = mutableListOf<User>()

    fun addUsers(newUsers: List<User>) {
        users.addAll(newUsers)
    }

    override fun onCreateViewHolder(parentView: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parentView.context)
            .inflate(R.layout.row, parentView, false)

        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position], listener)
    }

    override fun getItemCount(): Int= users.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User, listener: viewActions) {
            val imageView = itemView.findViewById<ImageView>(R.id.image)
            itemView.findViewById<TextView>(R.id.title).text= user.name?.first
            itemView.findViewById<TextView>(R.id.email).text= user.email
            Glide.with(itemView.context).load(user.picture?.thumbnail).into(imageView)
            itemView.setOnClickListener {listener.onItemClick(user)}
        }

    }

}
interface viewActions{
    fun onItemClick(item: User)
}
