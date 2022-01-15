package com.app.olx.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.app.olx.BaseFragment
import com.app.olx.R
import com.app.olx.model.DataItemModel
import com.app.olx.ui.previewImage.PreviewImageActivity
import com.app.olx.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_details.*
import java.text.SimpleDateFormat
import java.util.*


class DetailFragment : BaseFragment(), DetailImagesAdapter.ItemClickListener {

    private lateinit var dataItemModel: DataItemModel
    val db = FirebaseFirestore.getInstance()
    val TAG = DetailFragment::class.java.simpleName
    internal var r: Runnable? = null
    internal var h: Handler? = null
    internal var count: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_details, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getItemDetail()
        clickListener()
        if (arguments?.getString(Constants.KEY)!!.equals(Constants.CAR)){
            ll_km_driven.visibility=View.VISIBLE
        }
    }

    private fun clickListener() {
        buttonCall.setOnClickListener(View.OnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + dataItemModel.phone)
            startActivity(dialIntent)
        })
    }


    private fun getItemDetail() {
        showProgressBar()
        Log.d("dataarecive",arguments?.getString(Constants.KEY)+arguments?.getString(Constants.DOCUMENT_ID))

        db.collection(arguments?.getString(Constants.KEY)!!)
            .document(arguments?.getString(Constants.DOCUMENT_ID)!!)
            .get().addOnSuccessListener { result ->
                hideProgress()
                Log.d("dataas",result.data.toString())
                dataItemModel = result.toObject(DataItemModel::class.java)!!
                setData()
                setPagerAdapter()
                viewPagerListener()
            }
            .addOnFailureListener {
                Log.d(TAG, it.message)

            }
    }

    private fun setData() {
        tvPrice.text = Constants.CURRENCY_SYMBOL + dataItemModel.price
        tvTitle.text = dataItemModel.adTitle
        tvBrand.text = dataItemModel.brand
        tvAddress.text = dataItemModel.address
        tvDescription.text = dataItemModel.description
        tvKmDriven.text = dataItemModel.kmDriven
        tvPhone.text = dataItemModel.phone
        val dateFormat = SimpleDateFormat(
            "dd MMM", Locale.getDefault()
        )
        tvDate.text = dateFormat.format(dataItemModel.createdDate)
    }


    private fun setPagerAdapter() {
        val detailImagesAdapter = DetailImagesAdapter(context!!, dataItemModel.images, this)
        viewpager.setAdapter(detailImagesAdapter)
        circleIndicator.setViewPager(viewpager)
        viewpager.setOffscreenPageLimit(1)
        viewpager.setCurrentItem(count)

    }

    private fun showMembers() {

        h = Handler()
        r = Runnable {
            h!!.removeMessages(0)
            ++count
            if (count + 1 > dataItemModel.images?.size)
                count = 0

            viewpager.setCurrentItem(count)

            h!!.postDelayed(r, 3000)
        }


        h!!.postDelayed(r, 3000)

    }

    private fun viewPagerListener() {
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(pos: Int) {
                count = pos
                h?.removeCallbacksAndMessages(null);
                showMembers()
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    override fun onItemClick(position: Int) {
        startActivity(
            Intent(activity, PreviewImageActivity::class.java).putExtra(
                "imageurl",
                dataItemModel.images.get(position)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        h?.removeCallbacks(r)

    }
}