package com.app.olx.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.app.olx.BaseActivity
import com.app.olx.HomeActivity
import com.app.olx.R
import com.app.olx.utils.Constants
import com.app.olx.utils.SharedPref
import com.app.olx.utils.log
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : BaseActivity(), View.OnClickListener {

    private var callbackManager: CallbackManager? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private var googleSignInClient: GoogleSignInClient? = null

    private val RC_SIGN_IN: Int = 1

    private val EMAIL = "email"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FacebookSdk.sdkInitialize(applicationContext)
        auth = FirebaseAuth.getInstance()

        callbackManager = CallbackManager.Factory.create()

        fbLoginButton.setReadPermissions(listOf(EMAIL))

        clickListener()

        configureGoogleSignIn()
        registerFbCallBack()
    }

    private fun registerFbCallBack() {

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Facebook Login Failed:(" + exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                }
            })

    }

    private fun handleFacebookAccessToken(accessToken: AccessToken?) {
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    log("signInWithCredential: success")
                    val user = auth.currentUser
                    log("$user")

                    SharedPref(this).setString(Constants.USER_ID, user?.uid!!)
                    if (user.email != null)
                        SharedPref(this).setString(Constants.USER_EMAIL, user.email!!)
                    if (user.displayName != null)
                        SharedPref(this).setString(Constants.USER_NAME, user.displayName!!)
                    if (user.photoUrl != null)
                        SharedPref(this).setString(Constants.USER_PHOTO, user.photoUrl!!.toString())
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    log("signInWithCredential: failed")
                    Toast.makeText(this, "Try again later.", Toast.LENGTH_LONG).show()
                }
            }
    }

    /*Click Listener*/
    private fun clickListener() {
        buttonGoogleLogin.setOnClickListener(this)
        buttonFbLogin.setOnClickListener(this)
    }

    /*Click Listeners Callback*/
    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.buttonGoogleLogin -> {
                googleSignIn()
            }
            R.id.buttonFbLogin -> {
                fbLoginButton.performClick()
            }

        }
    }

    private fun googleSignIn() {
        val signInIntent: Intent = googleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /*Configuring Google Sign In*/
    private fun configureGoogleSignIn() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    /* OnActivity Result CallBack
    * This methods is callback when you successfull or failed
    * to login with google or facebook
    * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: Exception) {
                Toast.makeText(this, "Google sign in failed:(" + e.message, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }


    /*Check if User Login with google is Successfully or Failed*/
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                log("$acct")

                SharedPref(this).setString(Constants.USER_ID, acct.id!!)
                if (acct.email != null)
                    SharedPref(this).setString(Constants.USER_EMAIL, acct.email!!)
                if (acct.displayName != null)
                    SharedPref(this).setString(Constants.USER_NAME, acct.displayName!!)
                if (acct.photoUrl != null)
                    SharedPref(this).setString(Constants.USER_PHOTO, acct.photoUrl!!.toString())
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Google sign in failed:(" + it.exception?.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}