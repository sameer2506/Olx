package com.app.olx.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.olx.R
import com.app.olx.model.CategoriesModel
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView


class CategoriesAdapter(
    private var categoriesList: MutableList<CategoriesModel>,
    private var mClickListener: ItemClickListener
) :
    RecyclerView.Adapter<CategoriesAdapter.DashBoardViewHolder>() {

    lateinit var context: Context

    fun updateList(list: MutableList<CategoriesModel>) {
        categoriesList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashBoardViewHolder {
        context = parent.context
        val viewHolder = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_categories, parent, false
        )
        return DashBoardViewHolder(
            viewHolder
        )
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: DashBoardViewHolder, position: Int) {

        holder.textViewTitle.text = categoriesList[position].key
        Glide.with(context)
            .load(categoriesList[position].image)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.imageView)
        holder.itemView.setOnClickListener {
            mClickListener.onItemClick(position)
        }
        holder.imageView.setOnClickListener {
            mClickListener.onItemClick(position)
        }
    }

    class DashBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<CircleImageView>(R.id.ivIcon)!!
        val textViewTitle = itemView.findViewById<TextView>(R.id.tvTitle)!!
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }


}