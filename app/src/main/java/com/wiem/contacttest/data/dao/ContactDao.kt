package com.wiem.contacttest.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wiem.contacttest.data.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListContact(contacts: List<ContactEntity>)

    @Query("SELECT * FROM Contact WHERE  name  LIKE  '%'|| :nameOrPhone || '%' OR phone LIKE '%'|| :nameOrPhone || '%' order By name ASC")
    fun getContactByNameORPhone(
        nameOrPhone: String
    ): Flow<List<ContactEntity>>

    @Query("SELECT * FROM Contact order By name ASC")
    fun getContacts(): Flow<List<ContactEntity>>

}