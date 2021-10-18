package com.wiem.contacttest.view

import android.app.Application
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wiem.contacttest.data.repository.ContactRepository
import com.wiem.contacttest.model.UIState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ContactViewModel(
    application: Application,
    private val repository: ContactRepository,
) : AndroidViewModel(application) {

    var uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Loading)

    @FlowPreview
    fun init() {
        val contactCursor = createCursor()
        viewModelScope.launch { repository.getContactListData(contactCursor) }
        loadContact()
    }

    private fun createCursor() = getApplication<Application>().contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null,
        null,
        null,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
    )!!

    @FlowPreview
    fun loadContact(nameOrPhone: String? = null) = viewModelScope.launch {
        uiState.emit(UIState.Loading)
        if (nameOrPhone == null) {
            repository.getContacts().collectLatest { data ->
                if (data.isEmpty()) {
                    uiState.emit(UIState.Failed("Contact Not Found"))
                } else {
                    uiState.emit(UIState.Success(data))
                }
            }
        } else {
            repository.getContactByNameORPhone(nameOrPhone).collectLatest { data ->
                if (data.isEmpty()) {
                    uiState.emit(UIState.Failed("Contact Not Found"))
                } else {
                    uiState.emit(UIState.Success(data))
                }
            }
        }
    }
}
