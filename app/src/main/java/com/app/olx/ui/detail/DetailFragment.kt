package com.app.olx.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.app.olx.BaseFragment
import com.app.olx.R
import com.app.olx.model.DataItemModel
import com.app.olx.ui.previewImage.PreviewImageActivity
import com.app.olx.utils.Constants
import com.app.olx.utils.log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_details.*
import java.text.SimpleDateFormat
import java.util.*


class DetailFragment : BaseFragment(), DetailImagesAdapter.ItemClickListener, OnMapReadyCallback {

    private lateinit var dataItemModel: DataItemModel
    private val db = FirebaseFirestore.getInstance()
    internal var r: Runnable? = null
    internal var h: Handler? = null
    internal var count: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getItemDetail()
        clickListener()
        if (arguments?.getString(Constants.KEY)!! == Constants.CAR) {
            ll_km_driven.visibility = View.VISIBLE
        }
    }

    private fun clickListener() {
        buttonCall.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + dataItemModel.phone)
            startActivity(dialIntent)
        }

    }

    private fun getItemDetail() {
        showProgressBar()
        log(arguments?.getString(Constants.KEY) + arguments?.getString(Constants.DOCUMENT_ID))

        db.collection(arguments?.getString(Constants.KEY)!!)
            .document(arguments?.getString(Constants.DOCUMENT_ID)!!)
            .get().addOnSuccessListener { result ->
                hideProgress()
                log(result.data.toString())
                dataItemModel = result.toObject(DataItemModel::class.java)!!
                setData()
                setMap()
                setPagerAdapter()
                viewPagerListener()
            }
            .addOnFailureListener {
                log(it.localizedMessage!!)
            }
    }

    private fun setData() {
        log("${dataItemModel.latitude} ${dataItemModel.longitude}")

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

    private fun setMap() {
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.postLocation) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        val latLng = LatLng(dataItemModel.latitude, dataItemModel.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("Add posted at!")
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        googleMap.addMarker(markerOptions)
    }


    private fun setPagerAdapter() {
        val detailImagesAdapter = DetailImagesAdapter(context!!, dataItemModel.images, this)
        viewpager.adapter = detailImagesAdapter
        circleIndicator.setViewPager(viewpager)
        viewpager.offscreenPageLimit = 1
        viewpager.currentItem = count
    }

    private fun showMembers() {
        h = Handler()
        r = Runnable {
            h!!.removeMessages(0)
            ++count
            if (count + 1 > dataItemModel.images.size)
                count = 0

            viewpager.currentItem = count

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
                h?.removeCallbacksAndMessages(null)
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
                dataItemModel.images[position]
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        h?.removeCallbacks(r)

    }
}