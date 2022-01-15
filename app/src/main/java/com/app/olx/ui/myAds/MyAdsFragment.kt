package com.app.olx.ui.myAds

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.olx.BaseFragment
import com.app.olx.R
import com.app.olx.model.DataItemModel
import com.app.olx.ui.myAds.adapter.MyAdsAdapter
import com.app.olx.utils.Constants
import com.app.olx.utils.SharedPref
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_my_ads.*

class MyAdsFragment : BaseFragment(), View.OnClickListener, MyAdsAdapter.ItemClickListener {


    private val TAG = MyAdsFragment::class.java.simpleName
    private var documentIdList: MutableList<DataItemModel> = ArrayList()
    private lateinit var dataItemModel: MutableList<DataItemModel>
    private var myAdsAdapter: MyAdsAdapter? = null
    val db = FirebaseFirestore.getInstance()
    var documentCount=0
    var count=0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_my_ads, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listener()
        getMyAdsList()

        //set linear layout managger
        rv_ads.layoutManager = LinearLayoutManager(context)

    }


    private fun getMyAdsList() {
        showProgressBar()
        db.collection(Constants.CATEGORIES)
            .get().addOnSuccessListener { result ->
                documentIdList = ArrayList()
                count = 0
                documentCount =result.documents.size
                for (i in result.documents) {
                    getDataFromKeys(i.getString("key")!!)

                }


            }
            .addOnFailureListener {
                Log.d(TAG, it.message)

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

                if (count==documentCount&&documentIdList.size>0){
                    ll_no_data.visibility=View.GONE
                    setAdapter()
                }else{
                    ll_no_data.visibility=View.VISIBLE
                }


            }
            .addOnFailureListener {
                Log.d(TAG, it.message)

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
        var bundle = Bundle()
        bundle.putString(Constants.DOCUMENT_ID, documentIdList.get(position).id)
        bundle.putString(Constants.KEY, documentIdList.get(position).type)
        findNavController().navigate(R.id.action_my_ads_to_detail, bundle)
    }

}