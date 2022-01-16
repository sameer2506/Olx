package com.app.olx.ui.splash

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import androidx.core.app.ActivityCompat
import com.app.olx.BaseActivity
import com.app.olx.HomeActivity
import com.app.olx.R
import com.app.olx.ui.login.LoginActivity
import com.app.olx.utils.Constants
import com.app.olx.utils.MarshMellowHelper
import com.app.olx.utils.SharedPref
import com.app.olx.utils.log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class SplashActivity : BaseActivity() {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 1500 //1.5 seconds
    private val ENABLE_GPS_CODE = 10005
    private val PERMISSIONS_REQUEST_CODE = 10004

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null
    private var marshMellowHelper: MarshMellowHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallBack()
        locationPermissions()
        getHashKey()

    }

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            if (SharedPref(this).getString(Constants.USER_ID).isEmpty()) {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(applicationContext, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun locationPermissions() {
        marshMellowHelper = MarshMellowHelper(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSIONS_REQUEST_CODE
        )

        checkForPermissions()
    }


    private fun enableGPS() {
        locationRequest = LocationRequest.create()
        locationRequest!!.interval = 1000 // milliseconds
        locationRequest!!.fastestInterval =
            1000 // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest!!)


        val task =
            LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())


        task.addOnCompleteListener { result ->
            try {
                val response = result.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
                startLocationUpdates()

            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                this@SplashActivity,
                                ENABLE_GPS_CODE
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
                // Location settings are not satisfied. However, we have no way to fix the
                // settings so we won't show the dialog.
            }
        }
    }

    private fun locationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult

                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    val location = locationResult.lastLocation
                    SharedPref(applicationContext).setString(
                        Constants.CITY_NAME,
                        getCityName(location)!!
                    )

                    mDelayHandler!!.postDelayed(
                        mRunnable,
                        SPLASH_DELAY
                    )

                    // use your location object
                    // get latitude , longitude and other info from this
                }

            }
        }

    }

    private fun checkForPermissions() {
        marshMellowHelper!!.request(object : MarshMellowHelper.PermissionCallback {
            override fun onPermissionGranted() {
                enableGPS()
            }

            override fun onPermissionDenied() {
            }

            override fun onPermissionDeniedBySystem() {
            }
        })

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (marshMellowHelper != null) {
            marshMellowHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest!!,
            locationCallback,
            null /* Looper */
        )
    }

    private fun getCityName(location: Location): String? {
        var cityName = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude, 1
            )
            cityName = addresses[0].locality
        } catch (e: IOException) {
            when (e.message) {
                "grpc failed" -> {/* display a Toast or Snackbar instead*/
                }
                else -> throw e
            }
        }

        return cityName
    }

    private fun getHashKey() {
        val info: PackageInfo
        try {
            info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(Base64.encode(md.digest(), 0))
                log(something)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            log(e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            log(e.toString())
        } catch (e: Exception) {
            log(e.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ENABLE_GPS_CODE) {
            startLocationUpdates()
        }
    }

    public override fun onDestroy() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}
