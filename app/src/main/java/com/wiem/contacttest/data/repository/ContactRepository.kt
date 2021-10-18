package com.wiem.contacttest.data.repository

import android.database.Cursor
import android.provider.ContactsContract
import com.wiem.contacttest.data.database.ContactDatabase
import com.wiem.contacttest.data.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

class ContactRepository(private val db: ContactDatabase) {

    private suspend fun insertListContact(contacts: List<ContactEntity>) =
        db.contactDao.insertListContact(contacts)

    fun getContactByNameORPhone(nameOrPhone: String): Flow<List<ContactEntity>> =
        db.contactDao.getContactByNameORPhone(nameOrPhone)

    fun getContacts(): Flow<List<ContactEntity>> =
        db.contactDao.getContacts()

    suspend fun getContactListData(contactCursor: Cursor) {
        val itemArrayList: MutableList<ContactEntity> = mutableListOf()
        if (contactCursor.count > 0) {
            var index = 1
            while (contactCursor.moveToNext()) {

                val name: String = contactCursor.getString(
                    contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                )

                val phoneNumber: String = contactCursor.getString(
                    contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )

                val contact = ContactEntity(
                    id = index,
                    name = name,
                    phone = phoneNumber.replace(" ", "")
                )

                itemArrayList.add(contact)
                index++
            }
            insertListContact(itemArrayList)
        }
    }

}