package edu.umich.yanfuguo.contactap.ui.contactinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactInfoViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Contact Info Fragment"
    }
    val text: LiveData<String> = _text
}