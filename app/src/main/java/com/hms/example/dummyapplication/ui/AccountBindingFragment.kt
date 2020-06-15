package com.hms.example.dummyapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.hms.example.dummyapplication.R
import com.hms.example.dummyapplication.utils.RequestBindingTask

/**
 * A simple [Fragment] subclass.
 * Use the [AccountBindingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountBindingFragment : Fragment(), View.OnClickListener,
    RequestBindingTask.OnBindingListener {
    lateinit var text:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root= inflater.inflate(R.layout.fragment_account_binding, container, false)
        val btn: Button =root.findViewById(R.id.button4)
        text=root.findViewById(R.id.textResult)
        btn.setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        if(v?.id==R.id.button4){
            RequestBindingTask(this).execute()
        }
    }

    override fun onRequestResult(link: String?){
        text.text=link
    }


}
