package com.hms.example.dummyapplication.service

import android.util.Log
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage

class MyHuaweiPushService: HmsMessageService() {
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.d("onNewToken",p0)
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        message?.let {
            Log.i("OnNewMessage",it.data)
            val map=it.dataOfMap
            for( key in map.keys){
                Log.d("onNewMessage",map[key]!!)
            }
        }
    }
}