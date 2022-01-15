package com.app.olx.ui.browseCategory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.olx.BaseFragment
import com.app.olx.R
import com.app.olx.model.DataItemModel
import com.app.olx.ui.browseCategory.adapter.BrowseCategoriesAdapter
import com.app.olx.utils.Constants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_browse_categories.*

class BrowseCategoryFragment : BaseFragment(),
    BrowseCategoriesAdapter.ItemClickListener {

    private lateinit var documentIdList: MutableList<DocumentSnapshot>
    private lateinit var dataItemModel: MutableList<DataItemModel>
    private lateinit var categoriesAdapter: BrowseCategoriesAdapter
    val db = FirebaseFirestore.getInstance()
    private var TAG = BrowseCategoryFragment::class.java.simpleName
    private var key = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_browse_categories, container, false)

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        key = arguments?.getString("key")!!
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getList()
        textListener()
    }


    private fun textListener() {
        edSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                // filter your list from your input
                filterList(s.toString())
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        })
    }

    private fun filterList(text: String) {
        val temp: MutableList<DataItemModel> = ArrayList()
        for (d in dataItemModel) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.adTitle.contains(text.capitalize()) || d.adTitle.contains(text) ||
                d.brand.contains(text.capitalize()) || d.brand.contains(text) ||
                d.type.contains(text.capitalize()) || d.type.contains(text) ||
                d.description.contains(text.capitalize()) || d.description.contains(text)
            ) {
                temp.add(d)
            }
        }
        //update recyclerview
        categoriesAdapter.updateList(temp)
    }

    private fun getList() {

        showProgressBar()
        db.collection(key)
            .get().addOnSuccessListener { result ->
                hideProgress()
                dataItemModel = result.toObjects(DataItemModel::class.java)
                documentIdList = result.documents
                if (documentIdList.size > 0) {
                    setAdapter()
                } else {
                    ll_no_data.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                Log.d(TAG, it.message)

            }
    }


    private fun setAdapter() {
        rv_categories.layoutManager = LinearLayoutManager(context)
        categoriesAdapter =
            BrowseCategoriesAdapter(dataItemModel, this)
        rv_categories.adapter = categoriesAdapter
    }

    override fun onItemClick(position: Int) {
        var bundle = Bundle()
        bundle.putString(Constants.DOCUMENT_ID, documentIdList.get(position).id)
        bundle.putString(Constants.KEY, key)
        findNavController().navigate(R.id.action_browse_category_to_detail, bundle)
    }

}