package com.hms.example.dummyapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import com.hms.example.dummyapplication.R
import com.hms.example.dummyapplication.connectapi.PublishAPIAsync


class PublishApiFragment : Fragment(), View.OnClickListener{

    lateinit var text:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =inflater.inflate(R.layout.fragment_publish_api, container, false)
        val btn=root.findViewById<Button>(R.id.publishbtn)
        btn.setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        PublishAPIAsync().execute()
    }



}
