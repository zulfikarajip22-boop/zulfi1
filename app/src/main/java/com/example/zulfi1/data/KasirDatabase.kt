package com.example.uas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [KasirEntity::class], version = 1)
abstract class KasirDatabase : RoomDatabase() {

    abstract fun kasirDao(): KasirDao

    companion object {
        @Volatile
        private var INSTANCE: KasirDatabase? = null

        fun getInstance(context: Context): KasirDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KasirDatabase::class.java,
                    "kasir_db"
                ).build().also { INSTANCE = it }
            }
    }
}
