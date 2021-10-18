package com.wiem.contacttest.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wiem.contacttest.data.dao.ContactDao
import com.wiem.contacttest.data.entity.ContactEntity

@Database(
    entities = [ContactEntity::class],
    version = 1
)

abstract class ContactDatabase : RoomDatabase() {
    abstract val contactDao: ContactDao

    companion object {
        @Volatile
        private var instance: ContactDatabase? = null
        private const val DATABASE_NAME = "contact_db.db"

        operator fun invoke(context: Context) = instance ?: synchronized(this) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ContactDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}