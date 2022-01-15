package com.app.olx

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

open class  BaseFragment:Fragment() {
    private var mLastClickTime:Long= 0
    var progressDialog: ProgressDialog? = null
    lateinit var mDialog:Dialog


    fun showUploadProgress(s: String) {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMax(100)
        progressDialog!!.setMessage(s)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog!!.show()
        progressDialog!!.setCancelable(false)
    }

    open fun showProgressBar() {
        mDialog = Dialog(activity!!)
        // no tile for the dialog
        // mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.setContentView(R.layout.prograss_bar_dialog)

        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        mDialog.show()
    }

    open fun hideProgress() {
        mDialog.dismiss()
    }

    fun showMessage(context: Context, view: View, message: String) {
        val snakbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        snakbar.view.setBackgroundColor(
            ContextCompat.getColor(context,
                android.R.color.white))
        if (snakbar.isShown) {
            snakbar.dismiss()
        }
        snakbar.show()
    }

    fun clickWait():Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false
        } else {
            mLastClickTime = SystemClock.elapsedRealtime()
            return true

        }
    }
}