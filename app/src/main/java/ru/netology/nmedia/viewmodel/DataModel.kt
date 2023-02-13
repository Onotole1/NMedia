package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class DataModel :  ViewModel() {
    val postIdMessage: MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }
}