package com.hms.example.dummyapplication.ui.cloud_function

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hms.example.dummyapplication.R
import com.huawei.agconnect.function.AGCFunctionException
import com.huawei.agconnect.function.AGConnectFunction
import com.huawei.agconnect.function.FunctionResult
import com.huawei.hmf.tasks.OnCompleteListener
import com.huawei.hmf.tasks.Task
import org.json.JSONException
import org.json.JSONObject

class CloudFunctionFragment : Fragment(), View.OnClickListener, OnCompleteListener<FunctionResult> {

    private lateinit var cloudFunctionViewModel: CloudFunctionViewModel
    lateinit var n1: EditText
    lateinit var n2: EditText
    val TAG = "CloudFunction"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cloudFunctionViewModel =ViewModelProvider(this).get(CloudFunctionViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cloud_function, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        cloudFunctionViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        n1 = root.findViewById(R.id.n1)
        n2 = root.findViewById(R.id.n2)
        val btn: Button = root.findViewById(R.id.btn)
        btn.setOnClickListener(this)

        return root
    }

    override fun onClick(v: View?) {
        if(v?.id==R.id.btn){

            val n1String=n1.text.toString()
            val n2String=n2.text.toString()
            if(n1String.isEmpty()||n2String.isEmpty()){
                return
            }
            val map: HashMap<String, Int> = HashMap()
            val number1 = Integer.parseInt(n1String)
            val number2 = Integer.parseInt(n2String)
            map["n1"] = number1
            map["n2"] = number2
            val function: AGConnectFunction = AGConnectFunction.getInstance()
            function.wrap("sum-${'$'}latest")
                .call(map).addOnCompleteListener(this)
        }
    }

    override fun onComplete(task: Task<FunctionResult>?) {

        if (task != null) {
            if (task.isSuccessful) {
                val value = task.result.value
                Log.e(TAG,value)
                try{
                    val json=JSONObject(value)
                    val message="Result: ${json.get("result")}"
                    cloudFunctionViewModel.setText(message)
                }catch (e:JSONException){
                    Log.e(TAG,e.toString())
                }

            } else {
                val e = task.exception
                if (e is AGCFunctionException) {

                    val errCode = e.code
                    val message = e.message
                    Log.e(TAG, "Code: $errCode\t Message: $message")
                }
                // ...
            }
        }
    }
}

