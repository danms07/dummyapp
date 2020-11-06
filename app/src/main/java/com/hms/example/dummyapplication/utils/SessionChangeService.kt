package com.hms.example.dummyapplication.utils

import android.content.Context
import android.content.Intent
import android.provider.SyncStateContract
import android.util.Log
import androidx.core.app.JobIntentService

class SessionChangeService: JobIntentService() {



    companion object{
        private val SESSION_CHANGE_JOB_ID: Int=100

        fun enqueueWork(context: Context?, intent: Intent?) {
            Log.d("JOB", "enqueueWork: enqueued")
            enqueueWork(
                context!!,
                SessionChangeService::class.java,
                SESSION_CHANGE_JOB_ID,
                intent!!
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        TODO("Not yet implemented")
    }
}