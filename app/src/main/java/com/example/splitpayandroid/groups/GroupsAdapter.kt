package com.example.splitpayandroid.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.splitpayandroid.R
import com.example.splitpayandroid.model.GroupDto

class GroupsAdapter(private val myDataset: ArrayList<GroupDto>) :
    RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    class GroupViewHolder(val view: View) : RecyclerView.ViewHolder(view){

        val textView: TextView = view.findViewById(R.id.groupItemName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.group_item, parent, false)
        return GroupViewHolder(textView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.textView.text = myDataset[position].displayname
    }

    override fun getItemCount() = myDataset.size
}
