package com.app.olx.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.olx.BaseFragment
import com.app.olx.R
import com.app.olx.utils.Constants
import com.app.olx.utils.SharedPref
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(), View.OnClickListener {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        return root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        edFullName.setText(SharedPref(activity!!).getString(Constants.USER_NAME))
        edEmail.setText(SharedPref(activity!!).getString(Constants.USER_EMAIL))
        edPhone.setText(SharedPref(activity!!).getString(Constants.USER_PHONE))
        edAddress.setText(SharedPref(activity!!).getString(Constants.USER_ADDRESS))

        listener()
    }

    private fun listener() {
        textViewSave.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.textViewSave -> {
                validateData()
            }
        }
    }

    private fun validateData() {
        if (edFullName.text?.isEmpty()!!) {
            edFullName.setError(getString(R.string.enter_full_name))
        } else if (edEmail.text.toString().isEmpty()) {
            edEmail.setError(getString(R.string.enter_email))
        } else if (edPhone.text.toString().isEmpty()) {
            edPhone.setError(getString(R.string.enter_phone))
        } else {
            SharedPref(activity!!).setString(Constants.USER_NAME, edFullName.text.toString())
            SharedPref(activity!!).setString(Constants.USER_EMAIL, edEmail.text.toString())
            SharedPref(activity!!).setString(Constants.USER_PHONE, edPhone.text.toString())
            SharedPref(activity!!).setString(Constants.USER_ADDRESS, edAddress.text.toString())
            Toast.makeText(activity!!, "Saved Successfully", Toast.LENGTH_LONG).show()
            fragmentManager?.popBackStack()
        }
    }



}