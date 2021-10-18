package com.wiem.contacttest.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Contact")
@Parcelize
data class ContactEntity(
    @PrimaryKey
    var id: Int,
    var name: String? = null,
    var phone: String? = null
) : Parcelable
