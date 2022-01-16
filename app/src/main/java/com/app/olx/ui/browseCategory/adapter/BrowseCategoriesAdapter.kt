package com.app.olx.ui.browseCategory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.olx.R
import com.app.olx.model.DataItemModel
import com.app.olx.utils.Constants
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat


class BrowseCategoriesAdapter(
    private var dataItemModel: MutableList<DataItemModel>,
    private var mClickListener: ItemClickListener
) :
    RecyclerView.Adapter<BrowseCategoriesAdapter.DashBoardViewHolder>() {

    lateinit var context: Context

    fun updateList(temp: MutableList<DataItemModel>) {
        dataItemModel = temp
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashBoardViewHolder {
        context = parent.context
        val viewHolder = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_browse_category, parent, false
        )
        return DashBoardViewHolder(
            viewHolder
        )
    }

    override fun getItemCount(): Int {
        return dataItemModel.size
    }

    override fun onBindViewHolder(holder: DashBoardViewHolder, position: Int) {

        holder.textViewPrice.text = Constants.CURRENCY_SYMBOL + dataItemModel.get(position).price
        holder.tvAddress.text = dataItemModel[position].address
        holder.tvBrand.text = dataItemModel[position].brand
        Glide.with(context).load(dataItemModel[position].images[0])
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.imageView)

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = sdf.format(dataItemModel[position].createdDate?.time!!)
        holder.tvDate.text = formattedDate

        holder.itemView.setOnClickListener {
            mClickListener.onItemClick(position)
        }
    }

    class DashBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)!!
        val textViewPrice = itemView.findViewById<TextView>(R.id.tvPrice)!!
        val tvAddress = itemView.findViewById<TextView>(R.id.tvAddress)!!
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)!!
        val tvBrand = itemView.findViewById<TextView>(R.id.tvBrand)!!
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

}