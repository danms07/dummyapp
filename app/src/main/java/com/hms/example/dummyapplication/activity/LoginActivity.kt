package com.hms.example.dummyapplication.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.material.snackbar.Snackbar
import com.hms.example.dummyapplication.R
import com.hms.example.dummyapplication.utils.AccountBindingAsync
import com.hms.example.dummyapplication.utils.DemoConstants
import com.hms.example.dummyapplication.utils.LoadingDialog
import com.hms.example.dummyapplication.utils.VerifyDialog
import com.huawei.agconnect.auth.*
import com.huawei.agconnect.auth.VerifyCodeSettings.ACTION_REGISTER_LOGIN
import com.huawei.hmf.tasks.Task
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import kotlinx.android.synthetic.main.activity_login.*
import net.openid.appauth.*


class LoginActivity : AppCompatActivity(), View.OnClickListener,
    AccountBindingAsync.OnAccountBindListener, VerifyDialog.VerificationListener {
    private val GOOGLE_SIGN_IN: Int = 1001
    val HWID_SIGN_IN = 1003
    val TAG = "LoginActivity"
    lateinit var mCallbackManager: CallbackManager
    lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Performs the account Binding Logic for Huawei Ability Gallery
        accountBindingCheck()
        //Account Buttons Setup
        fb.setPermissions("email", "public_profile")
        hw.setOnClickListener(this)
        anon.setOnClickListener(this)
        mailBtn.setOnClickListener(this)
        google_sign_in_button.setOnClickListener(this)
        //setupGoogleSignIn()
        loadingDialog = LoadingDialog.createDialog(this)
        mCallbackManager = CallbackManager.Factory.create()
        fb.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
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

    //If the activity were started with the account binding deeplink
    private fun accountBindingCheck() {
        val appLinkIntent = intent
        val appLinkData = appLinkIntent.data
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
    }
    //var hasGooglePlayServices = false
    /*private fun setupGoogleSignIn() {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (status == ConnectionResult.SUCCESS){
            hasGooglePlayServices = true
            google_sign_in_button.visibility=View.VISIBLE
            google_sign_in_button.setOnClickListener(this)
        }
    }*/

    private fun startNavDrawer(user: AGConnectUser) {
        if (loadingDialog.isShowing) loadingDialog.dismiss()
        val intent = Intent(this, NavDrawer::class.java)
        if (user.displayName != null)
            intent.putExtra(DemoConstants.DISPLAY_NAME, user.displayName)
        else if (user.email != null)
            intent.putExtra(DemoConstants.DISPLAY_NAME, user.email)
        intent.putExtra(DemoConstants.USER_ID, user.uid)
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
            loadingDialog.show()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        builder.create().show()

    }

    private fun postData(uid: String, openId: String) {
        val task = AccountBindingAsync(
            uid,
            openId,
            this
        )
        task.execute()

    }

    override fun onAccountBind(result: Int) {
        if (loadingDialog.isShowing) loadingDialog.dismiss()
        Toast.makeText(this, "Account Bind Code:" + result, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }

    override fun onClick(v: View?) {
        loadingDialog.show()
        when (v?.id) {
            R.id.hw -> signInWithHWID()

            R.id.google_sign_in_button -> signInWithGoogle()

            R.id.anon -> signInAnonymously()

            R.id.mailBtn -> signInWithMail()
        }
    }

    private fun signInWithHWID() {
        val mAuthParam =
            HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setIdToken()
                .setAccessToken()
                .createParams()

        val mAuthManager = HuaweiIdAuthManager.getService(this, mAuthParam)
        startActivityForResult(
            mAuthManager.signInIntent,
            HWID_SIGN_IN
        )
    }

    private fun signInWithGoogle() {
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse("https://accounts.google.com/o/oauth2/auth"), // authorization endpoint
            Uri.parse("https://oauth2.googleapis.com/token")
        ) // token endpoint
        val clientId = getString(R.string.google_client_id)
        val authRequestBuilder = AuthorizationRequest.Builder(
            serviceConfig,  // the authorization service configuration
            clientId,  // the client ID, typically pre-registered and static
            ResponseTypeValues.CODE,  //
            Uri.parse("$packageName:/oauth2redirect")
        ) // the redirect URI to which the auth response is sent
        authRequestBuilder.setScope("openid email profile")
        val authRequest = authRequestBuilder.build()
        val authService = AuthorizationService(this)
        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        startActivityForResult(authIntent, GOOGLE_SIGN_IN)
        authService.dispose()
        //}
    }

    private fun signInAnonymously() {
        AGConnectAuth.getInstance().signInAnonymously().addOnSuccessListener {
            // onSuccess
            val user = it.user
            startNavDrawer(user)
        }.addOnFailureListener {
            // onFail

        }
    }

    private fun signInWithMail() {
        val input = mail.text.toString()
        val regex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
        if (!regex.matches(input)) {
            Snackbar.make(mailBtn, "Please use a valid email", Snackbar.LENGTH_SHORT).show()
            return
        }

        val settings = VerifyCodeSettings.newBuilder()
            .action(ACTION_REGISTER_LOGIN) //ACTION_REGISTER_LOGIN/ACTION_RESET_PASSWORD
            .sendInterval(30) // Minimum sending interval, ranging from 30s to 120s.
            .build()

        val task: Task<VerifyCodeResult> = EmailAuthProvider.requestVerifyCode(
            input,
            settings
        )
        task.addOnSuccessListener {
            //The verification code application is successful.
            Snackbar.make(
                mailBtn,
                "Verification code sent to your mailbox",
                Snackbar.LENGTH_SHORT
            ).show()
            Log.e("EmailAuth", "success")
            //Display dialog
            val dialog = VerifyDialog(this, input)
            dialog.listener = this
            dialog.show()
        }
            .addOnFailureListener {
                Log.e("EmailAuth", it.toString())
            }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            HWID_SIGN_IN -> {
                handleHWSignIn(data)
            }
            GOOGLE_SIGN_IN -> {
                handleGoogleSignIn(data)
            }
            else -> {
                mCallbackManager.onActivityResult(requestCode, resultCode, data)//For facebook
            }
        }
    }

    private fun handleGoogleSignIn(data: Intent?) {
        if (data != null) {
            val response = AuthorizationResponse.fromIntent(data)
            val ex = AuthorizationException.fromIntent(data)
            val authState = AuthState(response, ex)
            if (response != null) {
                val service = AuthorizationService(this)
                service.performTokenRequest(
                    response.createTokenExchangeRequest()
                ) { tokenResponse, exception ->
                    service.dispose()
                    if (exception != null) {
                        Log.e(TAG, "Token Exchange failed", exception)
                    } else {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception)
                            Log.e(
                                TAG,
                                "Token Response [ Access Token: ${tokenResponse.accessToken}, ID Token: ${tokenResponse.idToken}"
                            )

                            val credential = GoogleAuthProvider.credentialWithToken(
                                tokenResponse.idToken
                            )
                            AGConnectAuth.getInstance().signIn(credential)
                                .addOnSuccessListener {
                                    // onSuccess
                                    val user = it.user
                                    startNavDrawer(user)
                                }.addOnFailureListener {
                                    Log.e(TAG, it.toString())
                                }
                        }
                    }
                }
            }
        }
    }

    private fun handleHWSignIn(data: Intent?) {
        val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
        if (authHuaweiIdTask.isSuccessful) {
            val huaweiAccount = authHuaweiIdTask.result//
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
    }

    override fun onVerification(code: String, email: String, password: String) {
        val emailUser = EmailUser.Builder()
            .setEmail(email)
            .setVerifyCode(code)
            .setPassword(password) // Optional. If this parameter is set, the current user has created a password and can use the password to sign in.
            // If this parameter is not set, the user can only sign in using a verification code.
            .build()
        AGConnectAuth.getInstance().createUser(emailUser)
            .addOnSuccessListener {
                // After an account is created, the user is signed in by default.
                startNavDrawer(it.user)
            }

            .addOnFailureListener {
                Log.e("AuthSevice","Email Sign in failed $it")
            }

    }

    override fun onCancel() {

    }
}
