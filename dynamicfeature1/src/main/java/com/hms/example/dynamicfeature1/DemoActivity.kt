package com.hms.example.dynamicfeature1

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.huawei.hms.feature.dynamicinstall.FeatureCompat

class DemoActivity : AppCompatActivity(), View.OnClickListener {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        // Initialize the SDK in the activity.
        FeatureCompat.install(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        val tv=findViewById<TextView>(R.id.textview)
        val fab:FloatingActionButton=findViewById(R.id.fab)
        fab.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v!=null)
            Snackbar.make(v,"Dynamic Feature",Snackbar.LENGTH_SHORT)
    }
}
