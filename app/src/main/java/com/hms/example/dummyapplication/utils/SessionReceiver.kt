package com.hms.example.dummyapplication.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

public class SessionReceiver: BroadcastReceiver(){
    private val TAG = "SessionReceiver"
    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action == null) return
        Log.e(TAG, "onReceive: called")
        SessionChangeService.enqueueWork(context, intent)
    }
}
