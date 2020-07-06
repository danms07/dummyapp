package com.hms.example.dummyapplication.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hms.example.dummyapplication.R
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.splash.SplashView.SplashAdLoadListener
import kotlinx.android.synthetic.main.activity_ads.*


class AdsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)
    }

    fun loadAd(){
        //Create an intent to jump to the next acivity
        val intent=Intent(this,NavDrawer::class.java)

        //Add a listener to jump when the ad finished or if there aren't internet connection
        val splashAdLoadListener: SplashAdLoadListener = object : SplashAdLoadListener() {
            override fun onAdLoaded() {
                // Called when an ad is loaded successfully.
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Called when an ad failed to be loaded. The app home screen is then displayed.
                startActivity(intent)
            }

            override fun onAdDismissed() {
                // Called when the display of an ad is complete. The app home screen is then displayed.
                startActivity(intent)
            }
        }

        val splashView = splash//find view by id is not needed in kotlin
        val id=getString(R.string.ad_id2)
        val orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val adParam = AdParam.Builder().build()
        splashView.load(id, orientation, adParam, splashAdLoadListener)
    }
}