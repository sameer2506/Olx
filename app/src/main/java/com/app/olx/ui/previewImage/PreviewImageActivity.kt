package com.app.olx.ui.previewImage

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Window
import android.view.WindowManager
import com.app.olx.BaseActivity
import com.app.olx.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_preview_image.*
import kotlin.math.max
import kotlin.math.min

class PreviewImageActivity : BaseActivity() {

    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_preview_image)
        val extras = intent.extras
        if (extras?.containsKey("imageuri")!!) {
            val imageUri = extras.getString("imageuri")
            val myBitmap = BitmapFactory.decodeFile(imageUri)
            imgDisplay!!.setImageBitmap(myBitmap)
        } else if (extras.containsKey("imageurl")) {
            val imageUrl = extras.getString("imageurl")
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.big_placeholder)
                .into(imgDisplay)
        }

        imgDisplay!!.scaleX = 1.0f
        imgDisplay!!.scaleY = 1.0f
        btnClose!!.setOnClickListener { finish() }

        mScaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        mScaleGestureDetector!!.onTouchEvent(motionEvent)
        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = max(0.1f, min(mScaleFactor, 10.0f))
            imgDisplay!!.scaleX = mScaleFactor
            imgDisplay!!.scaleY = mScaleFactor
            return true
        }
    }

}