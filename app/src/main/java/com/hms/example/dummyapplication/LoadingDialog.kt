package com.hms.example.dummyapplication

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater

class LoadingDialog  {
    companion object{
        fun createDialog(context: Context):AlertDialog{
            val builder=AlertDialog.Builder(context)
            builder.setTitle(R.string.load_dialog_title)
            val inflater=LayoutInflater.from(context)
            val view=inflater.inflate(R.layout.loading_layout,null)
            builder.setView(view)
            builder.setCancelable(false)
            return builder.create()
        }
    }
}