package com.hms.example.dummyapplication.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hms.example.dummyapplication.R
import com.huawei.agconnect.applinking.AppLinking

class LinkFragment : Fragment(), View.OnClickListener {
    lateinit var nameText:EditText
    lateinit var result:TextView
    private val TAG="LinkFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root=inflater.inflate(R.layout.fragment_link, container, false)
        val btn=root.findViewById<Button>(R.id.link_btn)
        nameText=root.findViewById(R.id.name)
        result=root.findViewById(R.id.result)
        btn.setOnClickListener(this)
        return root
    }

    override fun onClick(v: View?) {
        val name=nameText.text.toString()
        val builder =
            AppLinking.Builder().setUriPrefix("https://dummyappdemo.dre.agconnect.link")
                .setDeepLink(Uri.parse("https://dummyapp.com/target?name=$name"))
                /*.setSocialCardInfo(//Optional
                    SocialCardInfo.Builder().setTitle("Dummy App")
                        .setImageUrl("https://example.com/1.png").setDescription("Description")
                        .build()
                )*/
        //Generate a long link
        val applinkgUri = builder.buildAppLinking().uri
        //Generate a short link
        val shortBuilder = AppLinking.Builder()
            .setLongLink(applinkgUri)
        shortBuilder.buildShortAppLinking().addOnSuccessListener { shortAppLinking ->
            val shortLinkUri: Uri = shortAppLinking.shortUrl
            result.text = shortLinkUri.toString()
        }.addOnFailureListener { e ->//Handle failure
            Log.e(TAG,e.toString())
        }

        //result.text = applinkgUri.toString()
    }

}
