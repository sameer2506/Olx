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
        return inflater.inflate(R.layout.fragment_include_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listener()
        if (arguments?.getString(Constants.KEY)!! == Constants.CAR) {
            ll_km_driven.visibility = View.VISIBLE
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
        when {
            edBrand.text?.isEmpty()!! -> {
                edBrand.error = getString(R.string.enter_brand)
            }
            edYear.text.toString().isEmpty() -> {
                edYear.error = getString(R.string.enter_year)
            }
            edAdTitle.text.toString().isEmpty() -> {
                edAdTitle.error = getString(R.string.enter_ad_title)
            }
            edSelling.text.toString().isEmpty() -> {
                edSelling.error = getString(R.string.enter_description)
            }
            edPrice.text.toString().isEmpty() -> {
                edPrice.error = getString(R.string.enter_price)
            }
            edAddress.text.toString().isEmpty() -> {
                edAddress.error = getString(R.string.enter_address)
            }
            edPhone.text.toString().isEmpty() -> {
                edPhone.error = getString(R.string.enter_phone)
            }
            else -> {
                val bundle = Bundle()
                bundle.putString(Constants.BRAND, edBrand.text.toString())
                bundle.putString(Constants.YEAR, edYear.text.toString())
                bundle.putString(Constants.AD_TITLE, edAdTitle.text.toString())
                bundle.putString(Constants.AD_DESCRIPTION, edSelling.text.toString())
                bundle.putString(Constants.ADDRESS, edAddress.text.toString())
                bundle.putString(Constants.PHONE, edPhone.text.toString())
                bundle.putString(Constants.PRICE, edPrice.text.toString())
                bundle.putString(Constants.KM_DRIVEN, edUsage.text.toString())
                bundle.putString(Constants.KEY, arguments?.getString(Constants.KEY))
                findNavController().navigate(R.id.action_include_details_to_uploadPhoto, bundle)
            }
        }
    }

}