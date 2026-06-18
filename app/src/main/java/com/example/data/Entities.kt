package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wedding_details")
data class WeddingDetails(
    @PrimaryKey val id: Int = 1,
    val brideName: String = "Ananya",
    val groomName: String = "Rohan",
    val weddingDate: String = "2026-11-28",
    val location: String = "Udaipur, Rajasthan",
    val totalBudget: Double = 1500000.0,
    val weddingType: String = "Hindu", // "Hindu", "Muslim", "Assamese", "Bengali", "Punjabi", "South Indian", "Christian"
    val familyMembers: String = "Mom, Dad, Sister, Brother-in-law" // Comma-separated names
)

@Entity(tableName = "vendors")
data class LocalVendor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String, // "Catering", "Decoration", "Venue", "Makeup", "Photographer", "Mehendi", "DJ & Music", "Traditional", "Priest"
    val price: Double,
    val rating: Float,
    val location: String,
    val imageResName: String, // String mapping to generated drawable resource or system icons
    val isFeatured: Boolean = false,
    val regionalTemplate: String = "Universal", // "Assamese", "Punjabi", "Bengali", "South Indian", "Universal"
    val description: String = "",
    val reviewsCount: Int = 24
)

@Entity(tableName = "bookings")
data class BookingRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val vendorId: Int,
    val vendorName: String,
    val vendorCategory: String,
    val price: Double,
    val bookingDate: String,
    val status: String = "Pending", // "Pending", "Confirmed", "In Progress", "Completed", "Cancelled", "Refunded"
    val invoiceNumber: String = "WZ-2026-0000",
    
    // Added for custom payment + slot lock systems
    val bookingPackage: String = "Standard Package",
    val timeSlot: String = "Morning (9 AM - 2 PM)",
    val guestCount: Int = 150,
    val location: String = "Udaipur, Rajasthan",
    val isPartialPayment: Boolean = false,
    val paidAmount: Double = 0.0,
    val pendingAmount: Double = 0.0,
    val gstAmount: Double = 0.0,
    val discountAmount: Double = 0.0,
    val razorpayOrderId: String = "",
    val razorpayPaymentId: String = "",
    val slotLockedUntilEpoch: Long = 0L, // 0 means not locked, positive is millisecond expiration
    val refundRequested: Boolean = false,
    val refundAmount: Double = 0.0,
    val refundStatus: String = "None", // "None", "Requested", "Approved", "Rejected"
    val commissionPaid: Double = 0.0, // Platform commission (10%)
    val vendorPayout: Double = 0.0, // Vendor share (90%)
    val rejectionReason: String = ""
)

@Entity(tableName = "expenses")
data class ExpenseRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val uploadedBillPath: String? = null
)

@Entity(tableName = "checklist")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "chats")
data class LiveChat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val vendorId: Int,
    val vendorName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSentByCustomer: Boolean = true
)
