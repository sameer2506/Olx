package com.app.olx.ui.myAds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.olx.BaseFragment
import com.app.olx.R
import com.app.olx.model.DataItemModel
import com.app.olx.ui.myAds.adapter.MyAdsAdapter
import com.app.olx.utils.Constants
import com.app.olx.utils.SharedPref
import com.app.olx.utils.log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_my_ads.*

class MyAdsFragment : BaseFragment(), View.OnClickListener, MyAdsAdapter.ItemClickListener {

    private var documentIdList: MutableList<DataItemModel> = ArrayList()
    private lateinit var dataItemModel: MutableList<DataItemModel>
    private var myAdsAdapter: MyAdsAdapter? = null
    private val db = FirebaseFirestore.getInstance()
    private var documentCount = 0
    private var count = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_ads, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listener()
        getMyAdsList()

        //set linear layout manager
        rv_ads.layoutManager = LinearLayoutManager(context)
    }

    private fun getMyAdsList() {
        showProgressBar()
        db.collection(Constants.CATEGORIES)
            .get().addOnSuccessListener { result ->
                documentIdList = ArrayList()
                count = 0
                documentCount = result.documents.size
                for (i in result.documents) {
                    getDataFromKeys(i.getString("key")!!)
                }
            }
            .addOnFailureListener {
                log(it.localizedMessage!!)
            }
    }

    private fun getDataFromKeys(key: String) {
        db.collection(key)
            .whereEqualTo("userId", SharedPref(activity!!).getString(Constants.USER_ID))
            .get().addOnSuccessListener { result ->
                hideProgress()
                count++
                dataItemModel = result.toObjects(DataItemModel::class.java)
                documentIdList.addAll(dataItemModel)

                if (count == documentCount && documentIdList.size > 0) {
                    ll_no_data.visibility = View.GONE
                    setAdapter()
                } else {
                    ll_no_data.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                log(it.localizedMessage!!)
            }
    }

    private fun setAdapter() {
        myAdsAdapter =
            MyAdsAdapter(documentIdList, this)
        if (rv_ads != null)
            rv_ads.adapter = myAdsAdapter
    }

    private fun listener() {
        buttonPost.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonPost -> {
                findNavController().navigate(R.id.action_ads_to_sell)
            }
        }
    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle()
        bundle.putString(Constants.DOCUMENT_ID, documentIdList[position].id)
        bundle.putString(Constants.KEY, documentIdList[position].type)
        findNavController().navigate(R.id.action_my_ads_to_detail, bundle)
    }

}