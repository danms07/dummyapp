package com.hms.example.dummyapplication.service

import android.util.Log
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage

class MyHuaweiPushService: HmsMessageService() {
    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.e("onNewToken",p0)
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        //super.onMessageReceived(message)
        if(message!=null){
            Log.i("OnNewMessage",message.data)
            val map=message.dataOfMap
            for( key in map.keys){
                Log.e("onNewMessage",map[key]!!)
            }
        }
    }
}