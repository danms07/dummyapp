package com.hms.example.dummyapplication.ui.remote

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hms.example.dummyapplication.R
import com.huawei.agconnect.remoteconfig.AGConnectConfig

class RemoteFragment : Fragment() {

    private lateinit var remoteViewModel: RemoteViewModel
    private val TAG="Remote Fragment"
    val config = AGConnectConfig.getInstance()
    lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        remoteViewModel =
            ViewModelProvider(this).get(RemoteViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_remote, container, false)
        textView  = root.findViewById(R.id.text_gallery)
        remoteViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchRemotes()
    }


    /**
     * Fetch the remotes each 12 hours
     * To customize the fetch interval call fetch(intervalSeconds : Long)
     */
    private fun fetchRemotes() {

        config.fetch(10).addOnSuccessListener {
            config.apply(it)
            val textColor=config.getValueAsString("text_color")
            val labelText=config.getValueAsString("label_text")
            val textBackground=config.getValueAsString("label_color")
            textView.text=labelText
            val labelColor:Int
            val resources=activity?.resources
            if(resources!=null){
                labelColor = when(textBackground){
                    "blue"->{
                        resources.getColor(R.color.blue,resources.newTheme())
                    }
                    "red" ->{
                        resources.getColor(R.color.huaweiRed,resources.newTheme())
                    }
                    "white"->{
                        resources.getColor(R.color.white,resources.newTheme())
                    }
                    else ->{
                        resources.getColor(R.color.green,resources.newTheme())
                    }
                }
                textView.setBackgroundColor(labelColor)
                val fontColor:Int = when(textColor){
                    "black"->{
                        resources.getColor(R.color.black,resources.newTheme())
                    }
                    "white"->{
                        resources.getColor(R.color.white,resources.newTheme())
                    }
                    else->{
                        resources.getColor(R.color.gray,resources.newTheme())
                    }
                }
                textView.setTextColor(fontColor)
            }

        }.addOnFailureListener{
            Log.e(TAG,it.toString())
        }
    }
}
