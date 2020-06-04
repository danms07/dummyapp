package com.hms.example.dummyapplication.ui.crash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CrashViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Crash Fragment"
    }
    val text: LiveData<String> = _text
}