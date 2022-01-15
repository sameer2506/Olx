package com.app.olx.ui.sell.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.olx.R
import com.app.olx.model.CategoriesModel
import com.bumptech.glide.Glide


/**
 * Created by Akshay.
 * seller product list adapter
 * @property mProductClickListener click Listener
 */
class SellAdapter(
    var categoriesList: MutableList<CategoriesModel>,
    var mClickListener: ItemClickListener
) :
    RecyclerView.Adapter<SellAdapter.DashBoardViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashBoardViewHolder {
        context = parent.context
        val viewHolder = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_sell
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


        holder.textViewTitle.text  = categoriesList.get(position).key
        Glide.with(context)
            .load(categoriesList.get(position).image_bw)
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
        val imageView = itemView.findViewById<ImageView>(R.id.ivIcon)!!
        val textViewTitle = itemView.findViewById<TextView>(R.id.tvTitle)!!
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }


}