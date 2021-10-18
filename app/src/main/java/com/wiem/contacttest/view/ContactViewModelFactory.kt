package com.wiem.contacttest.view

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wiem.contacttest.data.repository.ContactRepository

class ContactViewModelFactory(
    private val application: Application,
    private val repository: ContactRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            return ContactViewModel(repository = repository, application = application) as T
        } else {
            throw IllegalArgumentException("Unable to create instance from ${modelClass::class.java.canonicalName}")
        }
    }
}