package com.app.olx.ui.sell

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.olx.BaseFragment
import com.app.olx.R
import com.app.olx.model.CategoriesModel
import com.app.olx.ui.sell.adapter.SellAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_sell.*

class SellFragment : BaseFragment(), SellAdapter.ItemClickListener {

    private lateinit var categoriesModel: MutableList<CategoriesModel>
    private val TAG: String= SellFragment::class.java.simpleName
    private lateinit var offeringAdapter: SellAdapter
    private lateinit var dashboardViewModel: SellViewModel
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(SellViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_sell, container, false)
        return root
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getCategoryList()
    }

    private fun getCategoryList() {
        showProgressBar()
        db.collection("Categories")
            .get().addOnSuccessListener { result ->
                hideProgress()
                categoriesModel = result.toObjects(CategoriesModel::class.java)
                setAdapter()
            }
            .addOnFailureListener{
                Log.d(TAG, it.message)

            }
    }


    private fun setAdapter() {
        rv_offering.layoutManager = GridLayoutManager(context, 3)
        offeringAdapter = SellAdapter(categoriesModel, this)
        rv_offering.adapter = offeringAdapter
    }

    override fun onItemClick(position: Int) {
        var bundle = Bundle()
        bundle.putString("key",categoriesModel.get(position).key)
        findNavController().navigate(R.id.action_sell_to_include_details,bundle)

    }
}