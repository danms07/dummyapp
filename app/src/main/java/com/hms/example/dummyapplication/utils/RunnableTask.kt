package com.hms.example.dummyapplication.utils

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.aaid.HmsInstanceId

class RunnableTask(val context: Context):Runnable{
    override fun run() {
        try {
            val appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id")
            val pushtoken = HmsInstanceId.getInstance(context).getToken(appId, "HCM")
            if (!TextUtils.isEmpty(pushtoken)) {
                Log.i("GetToken", "push token:$pushtoken")
                val listener: RunnableTaskListener =context as RunnableTaskListener
                listener.onTokenFetched(pushtoken)
            }
        } catch (e: Exception) {
            Log.i("Token Exception", "getToken failed, $e")
        }
    }

    interface RunnableTaskListener{
        fun onTokenFetched(token:String)
    }
}