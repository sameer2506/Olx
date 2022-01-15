package com.app.olx.ui.uploadPhoto.adapter

import android.app.Activity
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.olx.R
import java.util.*

/**
 * Created by Thakur on 4/30/2018.
 */

class UploadImagesAdapter(
    internal var activity: Activity,
    internal var imagesArraylist: ArrayList<String>,
    internal var addImage: AddImage
) : RecyclerView.Adapter<UploadImagesAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return imagesArraylist.size + 1
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_upload_images, null, false)
        return ViewHolder(view)
    }

   override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < imagesArraylist.size) {
            val myBitmap = BitmapFactory.decodeFile(imagesArraylist[position])
            holder.imageView.setImageBitmap(myBitmap)
        }
        holder.itemView.setOnClickListener(View.OnClickListener {
            if (position == imagesArraylist.size) {
                addImage.addPhotoClick()
            }
        })


    }

    fun customNotify(imagesArraylist: ArrayList<String>) {
        this.imagesArraylist = imagesArraylist
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var imageView: ImageView


        init {
            imageView = itemView.findViewById(R.id.imageView)


        }
    }

    interface AddImage {

        fun addPhotoClick()


    }
}
