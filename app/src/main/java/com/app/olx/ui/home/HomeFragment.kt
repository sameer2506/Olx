package com.app.olx.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.olx.BaseFragment
import com.app.olx.R
import com.app.olx.model.CategoriesModel
import com.app.olx.ui.home.adapter.CategoriesAdapter
import com.app.olx.utils.Constants
import com.app.olx.utils.SharedPref
import com.app.olx.utils.log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment(), CategoriesAdapter.ItemClickListener {

    private lateinit var categoriesModel: MutableList<CategoriesModel>
    private lateinit var categoriesAdapter: CategoriesAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getCategoryList()
        textListener()

        tvCityName.text = SharedPref(activity!!).getString(Constants.CITY_NAME)
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
        rv_categories.layoutManager = GridLayoutManager(context, 3)
        categoriesAdapter = CategoriesAdapter(categoriesModel, this)
        rv_categories.adapter = categoriesAdapter
    }

    override fun onItemClick(position: Int) {
        if (clickWait()) {
            val bundle = Bundle()
            bundle.putString("key", categoriesModel[position].key)
            findNavController().navigate(R.id.action_home_to_browse_category, bundle)
        }
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
        val temp: MutableList<CategoriesModel> = ArrayList()
        for (d in categoriesModel) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.key.contains(text.capitalize()) || d.key.contains(text)) {
                temp.add(d)
            }
        }
        //update recyclerview
        categoriesAdapter.updateList(temp)
    }

}