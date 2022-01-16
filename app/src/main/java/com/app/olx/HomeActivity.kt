package com.app.olx

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.app.olx.utils.Constants
import com.app.olx.utils.onActivityResultData
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.alhazmy13.mediapicker.Image.ImagePicker


class HomeActivity : BaseActivity() {

    private lateinit var navController: NavController
    private lateinit var onActivityResultData: onActivityResultData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_sell,
                R.id.navigation_my_ads,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount > 1)
            supportFragmentManager.popBackStackImmediate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }


    fun getOnActivityResult(onActivityResultData: onActivityResultData) {
        this.onActivityResultData = onActivityResultData
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE) {
                val mPaths = data?.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH)
                val bundle = Bundle()
                bundle.putStringArrayList(Constants.IMAGE_PATHS, mPaths)
                onActivityResultData.resultData(bundle)
            }

        }
    }
}
