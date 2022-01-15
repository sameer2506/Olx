package com.app.olx.ui.detail

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.app.olx.R
import com.bumptech.glide.Glide


class DetailImagesAdapter(
    var context: Context,
    private val membersList: ArrayList<String>,
    var mClickListener: ItemClickListener


) :
    PagerAdapter() {

    private val inflater: LayoutInflater
    private var doNotifyDataSetChangedOnce = false


    init {
        inflater = LayoutInflater.from(context)
    }


    override fun getCount(): Int {
        if (doNotifyDataSetChangedOnce) {
            doNotifyDataSetChangedOnce = false
            notifyDataSetChanged()
        }
        return membersList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = inflater.inflate(R.layout.adapter_detail_images, container, false)!!
        val imageView = view!!.findViewById<ImageView>(R.id.image)

        imageView.setOnClickListener(View.OnClickListener {
            mClickListener.onItemClick(position)
        })
        Glide.with(context)
            .load(membersList.get(position))
            .placeholder(R.drawable.big_placeholder)
            .into(imageView)
        container.addView(view, 0)
        return view
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

}

