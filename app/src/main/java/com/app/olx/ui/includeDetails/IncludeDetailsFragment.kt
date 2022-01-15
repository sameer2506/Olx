package com.app.olx.ui.includeDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.olx.BaseFragment
import com.app.olx.R
import com.app.olx.utils.Constants
import kotlinx.android.synthetic.main.fragment_include_details.*

class IncludeDetailsFragment : BaseFragment(), View.OnClickListener {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_include_details, container, false)
        return root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listener()
        if (arguments?.getString(Constants.KEY)!!.equals(Constants.CAR)){
            ll_km_driven.visibility=View.VISIBLE
        }

    }

    private fun listener() {
        textViewNext.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.textViewNext -> {
                validateData()
            }
        }
    }

    private fun validateData() {
        if (edBrand.text?.isEmpty()!!){
            edBrand.setError(getString(R.string.enter_brand))
        }else if (edYear.text.toString().isEmpty()){
            edYear.setError(getString(R.string.enter_year))
        }else if (edAdTitle.text.toString().isEmpty()){
            edAdTitle.setError(getString(R.string.enter_ad_title))
        }else if (edSelling.text.toString().isEmpty()){
            edSelling.setError(getString(R.string.enter_description))
        }else if (edPrice.text.toString().isEmpty()){
            edPrice.setError(getString(R.string.enter_price))
        }else if (edAddress.text.toString().isEmpty()){
            edAddress.setError(getString(R.string.enter_address))
        }else if (edPhone.text.toString().isEmpty()){
            edPhone.setError(getString(R.string.enter_phone))
        }else{
            val bundle = Bundle()
            bundle.putString(Constants.BRAND,edBrand.text.toString())
            bundle.putString(Constants.YEAR,edYear.text.toString())
            bundle.putString(Constants.AD_TITLE,edAdTitle.text.toString())
            bundle.putString(Constants.AD_DESCRIPTION,edSelling.text.toString())
            bundle.putString(Constants.ADDRESS,edAddress.text.toString())
            bundle.putString(Constants.PHONE,edPhone.text.toString())
            bundle.putString(Constants.PRICE,edPrice.text.toString())
            bundle.putString(Constants.KM_DRIVEN,edUsage.text.toString())
            bundle.putString(Constants.KEY,arguments?.getString(Constants.KEY))
            findNavController().navigate(R.id.action_include_details_to_uploadPhoto,bundle)
        }
    }

}