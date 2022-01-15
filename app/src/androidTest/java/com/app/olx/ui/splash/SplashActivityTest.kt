package com.app.olx.ui.splash

import android.widget.ImageView
import android.widget.TextView
import androidx.test.rule.ActivityTestRule
import com.app.olx.R
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class SplashActivityTest {


    @Rule
    @JvmField
    open var mActivityTestRule: ActivityTestRule<SplashActivity> =
        ActivityTestRule<SplashActivity>(SplashActivity::class.java)

    private var mSplashActivity: SplashActivity? = null

    @Before
    fun setUp() {
        mSplashActivity = mActivityTestRule.activity
    }

    @Test
    public fun testLaunch() {
        var view = mSplashActivity?.findViewById<TextView>(R.id.textViewCamera)
        assertNotNull(view)
    }

    @After
    fun tearDown() {
    }
}