package com.hms.example.dummyapplication.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hms.example.dummyapplication.DemoConstants
import com.hms.example.dummyapplication.R
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectUser
import com.huawei.agconnect.core.service.auth.TokenSnapshot
import com.huawei.agconnect.crash.AGConnectCrash
import com.huawei.agconnect.remoteconfig.AGConnectConfig


class MainActivity : AppCompatActivity(), View.OnClickListener, Runnable{

    val TAG="MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AGConnectCrash.getInstance().enableCrashCollection(true)
        initDefaults()//Remote Config
        createNotificationChannel()
        if(AGConnectAuth.getInstance().currentUser!=null){
            val handler:Handler=Handler();
            handler.postDelayed(this,1000)
        }
        else{
            //Show Login Screen
            val intent=Intent(this,
                LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }



    private fun createNotificationChannel(){
            val name: CharSequence = getString(R.string.channel_name)
            val description ="Notifications Experiment"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ABTesting", name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)

    }

    private fun initDefaults() {
        //Set defaults
        val map=HashMap <String,Any>()
        map["label_color"] = "blue"
        map["text_color"] = "white"
        map["test1"] = 123
        //Apply defaults
        val config = AGConnectConfig.getInstance()
        config.applyDefault(map)
    }

    override fun onClick(v: View?) {
        /*val id=v?.id
        when(id){
            R.id.map->{
                val intent:Intent= Intent(this,MapActivity::class.java)
                startActivity(intent)
            }
            R.id.qr->{
                val intent:Intent= Intent(this,TargetActivity::class.java)
                startActivity(intent)
            }
        }*/
    }

    override fun run() {
        val user:AGConnectUser=AGConnectAuth.getInstance().currentUser
        val intent=Intent(this, NavDrawer::class.java)
        if(user.displayName!=null)
            intent.putExtra(DemoConstants.DISPLAY_NAME,user.displayName)
        intent.putExtra(DemoConstants.USER_ID,user.uid)
        startActivity(intent)
        finish()
    }

}
