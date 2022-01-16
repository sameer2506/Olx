package com.app.olx.ui.sell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.olx.BaseFragment
import com.app.olx.R
import com.app.olx.model.CategoriesModel
import com.app.olx.ui.sell.adapter.SellAdapter
import com.app.olx.utils.log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_sell.*

class SellFragment : BaseFragment(), SellAdapter.ItemClickListener {

    private lateinit var categoriesModel: MutableList<CategoriesModel>
    private lateinit var offeringAdapter: SellAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sell, container, false)
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
            .addOnFailureListener {
                log(it.localizedMessage!!)
            }
    }

    private fun setAdapter() {
        rv_offering.layoutManager = GridLayoutManager(context, 3)
        offeringAdapter = SellAdapter(categoriesModel, this)
        rv_offering.adapter = offeringAdapter
    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle()
        bundle.putString("key", categoriesModel[position].key)
        findNavController().navigate(R.id.action_sell_to_include_details, bundle)

    }
}