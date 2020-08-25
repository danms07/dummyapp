package com.hms.example.dummyapplication.utils

import android.content.Context
import android.content.DialogInterface
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.hms.example.dummyapplication.R
import kotlinx.android.synthetic.main.dialog_verify.view.*

class VerifyDialog(val context: Context,val email: String):DialogInterface.OnClickListener{
    lateinit var view:View
    var listener:VerificationListener?=null
    var alertDialog: AlertDialog
    init{
        val inflater=LayoutInflater.from(context)
        view=inflater.inflate(R.layout.dialog_verify,null)
        val builder=AlertDialog.Builder(context)
        builder.setTitle("Email Verification")
            .setView(view)
            .setPositiveButton("ok",this)
            .setNegativeButton("Cancel",this)
            .setCancelable(false)
        alertDialog=builder.create()
    }

    public fun show(){
        alertDialog.show()
    }



    override fun onClick(dialog: DialogInterface?, which: Int) {
        when(which){
            DialogInterface.BUTTON_POSITIVE ->{
                val code=view.inputCode.text.toString()
                if(code==""){
                    Snackbar.make(view,"Please input the code",Snackbar.LENGTH_SHORT).show()
                    return
                }
                val pass=view.inputPass.text.toString()
                val vPass=view.confirmPass.text.toString()
                if(pass.isEmpty()||pass!=vPass){
                    Snackbar.make(view,"Passwords doesn't match",Snackbar.LENGTH_SHORT).show()
                    return
                }
                listener?.onVerification(code,email,pass)
            }

            DialogInterface.BUTTON_NEGATIVE ->{
                dialog?.dismiss()
                listener?.onCancel()
            }
        }
    }

    public interface VerificationListener{
        fun onVerification(code:String,email:String,password:String)
        fun onCancel()
    }
}