package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeddingDetailsDao {
    @Query("SELECT * FROM wedding_details WHERE id = 1 LIMIT 1")
    fun getDetails(): Flow<WeddingDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(details: WeddingDetails)
}

@Dao
interface LocalVendorDao {
    @Query("SELECT * FROM vendors")
    fun getAllVendors(): Flow<List<LocalVendor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendors(vendors: List<LocalVendor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendor(vendor: LocalVendor)

    @Query("SELECT * FROM vendors WHERE id = :id LIMIT 1")
    fun getVendorById(id: Int): Flow<LocalVendor?>

    @Query("DELETE FROM vendors WHERE id = :id")
    suspend fun deleteVendor(id: Int)
}

@Dao
interface BookingRecordDao {
    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<BookingRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingRecord)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBooking(id: Int)

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Int, status: String)
}

@Dao
interface ExpenseRecordDao {
    @Query("SELECT * FROM expenses ORDER BY id DESC")
    fun getAllExpenses(): Flow<List<ExpenseRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseRecord)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpense(id: Int)
}

@Dao
interface ChecklistItemDao {
    @Query("SELECT * FROM checklist ORDER BY id ASC")
    fun getChecklist(): Flow<List<ChecklistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: ChecklistItem)

    @Query("UPDATE checklist SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletion(id: Int, isCompleted: Boolean)

    @Query("DELETE FROM checklist WHERE id = :id")
    suspend fun deleteChecklist(id: Int)
}

@Dao
interface LiveChatDao {
    @Query("SELECT * FROM chats ORDER BY timestamp ASC")
    fun getAllChats(): Flow<List<LiveChat>>

    @Query("SELECT * FROM chats WHERE vendorId = :vendorId ORDER BY timestamp ASC")
    fun getChatsForVendor(vendorId: Int): Flow<List<LiveChat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(chat: LiveChat)
}
