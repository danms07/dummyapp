package com.hms.example.dummyapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hms.example.dummyapplication.R
import com.huawei.cloud.services.drive.model.File

class FileAdapter (val fileList:ArrayList<File>,private val listener: FileViewHolder.OnFileItemListener):
    RecyclerView.Adapter<FileAdapter.FileViewHolder>(){

    class FileViewHolder(private val view: View,val listener:OnFileItemListener): RecyclerView.ViewHolder(view),
        View.OnClickListener {
        fun init(file:File){
            val fileName=view.findViewById<TextView>(R.id.file_name)
            val fileSize=view.findViewById<TextView>(R.id.file_size)
            val downloadBtn=view.findViewById<Button>(R.id.dwn_btn)
            fileName.text=file.fileName
            //fileSize.text=file.occupiedSpace.toString()

            downloadBtn.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onDownloadRequest(adapterPosition)
        }

        public interface OnFileItemListener{
            fun onDownloadRequest(position: Int)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.file_item, parent, false)
        return FileViewHolder(view,listener)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.init(fileList[position])
    }


}