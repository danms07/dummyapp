package com.hms.example.dummyapplication.ui.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RemoteViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is remote Fragment"
    }
    val text: LiveData<String> = _text
}