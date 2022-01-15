package com.app.olx.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.app.olx.BaseFragment
import com.app.olx.R
import com.app.olx.ui.login.LoginActivity
import com.app.olx.utils.Constants
import com.app.olx.utils.SharedPref
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : BaseFragment(), View.OnClickListener {

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_profile, container, false)


        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listener()
        tvName.text = SharedPref(activity!!).getString(Constants.USER_NAME)
        tvEmail.text = SharedPref(activity!!).getString(Constants.USER_EMAIL)
        Log.d("profileimagees",SharedPref(activity!!).getString(Constants.USER_PHOTO))
        Glide.with(activity!!).
            load(SharedPref(activity!!).getString(Constants.USER_PHOTO))
            .placeholder(R.drawable.avatar)
            .into(imageViewAvatar)

    }

    private fun listener() {
        ll_settings.setOnClickListener(this)
        ll_logout.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){

            R.id.ll_settings->{
                findNavController().navigate(com.app.olx.R.id.action_profile_to_settings)
            }

            R.id.ll_logout->{
                showLogoutDialog()

            }
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(activity!!)
        //set title for alert dialog
        builder.setTitle(R.string.logout)
        //set message for alert dialog
        builder.setMessage(R.string.logout_message)
        builder.setIcon(R.drawable.ic_warning)


        //performing positive action
        builder.setPositiveButton("Yes"){dialogInterface, which ->
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut();

            clearSession()

            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton("No"){dialogInterface, which ->
        dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun clearSession() {
        SharedPref(activity!!).setString(Constants.USER_NAME,"")
        SharedPref(activity!!).setString(Constants.USER_ID,"")
        SharedPref(activity!!).setString(Constants.USER_EMAIL,"")
        SharedPref(activity!!).setString(Constants.USER_PHOTO,"")
        SharedPref(activity!!).setString(Constants.USER_PHONE,"")
    }

}