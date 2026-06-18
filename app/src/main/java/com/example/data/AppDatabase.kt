package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WeddingDetails::class,
        LocalVendor::class,
        BookingRecord::class,
        ExpenseRecord::class,
        ChecklistItem::class,
        LiveChat::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weddingDetailsDao(): WeddingDetailsDao
    abstract fun localVendorDao(): LocalVendorDao
    abstract fun bookingRecordDao(): BookingRecordDao
    abstract fun expenseRecordDao(): ExpenseRecordDao
    abstract fun checklistItemDao(): ChecklistItemDao
    abstract fun liveChatDao(): LiveChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wedzen_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
