package com.hms.example.dummyapplication.utils

import android.content.Context
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.ads.identifier.AdvertisingIdClient


class DevIdThread (val context: Context, val listener: DevIdThreadListener, val which:Int): Thread() {

    override fun run(){
        when(which){
            AAID ->{
                val  inst = HmsInstanceId.getInstance(context)
                //Method 1
                val aaid = inst.id
                listener.onAAId(aaid)
                //Method 2
                /*
                val idResult: Task<AAIDResult> = inst.aaid
                idResult.addOnSuccessListener{ aaidResult ->
                    val aaid = aaidResult.id
                }.addOnFailureListener { e -> Log.d("AAID", "getAAID failure:$e") }
                */
            }
            OAID ->{
                val info = AdvertisingIdClient.getAdvertisingIdInfo(context)
                if (null != info) {
                    listener.onOAId(info.id, info.isLimitAdTrackingEnabled)
                }
            }
        }
    }

    interface DevIdThreadListener{
        fun onAAId(aaid:String)
        fun onOAId(oaid:String,isLimitAdTrackingEnabled: Boolean)
    }

    companion object{
        const val AAID=1
        const val OAID=2
    }
}