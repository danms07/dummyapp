package com.hms.example.dummyapplication.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.hms.example.dummyapplication.AccountBindingAsync
import com.hms.example.dummyapplication.DemoConstants
import com.hms.example.dummyapplication.R
import com.huawei.agconnect.auth.*
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper


class LoginActivity : AppCompatActivity(), View.OnClickListener,
    AccountBindingAsync.OnAccountBindListener {
    private val GOOGLE_SIGN_IN: Int = 1001
    val REQUEST_SIGN_IN_LOGIN_CODE = 1003
    val TAG = "LoginActivity"
    lateinit var mCallbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val hwBtn: Button = findViewById(R.id.hw)
        val fbBtn: LoginButton = findViewById(R.id.fb)
        fbBtn.setPermissions("email", "public_profile")
        hwBtn.setOnClickListener(this)
        findViewById<Button>(R.id.anon).setOnClickListener(this)
        setUpGoogleLogin()
        val appLinkIntent = getIntent()
        //val appLinkAction = appLinkIntent.getAction()
        val appLinkData = appLinkIntent.getData()
        if (appLinkData != null) {
            val openId = appLinkData.getQueryParameter("openId")
            if (openId != null && openId != "") {
                if (AGConnectAuth.getInstance().currentUser != null) {
                    val user: AGConnectUser = AGConnectAuth.getInstance().currentUser
                    Log.e("User", "${user.displayName}\t${user.email}\t${user.uid}\t${user.phone}")
                    displayDialog(user.displayName, user.uid, openId)
                }

            }
        }
        mCallbackManager = CallbackManager.Factory.create()
        fbBtn.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                val accessToken: String = loginResult.accessToken.token
                val credential = FacebookAuthProvider.credentialWithToken(accessToken)
                AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener {
                    // onSuccess
                    val user = it.user
                    startNavDrawer(user)
                }.addOnFailureListener {
                    // onFail
                    Log.e(TAG, it.toString())
                }
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })
    }

    private fun setUpGoogleLogin() {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (status == ConnectionResult.SUCCESS) {
            val googleButton = findViewById<SignInButton>(R.id.sign_in_button)
            googleButton.visibility = View.VISIBLE
            googleButton.setOnClickListener(this)
        }
    }

    private fun startNavDrawer(user: AGConnectUser) {
        val intent = Intent(this, NavDrawer::class.java)
        if(user.displayName!=null)
            intent.putExtra(DemoConstants.DISPLAY_NAME,user.displayName)
        intent.putExtra(DemoConstants.USER_ID,user.uid)
        startActivity(intent)
        finish()
    }


    private fun displayDialog(displayName: String, uid: String, openId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Account binding")
        builder.setMessage("Continue as $displayName?")
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.ok) { dialog, _ ->
            postData(uid, openId)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        builder.create().show()

    }

    private fun postData(uid: String, openId: String) {
        val task = AccountBindingAsync(uid, openId, this)
        task.execute()

    }

    override fun onAccountBind(result: Int) {
        Toast.makeText(this, "Account Bind Code:" + result, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.hw -> {
                val mAuthParam =
                    HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                        .setIdToken()
                        .setAccessToken()
                        .createParams()

                val mAuthManager = HuaweiIdAuthManager.getService(this, mAuthParam)
                startActivityForResult(
                    mAuthManager.signInIntent,
                    REQUEST_SIGN_IN_LOGIN_CODE
                )
            }

            R.id.sign_in_button -> {
                val gso: GoogleSignInOptions =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("551750913679-hj805b8u3m33ajo3qukthlj7bj8iecv5.apps.googleusercontent.com")
                        .requestProfile()
                        .build()
                val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
                val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
            }

            R.id.anon -> {
                AGConnectAuth.getInstance().signInAnonymously().addOnSuccessListener {
                    // onSuccess
                    val user = it.user
                    startNavDrawer(user)
                }.addOnFailureListener {
                    // onFail

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN_LOGIN_CODE) {
            //Huawei login success
            val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                val huaweiAccount = authHuaweiIdTask.result
                /**** english doc:For security reasons, the operation of changing the code to an AT must be performed on your server. The code is only an example and cannot be run.  */
                /** */
                val accessToken = huaweiAccount.accessToken
                val credential = HwIdAuthProvider.credentialWithToken(accessToken)
                AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener {
                    // onSuccess
                    val user = it.user
                    startNavDrawer(user)
                }.addOnFailureListener {
                    // onFail
                    Log.e(TAG, it.toString())
                }
            } else {
                Log.i(
                    TAG,
                    "signIn get code failed: " + (authHuaweiIdTask.exception as ApiException).statusCode
                )
            }
        } else if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.addOnSuccessListener { googleSignInAccount ->
                val idToken = googleSignInAccount.idToken
                val credential = GoogleAuthProvider.credentialWithToken(idToken)
                AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener {
                    // onSuccess
                    val user = it.user
                    startNavDrawer(user)
                }.addOnFailureListener {
                    // onFail
                }
            }
                .addOnFailureListener { exception -> Log.e(TAG, exception.toString()) }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data)//For facebook
        }
    }
}
