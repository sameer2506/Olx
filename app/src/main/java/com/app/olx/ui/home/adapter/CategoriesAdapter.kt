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




/**
 * Created by Akshay.
 * seller product list adapter
 * @property mProductClickListener click Listener
 */
class CategoriesAdapter(
    var categoriesList: MutableList<CategoriesModel>,
    var mClickListener: ItemClickListener
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
            R.layout.adapter_categories
            , parent, false
        )
        return DashBoardViewHolder(
            viewHolder
        )
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }

    override fun onBindViewHolder(holder: DashBoardViewHolder, position: Int) {


        holder.textViewTitle.text = categoriesList.get(position).key
        Glide.with(context)
            .load(categoriesList.get(position).image)
            .placeholder(com.app.olx.R.drawable.ic_placeholder)
            .into(holder.imageView)
        holder.itemView.setOnClickListener {
            mClickListener.onItemClick(position)
        }
        holder.imageView.setOnClickListener {
            mClickListener.onItemClick(position)
        }
    }

    class DashBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<CircleImageView>(com.app.olx.R.id.ivIcon)!!
        val textViewTitle = itemView.findViewById<TextView>(com.app.olx.R.id.tvTitle)!!
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }


}