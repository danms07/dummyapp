package com.hms.example.dummyapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.hms.example.dummyapplication.utils.DevIdThread

import com.hms.example.dummyapplication.R


class DeviceIdFragment : Fragment(), View.OnClickListener, DevIdThread.DevIdThreadListener {
    lateinit var aaidText: TextView
    lateinit var oaidText: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root=inflater.inflate(R.layout.fragment_device_id, container, false)
        aaidText=root.findViewById(R.id.aaid_tv)
        oaidText=root.findViewById(R.id.devid_tv)
        val aaidbtn: Button =root.findViewById(R.id.aaidbtn)
        val oaidbtn: Button=root.findViewById(R.id.oaidbtn)
        aaidbtn.setOnClickListener(this)
        oaidbtn.setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.aaidbtn ->{
                DevIdThread(
                    requireContext(),
                    this,
                    DevIdThread.AAID
                ).start()
            }
            R.id.oaidbtn ->{
                DevIdThread(
                    requireContext(),
                    this,
                    DevIdThread.OAID
                ).start()
            }
            else ->{

            }
        }
    }

    override fun onAAId(aaid: String) {
        activity?.runOnUiThread{
            aaidText.text=aaid
        }
    }

    override fun onOAId(oaid: String, isLimitAdTrackingEnabled: Boolean) {
        activity?.runOnUiThread{
            oaidText.text=oaid
            if(isLimitAdTrackingEnabled){
                Snackbar.make(oaidText,"Limit Ad Tracking Enabled",Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(oaidText,"Limit Ad Tracking Not Enabled",Snackbar.LENGTH_SHORT).show()
            }
        }
    }

}
