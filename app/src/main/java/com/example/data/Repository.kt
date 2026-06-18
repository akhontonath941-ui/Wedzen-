package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class WeddingRepository(private val db: AppDatabase) {

    val weddingDetails: Flow<WeddingDetails?> = db.weddingDetailsDao().getDetails()
    val allVendors: Flow<List<LocalVendor>> = db.localVendorDao().getAllVendors()
    val allBookings: Flow<List<BookingRecord>> = db.bookingRecordDao().getAllBookings()
    val allExpenses: Flow<List<ExpenseRecord>> = db.expenseRecordDao().getAllExpenses()
    val checklistItems: Flow<List<ChecklistItem>> = db.checklistItemDao().getChecklist()
    val allChats: Flow<List<LiveChat>> = db.liveChatDao().getAllChats()

    // Database Actions
    suspend fun saveWeddingDetails(details: WeddingDetails) = withContext(Dispatchers.IO) {
        db.weddingDetailsDao().insertOrUpdate(details)
    }

    suspend fun insertVendor(vendor: LocalVendor) = withContext(Dispatchers.IO) {
        db.localVendorDao().insertVendor(vendor)
    }

    fun getVendorById(id: Int): Flow<LocalVendor?> {
        return db.localVendorDao().getVendorById(id)
    }

    suspend fun bookVendor(vendor: LocalVendor, date: String, customPrice: Double = vendor.price) = withContext(Dispatchers.IO) {
        // Add booking
        val invoiceNo = "WZ-2026-${(1000..9999).random()}"
        val record = BookingRecord(
            vendorId = vendor.id,
            vendorName = vendor.name,
            vendorCategory = vendor.category,
            price = customPrice,
            bookingDate = date,
            status = "Pending",
            invoiceNumber = invoiceNo
        )
        db.bookingRecordDao().insertBooking(record)

        // Also automatically add an expense entry as pending/committed
        val expense = ExpenseRecord(
            title = "Deposit for ${vendor.name} (${vendor.category})",
            amount = customPrice * 0.40, // standard 40% initial deposit
            category = vendor.category,
            date = date
        )
        db.expenseRecordDao().insertExpense(expense)
    }

    suspend fun deleteBooking(bookingId: Int) = withContext(Dispatchers.IO) {
        db.bookingRecordDao().deleteBooking(bookingId)
    }

    suspend fun updateBookingStatus(bookingId: Int, status: String) = withContext(Dispatchers.IO) {
        db.bookingRecordDao().updateBookingStatus(bookingId, status)
    }

    suspend fun insertExpense(expense: ExpenseRecord) = withContext(Dispatchers.IO) {
        db.expenseRecordDao().insertExpense(expense)
    }

    suspend fun deleteExpense(id: Int) = withContext(Dispatchers.IO) {
        db.expenseRecordDao().deleteExpense(id)
    }

    suspend fun toggleChecklistItem(id: Int, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        db.checklistItemDao().updateCompletion(id, isCompleted)
    }

    suspend fun addChecklistItem(item: ChecklistItem) = withContext(Dispatchers.IO) {
        db.checklistItemDao().insertChecklistItem(item)
    }

    suspend fun deleteChecklistItem(id: Int) = withContext(Dispatchers.IO) {
        db.checklistItemDao().deleteChecklist(id)
    }

    fun getChatsForVendor(vendorId: Int): Flow<List<LiveChat>> {
        return db.liveChatDao().getChatsForVendor(vendorId)
    }

    suspend fun insertChatMessage(chat: LiveChat) = withContext(Dispatchers.IO) {
        db.liveChatDao().insertChatMessage(chat)
    }

    // Seed the local database with realistic Demo Data if it’s currently empty
    suspend fun preseedDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        val existingDetails = db.weddingDetailsDao().getDetails().firstOrNull()
        if (existingDetails == null) {
            // Seed Wedding Details
            db.weddingDetailsDao().insertOrUpdate(
                WeddingDetails(
                    brideName = "Ananya",
                    groomName = "Rohan",
                    weddingDate = "2026-11-28",
                    location = "Udaipur, Rajasthan",
                    totalBudget = 1500000.0,
                    weddingType = "Hindu"
                )
            )

            // Seed Vendors List
            val seedVendors = listOf(
                LocalVendor(
                    name = "The Heritage Palace Resort",
                    category = "Venue",
                    price = 450000.0,
                    rating = 4.9f,
                    location = "Udaipur, Rajasthan",
                    imageResName = "img_decor_venue",
                    isFeatured = true,
                    regionalTemplate = "Universal",
                    description = "A gorgeous Rajasthani heritage castle setup overlooking private lakes. Includes grand banquet spaces, courtyards, and luxury suites for relatives.",
                    reviewsCount = 142
                ),
                LocalVendor(
                    name = "Laxmi Tent & Floral Artistry",
                    category = "Decoration",
                    price = 180000.0,
                    rating = 4.7f,
                    location = "Mumbai, Maharashtra",
                    imageResName = "img_decor_venue",
                    isFeatured = true,
                    regionalTemplate = "Universal",
                    description = "Specialists in marigold cascades, traditional royal mandaps, and modern theme setups with glowing fairytale fairy lights.",
                    reviewsCount = 88
                ),
                LocalVendor(
                    name = "Royal Rajput Catering Services",
                    category = "Catering",
                    price = 320000.0,
                    rating = 4.8f,
                    location = "Jaipur, Rajasthan",
                    imageResName = "img_catering_food",
                    isFeatured = true,
                    regionalTemplate = "Universal",
                    description = "Gourmet Live Counters. Offers delicious Dal-Baati-Churma, traditional rich Biryanis, butter paneer, Shahi Tukda, and organic fruit mocktails.",
                    reviewsCount = 110
                ),
                LocalVendor(
                    name = "Zoya Bridal Makeup & Glow Salon",
                    category = "Makeup",
                    price = 35000.0,
                    rating = 4.9f,
                    location = "Delhi NCR",
                    imageResName = "img_makeup_artist",
                    isFeatured = true,
                    regionalTemplate = "Universal",
                    description = "Immaculate bridal makeup. Specializes in luxury HD and Airbrush cosmetics, matching lehenga shades with rich heritage glows.",
                    reviewsCount = 65
                ),
                LocalVendor(
                    name = "Kangana Banarasi & Muga Silk Outfitters",
                    category = "Traditional",
                    price = 65000.0,
                    rating = 4.9f,
                    location = "Guwahati, Assam",
                    imageResName = "img_makeup_artist",
                    isFeatured = false,
                    regionalTemplate = "Assamese",
                    description = "Authentic elegant Handloom Muga Silk Mekhela Sador with traditional golden thread (guna) bridal patterns.",
                    reviewsCount = 31
                ),
                LocalVendor(
                    name = "Golden Lens Cinematic Films",
                    category = "Photographer",
                    price = 110000.0,
                    rating = 4.8f,
                    location = "Chandigarh, Punjab",
                    imageResName = "img_home_hero",
                    isFeatured = true,
                    regionalTemplate = "Universal",
                    description = "Specialists in emotional wedding reels, drone videography, candid expressions, and large family portraits.",
                    reviewsCount = 94
                ),
                LocalVendor(
                    name = "Shree Vinayak Sanskriti Pandits",
                    category = "Priest",
                    price = 21000.0,
                    rating = 4.9f,
                    location = "Varanasi, UP",
                    imageResName = "img_launcher_logo",
                    isFeatured = false,
                    regionalTemplate = "Universal",
                    description = "Experienced priests reciting highly classical Vedic mantras for traditional Hindu Saat Pheras, explain meanings of wedding vows.",
                    reviewsCount = 74
                ),
                LocalVendor(
                    name = "Qazi Sahab Nikah Registrars",
                    category = "Priest",
                    price = 18000.0,
                    rating = 4.8f,
                    location = "Lucknow, UP",
                    imageResName = "img_launcher_logo",
                    isFeatured = false,
                    regionalTemplate = "Muslim",
                    description = "Facilitates beautiful, certified Nikah rituals, preparing verified offline & online marriage contracts (An-Nikah).",
                    reviewsCount = 42
                ),
                LocalVendor(
                    name = "Punjabi Dhol beats & DJ Bunty",
                    category = "DJ & Music",
                    price = 45000.0,
                    rating = 4.6f,
                    location = "Delhi NCR",
                    imageResName = "img_home_hero",
                    isFeatured = false,
                    regionalTemplate = "Punjabi",
                    description = "Loud authentic brass dhol players paired with high-voltage DJ sound systems, custom light shows, and dancefloor support.",
                    reviewsCount = 57
                ),
                LocalVendor(
                    name = "Subho Nababarsho Catering",
                    category = "Catering",
                    price = 220000.0,
                    rating = 4.7f,
                    location = "Kolkata, West Bengal",
                    imageResName = "img_catering_food",
                    isFeatured = false,
                    regionalTemplate = "Bengali",
                    description = "Premium traditional Bengali wedding catering, offering melt-in-the-mouth Bhetki Paturi, Chingri Malaikari, and authentic Rossogollas.",
                    reviewsCount = 49
                ),
                LocalVendor(
                    name = "Nadaswaram Heritage Wedding Instrumental",
                    category = "Traditional",
                    price = 25000.0,
                    rating = 4.9f,
                    location = "Chennai, Tamil Nadu",
                    imageResName = "img_launcher_logo",
                    isFeatured = false,
                    regionalTemplate = "South Indian",
                    description = "Traditional Carnatic Nadaswaram and Thavil instrumentalists creating an auspicious, high-fidelity atmosphere for early morning muhurthams.",
                    reviewsCount = 38
                ),
                LocalVendor(
                    name = "Zeenat Designer Mehendi Arts",
                    category = "Mehendi",
                    price = 12000.0,
                    rating = 4.8f,
                    location = "Hyderabad, Telangana",
                    imageResName = "img_makeup_artist",
                    isFeatured = false,
                    regionalTemplate = "Universal",
                    description = "Exquisite Arabic and Rajasthani full-hand mehendi, secret dark-stain natural henna, bridal story silhouettes.",
                    reviewsCount = 82
                )
            )
            db.localVendorDao().insertVendors(seedVendors)

            // Seed initial Checklist Items
            val initialChecklist = listOf(
                ChecklistItem(title = "Finalize Bride & Groom Outfits styling", category = "Traditional", isCompleted = false),
                ChecklistItem(title = "Select & Book Wedding Hall Venue", category = "Venue", isCompleted = true),
                ChecklistItem(title = "Approve Menu with Local Caterers", category = "Catering", isCompleted = false),
                ChecklistItem(title = "Finalize Floral Decor theme with planner", category = "Decoration", isCompleted = false),
                ChecklistItem(title = "Register with Pujari/Qazi/Pastor", category = "Priest", isCompleted = false),
                ChecklistItem(title = "Design & dispatch digital invitation cards", category = "Universal", isCompleted = true)
            )
            initialChecklist.forEach { db.checklistItemDao().insertChecklistItem(it) }

            // Seed initial Demo Expenses
            val initialExpenses = listOf(
                ExpenseRecord(title = "Venue advanced booking deposit", amount = 180000.0, category = "Venue", date = "2026-05-10"),
                ExpenseRecord(title = "Digital e-cards reservation", amount = 8500.0, category = "Catering", date = "2026-05-15")
            )
            initialExpenses.forEach { db.expenseRecordDao().insertExpense(it) }

            // Seed initial Live Chat messages
            val initialChats = listOf(
                LiveChat(vendorId = 1, vendorName = "The Heritage Palace Resort", message = "Hello Rohan and Ananya! Thank you for interest in our heritage palace. For your date in late November, the main courtyard is currently vacant. Would you like a live video tour?", isSentByCustomer = false)
            )
            initialChats.forEach { db.liveChatDao().insertChatMessage(it) }
        }
    }
}
