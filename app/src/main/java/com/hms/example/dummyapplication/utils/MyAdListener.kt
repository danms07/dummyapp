package com.hms.example.dummyapplication.utils

import android.content.Context
import android.widget.Toast
import com.huawei.hms.ads.AdListener

class MyAdListener(private val context: Context,private val adTag:String): AdListener() {
    override fun onAdClicked() {

    }

    override fun onAdClosed() {
        Toast.makeText(context,"$adTag allows you enjoy the app for free", Toast.LENGTH_SHORT).show()
    }

    override fun onAdFailed(p0: Int) {
        Toast.makeText(context,"Failed to load $adTag code: $p0", Toast.LENGTH_SHORT).show()
    }

    override fun onAdImpression() {

    }

    override fun onAdLeave() {

    }

    override fun onAdLoaded() {
        Toast.makeText(context,"$adTag Loaded", Toast.LENGTH_SHORT).show()
    }

    override fun onAdOpened() {

    }
}