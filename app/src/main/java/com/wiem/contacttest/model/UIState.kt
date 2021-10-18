package com.wiem.contacttest.model

import com.wiem.contacttest.data.entity.ContactEntity


sealed class UIState {
    class Success(val data: List<ContactEntity>) : UIState()
    class Failed(val message: String) : UIState()
    object Loading : UIState()
}