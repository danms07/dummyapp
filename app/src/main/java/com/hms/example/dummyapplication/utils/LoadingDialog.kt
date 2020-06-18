package com.hms.example.dummyapplication.utils

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.hms.example.dummyapplication.R

class LoadingDialog  {
    companion object{
        fun createDialog(context: Context,titleId:Int=R.string.load_dialog_title,messageId:Int=R.string.load_dialog_message):AlertDialog{
            val builder=AlertDialog.Builder(context)
            builder.setTitle(titleId)
            val inflater=LayoutInflater.from(context)
            val view=inflater.inflate(R.layout.loading_layout,null)
            val dialogMessage=view.findViewById<TextView>(R.id.dialogMessage)
            dialogMessage.setText(messageId)
            builder.setView(view)
            builder.setCancelable(false)
            return builder.create()
        }

    }
}