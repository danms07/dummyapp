package com.hms.example.dummyapplication.activity

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.hms.example.dummyapplication.utils.DemoConstants
import com.hms.example.dummyapplication.R
import com.hms.example.dummyapplication.utils.TokenTask
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.core.service.auth.TokenSnapshot
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.aaid.entity.AAIDResult
import com.huawei.hms.ads.HwAds
import com.huawei.hms.feature.install.FeatureInstallManager
import com.huawei.hms.feature.install.FeatureInstallManagerFactory
import com.huawei.hms.feature.listener.InstallStateListener
import com.huawei.hms.feature.model.FeatureInstallRequest
import com.huawei.hms.feature.model.FeatureInstallSessionStatus
import com.huawei.hms.feature.model.InstallState
import com.huawei.hms.feature.tasks.FeatureTask
import com.huawei.hms.feature.tasks.listener.OnFeatureCompleteListener


class NavDrawer : AppCompatActivity(),
    TokenTask.TokenTaskListener, InstallStateListener,
    OnSuccessListener<AAIDResult>, View.OnClickListener {

    private val TAG = "NavDrawer"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var fab: FloatingActionButton
    private var mFeatureInstallManager:FeatureInstallManager?=null
    private var sessionId=1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav_drawer)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerLayout=navView.getHeaderView(0)
        val displayName=headerLayout.findViewById<TextView>(R.id.display_name)
        val userId=headerLayout.findViewById<TextView>(R.id.userid)
        val intent=this.intent
        if(intent.hasExtra(DemoConstants.DISPLAY_NAME)){
            displayName.text=intent.getStringExtra(DemoConstants.DISPLAY_NAME)
        }
        else displayName.text=getString(R.string.anonymous)

        if(intent.hasExtra(DemoConstants.USER_ID)){
            userId.text=intent.getStringExtra(DemoConstants.USER_ID)
        }



        val textLogout=navView.findViewById<TextView>(R.id.logout)
        textLogout.setOnClickListener(this)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_cloud_function,
                R.id.nav_scan,
                R.id.nav_crash,
                R.id.nav_link,
                R.id.publishAPI,
                R.id.deviceId,
                R.id.AccountBind,
                R.id.map,
                R.id.drive,
                R.id.ads
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setupAGCAuth()
        getToken()
    }

    private fun setupAGCAuth() {
        AGConnectAuth.getInstance().addTokenListener {
            when(it.state){
                TokenSnapshot.State.TOKEN_UPDATED ->{
                    val token = it.token
                    Log.i("Main Activity",token)
                }

                TokenSnapshot.State.TOKEN_INVALID ->{
                    Log.e(TAG,"Token invalid")
                }
                TokenSnapshot.State.SIGNED_OUT->{
                    Log.e(TAG,"Signed Out")
                    val intent=Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else ->{

                }
            }
        }
    }

    fun getToken() {
        Log.e("MainActivity", "get token: begin")
        Thread(TokenTask(this)).start()
    }

    override fun onResume() {
        super.onResume()
        mFeatureInstallManager?.registerInstallListener(this)
        val intent=intent
        val bundle=intent.extras
        if(bundle!=null){
            for(key in bundle.keySet())
                Log.e(TAG,"$key ${bundle.get(key)}")
        }

    }

    override fun onPause() {
        mFeatureInstallManager?.unregisterInstallListener(this)
        super.onPause()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.features->{
                mFeatureInstallManager = FeatureInstallManagerFactory.create(this);
                val moduleNames: Set<String> = mFeatureInstallManager?.allInstalledModules!!
                //Check for all installed features
                for(feature in moduleNames){
                    Log.e(TAG,"Feature: $feature")
                }
                val featureId = getString(R.string.title_dynamicfeature1)
                if (featureId in moduleNames) {
                    //Feature is intstalled
                    val intent = Intent(this,Class.forName("com.hms.example.dynamicfeature1.DemoActivity"))
                    startActivity(intent)
                } else {
                    //Display Request Dialog
                    val request = FeatureInstallRequest.newBuilder()
                        .addModule(featureId).build()
                    val task = mFeatureInstallManager?.installFeature(request)

                    task?.addOnListener(object : OnFeatureCompleteListener<Int>() {
                        override fun onComplete(featureTask: FeatureTask<Int>) {
                            if (featureTask.isComplete) {
                                Log.d(TAG, "complete to start install.")
                                if (featureTask.isSuccessful) {
                                    // Note: result in the following code indicates sessionId.
                                    val result = featureTask.result
                                    sessionId = result
                                    Log.d(TAG, "succeed to start install. session id :$result")
                                } else {
                                    Log.d(TAG, "fail to start install.")
                                    val exception = featureTask.exception
                                    exception.printStackTrace()
                                }
                            }
                        }
                    })
                    mFeatureInstallManager?.registerInstallListener(this)
                }
            }
            R.id.settings->{
                val intent=Intent()
                //intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")
                intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    // Fallback to global settings
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }catch (e : SecurityException){
                    Log.e("Catch",e.toString())
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onToken(token: String) {
        Log.e("Token", token)
        //fetching AAID
        val inst = HmsInstanceId.getInstance(this)
        val idResult =  inst.aaid
        idResult.addOnSuccessListener(this).addOnFailureListener{
            Log.e(TAG,"Failure $it")
        }
    }

    override fun onStateUpdate(state: InstallState?) {
        //Verifying User Authorization
        if (state?.status() == FeatureInstallSessionStatus.REQUIRES_USER_CONFIRMATION) {
            try {
                val result = mFeatureInstallManager?.triggerUserConfirm(state, this, 1)
            } catch (e: SendIntentException) {
                e.printStackTrace()
            }
        }
        if (state?.status() == FeatureInstallSessionStatus.REQUIRES_PERSON_AGREEMENT) {
            try {
                // Specify the activity.
                val result =
                    mFeatureInstallManager?.triggerUserConfirm(
                        state,
                        this, 0
                    )
            } catch (e: SendIntentException) {
                e.printStackTrace()
            }

        }
        //Obtaining the download progress
        if (state?.status() == FeatureInstallSessionStatus.DOWNLOADING) {
            val process =
                ((state.bytesDownloaded() + 0.0) / state.totalBytesToDownload() * 100).toInt()
            Log.d(TAG, "Downloading progress:$process")
        }
    }
    //AAID on success listener
    override fun onSuccess(aaidResult: AAIDResult?) {
        val aaid=aaidResult?.id
        Log.e(TAG,"AAID $aaid")
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.logout ->{
                Log.e(TAG,"on click logout")
                AGConnectAuth.getInstance().signOut()
            }
        }
    }

}
