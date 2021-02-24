package com.hms.example.dummyapplication.ui.crash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hms.example.dummyapplication.R
import com.huawei.agconnect.crash.AGConnectCrash

class CrashFragment : Fragment(), View.OnClickListener {

    private lateinit var crashViewModel: CrashViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        crashViewModel =
            ViewModelProvider(this).get(CrashViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_crash, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        crashViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        val btn: Button =root.findViewById(R.id.crashbtn)
        btn.setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        AGConnectCrash.getInstance().testIt(requireContext())
    }
}
