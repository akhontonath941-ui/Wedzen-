package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.gemini.GeminiApiClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = WeddingRepository(db)

    // User Roles / Panels & Session Simulator
    val userRole = MutableStateFlow("Customer") // "Customer", "Vendor", "Admin"
    val showAuthScreen = MutableStateFlow(true)
    val phoneAuthNumber = MutableStateFlow("")
    val isOtpSent = MutableStateFlow(false)
    val phoneOtpCode = MutableStateFlow("")
    val currentUserName = MutableStateFlow("Rohan")

    // Language Toggle
    val appLanguage = MutableStateFlow("English") // "English", "Hindi", "Assamese", "Bengali", "Tamil"

    // Theme Mode System (Simulated custom toggle)
    val isDarkMode = MutableStateFlow(false)

    // Data Flows from Repository
    val weddingDetails: StateFlow<WeddingDetails?> = repository.weddingDetails
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allVendors: StateFlow<List<LocalVendor>> = repository.allVendors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookings: StateFlow<List<BookingRecord>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseRecord>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val checklistItem: StateFlow<List<ChecklistItem>> = repository.checklistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChats: StateFlow<List<LiveChat>> = repository.allChats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // CUSTOM WEDDING MARKETPLACE CHECKOUT & SECURE PAYMENT STATES
    val checkoutStep = MutableStateFlow("Selection") // "Selection", "Overview", "RazorpaySim", "Receipt"
    val checkoutVendor = MutableStateFlow<LocalVendor?>(null)
    
    val selectedBookingPackage = MutableStateFlow("Premium Royal Deluxe") // "Standard Budget", "Premium Royal Deluxe", "Luxury Kohinoor Signature"
    val selectedBookingDate = MutableStateFlow("2026-11-28")
    val selectedTimeSlot = MutableStateFlow("Evening Celebration (4 PM - 11 PM)")
    val selectedGuestCount = MutableStateFlow(250)
    val selectedBookingLocation = MutableStateFlow("Udaipur, Rajasthan")
    
    val appliedCouponCode = MutableStateFlow("")
    val couponDiscountValue = MutableStateFlow(0.0) // Flat rupees
    val isFullPaymentMode = MutableStateFlow(false) // false means 40% advance payment, true means 100% full payment
    
    // Simulating specific Razorpay selections
    val selectedPaymentMethod = MutableStateFlow("UPI") // "UPI", "Card", "NetBanking", "Wallet", "EMI"
    val simulatedUpiId = MutableStateFlow("rohan@okaxis")
    val simulatedCardNo = MutableStateFlow("4321 5567 8901 2345")
    val simulatedCardExpiry = MutableStateFlow("12/29")
    val simulatedCardCvv = MutableStateFlow("123")
    val simulatedNetBankName = MutableStateFlow("State Bank of India (SBI)")
    val simulatedWalletName = MutableStateFlow("Paytm Wallet")
    val simulatedEmiMonths = MutableStateFlow(12)
    
    val simulatePaymentFailure = MutableStateFlow(false) // Toggle failures to test retry states and slot locking
    val paymentErrorMessage = MutableStateFlow("")
    
    // Live checkout count-down timer for locked slot
    val slotLockedCountDownSecs = MutableStateFlow(600) // 10 minutes = 600s
    val activeCheckoutBookingId = MutableStateFlow<Int?>(null)
    
    // Coupon configurations (Admin and User use cases)
    val availableCoupons = MutableStateFlow(mapOf(
        "SHUBHUTSAV" to 50000.0, // flat ₹50,000 off
        "WEDHAPPY10" to 0.10,    // 10% percentage discount
        "DILSE5" to 0.05         // 5% percentage discount
    ))
    
    // Admin configurations
    val platformCommissionRate = MutableStateFlow(10.0) // 10% platform commission default

    // Filtering & Searching Systems
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val selectedRegion = MutableStateFlow("All") // "All", "Assamese", "Punjabi", "Bengali", "South Indian", "Muslim"

    val filteredVendors: StateFlow<List<LocalVendor>> = combine(
        allVendors, searchQuery, selectedCategory, selectedRegion
    ) { vendors, query, cat, region ->
        vendors.filter { vendor ->
            val matchesQuery = vendor.name.contains(query, ignoreCase = true) ||
                    vendor.category.contains(query, ignoreCase = true) ||
                    vendor.description.contains(query, ignoreCase = true)
            val matchesCategory = cat == "All" || vendor.category.equals(cat, ignoreCase = true)
            val matchesRegion = region == "All" || vendor.regionalTemplate.equals(region, ignoreCase = true)
            matchesQuery && matchesCategory && matchesRegion
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Selected Dialog Vendor
    val selectedVendor = MutableStateFlow<LocalVendor?>(null)

    // Expense Manager Screen Inputs
    val expenseTitleInput = MutableStateFlow("")
    val expenseAmountInput = MutableStateFlow("")
    val expenseCategorySelected = MutableStateFlow("Venue")
    val expenseDateInput = MutableStateFlow("2026-05-22")

    // Checklist Input
    val checklistItemTitle = MutableStateFlow("")
    val checklistItemCategory = MutableStateFlow("General")

    // Selected Chat Detail
    val activeChatVendorId = MutableStateFlow<Int?>(null)
    val activeChatVendorName = MutableStateFlow("")
    val chatMessageInput = MutableStateFlow("")

    val activeVendorChats: StateFlow<List<LiveChat>> = combine(
        allChats, activeChatVendorId
    ) { chats, vendorId ->
        if (vendorId == null) emptyList()
        else chats.filter { it.vendorId == vendorId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Vendor Panel Live Input States
    val newVendorName = MutableStateFlow("")
    val newVendorCategory = MutableStateFlow("Catering")
    val newVendorPrice = MutableStateFlow("")
    val newVendorDescription = MutableStateFlow("")
    val newVendorRegion = MutableStateFlow("Universal")

    // AI suggestion response state
    val aiPlannerHistory = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            Pair("Namaste! I am your WedZen AI Planner. Ask me questions like:\n• 'Suggest catering menu for Assamese wedding'\n• 'How can I save ₹60,000 on decoration?'\n• 'Suggest bridal outfit combinations'", false)
        )
    )
    val aiMessageInput = MutableStateFlow("")
    val isAiLoading = MutableStateFlow(false)

    // Indian Gold Price Tracker State
    // Simulates live variations around average rate of ₹72,450 per 10 grams of 24k Gold
    val goldPriceRate = MutableStateFlow(72450.0)

    // EMI Calculator
    val emiMonthsSelected = MutableStateFlow(12) // months
    val emiApprovedAmount = MutableStateFlow(500000.0)

    init {
        viewModelScope.launch {
            // Initiate seeding
            repository.preseedDatabaseIfNeeded()
            // Randomly oscillate gold price on initiation to simulate live API values
            goldPriceRate.value = 72450.0 + (-400..350).random()
        }
    }

    // AUTH SIMULATION ACTIONS
    fun getOtp() {
        if (phoneAuthNumber.value.isNotEmpty()) {
            isOtpSent.value = true
        }
    }

    fun verifyOtpAndLogin(userName: String) {
        currentUserName.value = userName.ifEmpty { "Rohan" }
        showAuthScreen.value = false
        // Update wedding details names on success
        viewModelScope.launch {
            val d = weddingDetails.value ?: WeddingDetails()
            repository.saveWeddingDetails(d.copy(groomName = currentUserName.value))
        }
    }

    // WEDDING CONFIG ACTIONS
    fun updateWeddingDetails(bride: String, groom: String, date: String, location: String, budget: Double, type: String) {
        viewModelScope.launch {
            val existing = weddingDetails.value ?: WeddingDetails()
            repository.saveWeddingDetails(
                existing.copy(
                    brideName = bride,
                    groomName = groom,
                    weddingDate = date,
                    location = location,
                    totalBudget = budget,
                    weddingType = type
                )
            )
        }
    }

    fun addFamilyCollaborator(name: String) {
        viewModelScope.launch {
            val existing = weddingDetails.value ?: WeddingDetails()
            val list = existing.familyMembers
            val updatedList = if (list.isEmpty()) name else "$list, $name"
            repository.saveWeddingDetails(existing.copy(familyMembers = updatedList))
        }
    }

    // BOOKING INSTANT ACTIONS
    fun makeInstantBooking(vendor: LocalVendor, date: String) {
        viewModelScope.launch {
            repository.bookVendor(vendor, date)
            // Send instant success system notification/chat introduction
            repository.insertChatMessage(
                LiveChat(
                    vendorId = vendor.id,
                    vendorName = vendor.name,
                    message = "Congratulations Rohan and Ananya! Your booking query for ${vendor.name} has been placed. I am locking ${date} on my calendar. Talk here to organize details!",
                    isSentByCustomer = false,
                    timestamp = System.currentTimeMillis() + 500
                )
            )
        }
    }

    fun cancelActiveBooking(bookingId: Int) {
        viewModelScope.launch {
            repository.deleteBooking(bookingId)
        }
    }

    fun updateBookingRecordStatus(bookingId: Int, newStatus: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, newStatus)
        }
    }

    // EXPENSE SYSTEM ACTIONS
    fun addExpense() {
        val title = expenseTitleInput.value.trim()
        val amt = expenseAmountInput.value.toDoubleOrNull() ?: 0.0
        val cat = expenseCategorySelected.value
        val date = expenseDateInput.value

        if (title.isNotEmpty() && amt > 0.0) {
            viewModelScope.launch {
                repository.insertExpense(
                    ExpenseRecord(
                        title = title,
                        amount = amt,
                        category = cat,
                        date = date
                    )
                )
                // Clear input
                expenseTitleInput.value = ""
                expenseAmountInput.value = ""
            }
        }
    }

    fun deleteExpense(id: Int) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    // CHECKLIST ACTIONS
    fun toggleChecklistItem(id: Int, checked: Boolean) {
        viewModelScope.launch {
            repository.toggleChecklistItem(id, checked)
        }
    }

    fun addChecklistItem() {
        val title = checklistItemTitle.value.trim()
        val cat = checklistItemCategory.value
        if (title.isNotEmpty()) {
            viewModelScope.launch {
                repository.addChecklistItem(ChecklistItem(title = title, category = cat, isCompleted = false))
                checklistItemTitle.value = ""
            }
        }
    }

    fun removeChecklistItem(id: Int) {
        viewModelScope.launch {
            repository.deleteChecklistItem(id)
        }
    }

    // CHATS MODULE ACTIONS
    fun sendChatMessage() {
        val msg = chatMessageInput.value.trim()
        val vId = activeChatVendorId.value
        val vName = activeChatVendorName.value
        if (msg.isNotEmpty() && vId != null) {
            viewModelScope.launch {
                // Customer speaks
                repository.insertChatMessage(
                    LiveChat(
                        vendorId = vId,
                        vendorName = vName,
                        message = msg,
                        isSentByCustomer = true
                    )
                )
                chatMessageInput.value = ""

                // Automated Instant Replier (Simulated Vendor reply)
                repository.insertChatMessage(
                    LiveChat(
                        vendorId = vId,
                        vendorName = vName,
                        message = "Dhanyawad Rohan! I have received your message. Let's arrange a call to outline menu tasting and floral selections tomorrow.",
                        isSentByCustomer = false,
                        timestamp = System.currentTimeMillis() + 1500
                    )
                )
            }
        }
    }

    // VENDOR ACTIONS
    fun registerNewVendor() {
        val name = newVendorName.value.trim()
        val cat = newVendorCategory.value
        val priceVal = newVendorPrice.value.toDoubleOrNull() ?: 15000.0
        val desc = newVendorDescription.value.trim()
        val reg = newVendorRegion.value

        if (name.isNotEmpty()) {
            viewModelScope.launch {
                repository.insertVendor(
                    LocalVendor(
                        name = name,
                        category = cat,
                        price = priceVal,
                        rating = 5.0f,
                        location = "Local Area",
                        imageResName = "img_launcher_logo",
                        isFeatured = false,
                        regionalTemplate = reg,
                        description = desc
                    )
                )
                // Reset fields
                newVendorName.value = ""
                newVendorPrice.value = ""
                newVendorDescription.value = ""
                userRole.value = "Customer" // Go back
            }
        }
    }

    // AI WEDDING PLANNER INTELLIGENCE ACTIONS
    fun submitAiPlannerQuery() {
        val queryText = aiMessageInput.value.trim()
        if (queryText.isNotEmpty()) {
            // Append user message
            val currentList = aiPlannerHistory.value.toMutableList()
            currentList.add(Pair(queryText, true))
            aiPlannerHistory.value = currentList
            aiMessageInput.value = ""

            isAiLoading.value = true
            viewModelScope.launch {
                val aiResponse = GeminiApiClient.getWeddingAiSuggestions(queryText)
                isAiLoading.value = false

                val updatedList = aiPlannerHistory.value.toMutableList()
                updatedList.add(Pair(aiResponse, false))
                aiPlannerHistory.value = updatedList
            }
        }
    }

    // CALCULATED CHECKOUT BREAKDOWN
    fun getBasePriceForPackage(baseVendorPrice: Double, pkgName: String): Double {
        return when (pkgName) {
            "Standard Budget" -> baseVendorPrice * 0.75
            "Premium Royal Deluxe" -> baseVendorPrice
            "Luxury Kohinoor Signature" -> baseVendorPrice * 1.40
            else -> baseVendorPrice
        }
    }

    // Initialize the checkout process for a vendor
    fun startVendorCheckout(vendor: LocalVendor) {
        checkoutVendor.value = vendor
        selectedBookingPackage.value = "Premium Royal Deluxe"
        selectedBookingLocation.value = vendor.location
        appliedCouponCode.value = ""
        couponDiscountValue.value = 0.0
        isFullPaymentMode.value = false
        simulatePaymentFailure.value = false
        paymentErrorMessage.value = ""
        checkoutStep.value = "Overview"
        
        // Setup initial date from wedding details if possible
        weddingDetails.value?.let {
            selectedBookingDate.value = it.weddingDate
        }
    }

    // Apply coupon logic
    fun applyCheckoutCoupon(code: String): Boolean {
        val upperCode = code.trim().uppercase()
        val discount = availableCoupons.value[upperCode]
        return if (discount != null) {
            appliedCouponCode.value = upperCode
            // Calculate base price dynamically based on package
            val vendorPrice = checkoutVendor.value?.price ?: 0.0
            val basePrice = getBasePriceForPackage(vendorPrice, selectedBookingPackage.value)
            if (discount > 1.0) {
                // Flat discount
                couponDiscountValue.value = discount
            } else {
                // Percentage discount
                couponDiscountValue.value = basePrice * discount
            }
            true
        } else {
            false
        }
    }

    // Proceed from overview to payment simulation: Creates simulated Razorpay order + locks slot!
    fun proceedToPayment() {
        val vendor = checkoutVendor.value ?: return
        val priceMultiplier = when (selectedBookingPackage.value) {
            "Standard Budget" -> 0.75
            "Premium Royal Deluxe" -> 1.0
            "Luxury Kohinoor Signature" -> 1.40
            else -> 1.0
        }
        val basePrice = vendor.price * priceMultiplier
        val discount = couponDiscountValue.value
        val afterDiscount = (basePrice - discount).coerceAtLeast(0.0)
        val gst = afterDiscount * 0.18
        val totalAmount = afterDiscount + gst
        
        val advanceRequired = totalAmount * 0.40
        val isFullVal = isFullPaymentMode.value
        val amountToPayThisTime = if (isFullVal) totalAmount else advanceRequired

        viewModelScope.launch {
            // STEP 1 & 2: Temporary booking slot gets locked in local DB (slot expiration in 10 mins)
            val lockTimeMillis = System.currentTimeMillis() + 600000 // 10 minutes from now
            val invoiceNo = "WZ-LOCK-${(1000..9999).random()}"
            val rzpOrderId = "order_rzp_" + (100000..999999).random()

            val record = BookingRecord(
                vendorId = vendor.id,
                vendorName = vendor.name,
                vendorCategory = vendor.category,
                price = totalAmount, 
                bookingDate = selectedBookingDate.value,
                status = "Slot Locked", // Temporary status!
                invoiceNumber = invoiceNo,
                bookingPackage = selectedBookingPackage.value,
                timeSlot = selectedTimeSlot.value,
                guestCount = selectedGuestCount.value,
                location = selectedBookingLocation.value,
                isPartialPayment = !isFullVal,
                paidAmount = 0.0, // Not paid yet
                pendingAmount = totalAmount,
                gstAmount = gst,
                discountAmount = discount,
                razorpayOrderId = rzpOrderId,
                slotLockedUntilEpoch = lockTimeMillis
            )
            
            // Insert lock in database
            db.bookingRecordDao().insertBooking(record)
            
            // Retrieve latest generated record id
            val currentBookings = db.bookingRecordDao().getAllBookings().first()
            val recentInDB = currentBookings.firstOrNull { it.razorpayOrderId == rzpOrderId }
            activeCheckoutBookingId.value = recentInDB?.id

            // Set countdown timer
            slotLockedCountDownSecs.value = 600
            
            // Now proceed to custom Razorpay payment UI popup page!
            checkoutStep.value = "RazorpaySim"
        }
    }

    // Complete the payment flow with Razorpay Webhook Signatures, payouts, and confirmations
    fun simulateRazorpayPaymentSuccess() {
        val bookingId = activeCheckoutBookingId.value ?: return
        val vendor = checkoutVendor.value ?: return

        viewModelScope.launch {
            val bookingsList = db.bookingRecordDao().getAllBookings().first()
            val originalBooking = bookingsList.firstOrNull { it.id == bookingId } ?: return@launch
            
            // Double payments/double booking verify: Check if slot lock is still valid
            if (originalBooking.status != "Slot Locked" && originalBooking.status != "Pending") {
                paymentErrorMessage.value = "Transaction secure exception: Slot was already released or secured."
                return@launch
            }

            // Cryptographic Signatures Simulation:
            val generatedPaymentId = "pay_rzp_" + (100000000..999999999).random()
            
            // Split percentages for payouts
            val totalCharged = originalBooking.price
            val commissionPercent = platformCommissionRate.value / 100.0
            val adminCommission = totalCharged * commissionPercent
            val vendorPayoutAmt = totalCharged * (1.0 - commissionPercent)

            val finalPaidAmount = if (originalBooking.isPartialPayment) originalBooking.price * 0.40 else originalBooking.price
            val finalPendingAmount = originalBooking.price - finalPaidAmount

            val invoiceNo = "WZ-2026-${(1000..9999).random()}"

            // Confirm booking in Database permanently:
            val confirmedBooking = originalBooking.copy(
                status = "Confirmed",
                paidAmount = finalPaidAmount,
                pendingAmount = finalPendingAmount,
                razorpayPaymentId = generatedPaymentId,
                slotLockedUntilEpoch = 0L, // Slot permanently claimed
                invoiceNumber = invoiceNo,
                commissionPaid = adminCommission,
                vendorPayout = vendorPayoutAmt
            )

            db.bookingRecordDao().insertBooking(confirmedBooking)

            // Automate Financial budget ledger tracking
            db.expenseRecordDao().insertExpense(
                ExpenseRecord(
                    title = "Booking Paid for ${vendor.name} (${confirmedBooking.bookingPackage})",
                    amount = finalPaidAmount,
                    category = vendor.category,
                    date = confirmedBooking.bookingDate
                )
            )

            // Vendor notify + user notify via chat system
            db.liveChatDao().insertChatMessage(
                LiveChat(
                    vendorId = vendor.id,
                    vendorName = vendor.name,
                    message = "✨ BOOKING SECURED! Your booking of ${confirmedBooking.bookingPackage} on ${confirmedBooking.bookingDate} (${confirmedBooking.timeSlot}) is CONFIRMED with Razorpay ID: ${generatedPaymentId}. Advance collected: ₹${finalPaidAmount} (Pending: ₹${finalPendingAmount}). Platform commission: ₹${adminCommission}.",
                    isSentByCustomer = false,
                    timestamp = System.currentTimeMillis()
                )
            )

            checkoutStep.value = "Receipt"
        }
    }

    // Simulates payment failures or expirations
    fun simulatePaymentFailure(reason: String) {
        val bookingId = activeCheckoutBookingId.value ?: return
        viewModelScope.launch {
            val bookingsList = db.bookingRecordDao().getAllBookings().first()
            val originalBooking = bookingsList.firstOrNull { it.id == bookingId } ?: return@launch

            // Released locked slot - mark as Cancelled/Failed in DB
            val failedBooking = originalBooking.copy(
                status = "Cancelled",
                slotLockedUntilEpoch = 0L, // Released
                rejectionReason = "Payment failed: $reason"
            )
            db.bookingRecordDao().insertBooking(failedBooking)
            
            paymentErrorMessage.value = "Razorpay Transaction Error ($reason). Please try again. Your slot has been unsaved."
            simulatePaymentFailure.value = true
        }
    }

    // Cancellation request system with partial refund and advance rules
    fun requestBookingCancelAndRefund(bookingId: Int, reason: String) {
        viewModelScope.launch {
            val bookingsList = db.bookingRecordDao().getAllBookings().first()
            val originalBooking = bookingsList.firstOrNull { it.id == bookingId } ?: return@launch
            
            val totalAdvancePaid = originalBooking.paidAmount
            val cancelFeeRate = 0.10 // 10% Cancellation fee of paid amount
            val refundEst = totalAdvancePaid * (1.0 - cancelFeeRate)

            val updatedBooking = originalBooking.copy(
                status = "Cancelled",
                refundRequested = true,
                refundAmount = refundEst,
                refundStatus = "Requested",
                rejectionReason = "Cancelled by Client due to: $reason"
            )
            db.bookingRecordDao().insertBooking(updatedBooking)

            // Add notification response in chat
            db.liveChatDao().insertChatMessage(
                LiveChat(
                    vendorId = originalBooking.vendorId,
                    vendorName = originalBooking.vendorName,
                    message = "⚠️ Client has initiated a cancellation and requested a refund of ₹${refundEst} (10% standard cancellation fee deducted: ₹${totalAdvancePaid * cancelFeeRate}). Admin team is currently verifying the payout reversion.",
                    isSentByCustomer = true,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Admin Workflow: Approve refund
    fun adminApproveRefund(bookingId: Int) {
        viewModelScope.launch {
            val bookingsList = db.bookingRecordDao().getAllBookings().first()
            val b = bookingsList.firstOrNull { it.id == bookingId } ?: return@launch
            
            val approvedRefundBooking = b.copy(
                status = "Refunded",
                refundStatus = "Approved"
            )
            db.bookingRecordDao().insertBooking(approvedRefundBooking)

            // Log corresponding reverse ledger expense item
            db.expenseRecordDao().insertExpense(
                ExpenseRecord(
                    title = "REFUND CREDIT: ${b.vendorName} Cancelled",
                    amount = -b.refundAmount, // negative means refund inflow
                    category = b.vendorCategory,
                    date = b.bookingDate
                )
            )

            db.liveChatDao().insertChatMessage(
                LiveChat(
                    vendorId = b.vendorId,
                    vendorName = b.vendorName,
                    message = "✅ Refund of ₹${b.refundAmount} has been APPROVED by WedZen Admin. Funds will be returned to your Razorpay account source within 3-5 business days.",
                    isSentByCustomer = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Admin Workflow: Reject refund
    fun adminRejectRefund(bookingId: Int) {
        viewModelScope.launch {
            val bookingsList = db.bookingRecordDao().getAllBookings().first()
            val b = bookingsList.firstOrNull { it.id == bookingId } ?: return@launch
            
            val rejectedRefundBooking = b.copy(
                refundStatus = "Rejected"
            )
            db.bookingRecordDao().insertBooking(rejectedRefundBooking)

            db.liveChatDao().insertChatMessage(
                LiveChat(
                    vendorId = b.vendorId,
                    vendorName = b.vendorName,
                    message = "❌ Refund dispute request was REJECTED by Admin. According to advance booking policy terms, deposits for weddings within 60 days are non-refundable.",
                    isSentByCustomer = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Customer clearing remaining due balance
    fun payRemainingBalance(bookingId: Int) {
        viewModelScope.launch {
            val bookingsList = db.bookingRecordDao().getAllBookings().first()
            val b = bookingsList.firstOrNull { it.id == bookingId } ?: return@launch
            
            val updated = b.copy(
                isPartialPayment = false,
                paidAmount = b.price,
                pendingAmount = 0.0
            )
            db.bookingRecordDao().insertBooking(updated)

            db.liveChatDao().insertChatMessage(
                LiveChat(
                    vendorId = b.vendorId,
                    vendorName = b.vendorName,
                    message = "💰 BALANCE FULLY SETTLED! Client has cleared the final remaining 60% balance (₹${String.format("%,.0f", b.price * 0.60)}) securely through Razorpay. Booking is now marked as FULLY PAID.",
                    isSentByCustomer = true,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Vendor: Accept customer vows booking
    fun confirmBooking(bookingId: Int) {
        viewModelScope.launch {
            val bookingsList = db.bookingRecordDao().getAllBookings().first()
            val b = bookingsList.firstOrNull { it.id == bookingId } ?: return@launch
            val updated = b.copy(status = "Confirmed", paidAmount = b.price * 0.40, pendingAmount = b.price * 0.60)
            db.bookingRecordDao().insertBooking(updated)
            db.liveChatDao().insertChatMessage(
                LiveChat(
                    vendorId = b.vendorId,
                    vendorName = b.vendorName,
                    message = "🎉 Vows accepted! Seller has approved your booking contract schedules, locking priority calendars permanent. See you on ${b.bookingDate}.",
                    isSentByCustomer = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Vendor: Reject booking due to schedule conflicts, trigger automatic advance refund
    fun vendorRejectBooking(bookingId: Int) {
        viewModelScope.launch {
            val bookingsList = db.bookingRecordDao().getAllBookings().first()
            val b = bookingsList.firstOrNull { it.id == bookingId } ?: return@launch
            val updated = b.copy(
                status = "Cancelled",
                refundRequested = true,
                refundAmount = b.paidAmount,
                refundStatus = "Requested",
                rejectionReason = "Service unavailable on chosen date (Vendor Rejected)"
            )
            db.bookingRecordDao().insertBooking(updated)
            db.liveChatDao().insertChatMessage(
                LiveChat(
                    vendorId = b.vendorId,
                    vendorName = b.vendorName,
                    message = "⚠️ Vendor Rejected Booking: Studio has initiated an automatic 100% full refund of ₹${String.format("%,.0f", b.paidAmount)} due to sudden calendar conflicts. Admin has been scheduled to disburse the funds back to your source account.",
                    isSentByCustomer = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Vendor / Admin: change vendor availability custom slots
    val vendorAvailabilitySlots = MutableStateFlow(mapOf(
        "Morning (9 AM - 2 PM)" to true,
        "Afternoon (2 PM - 7 PM)" to true,
        "Evening Celebration (4 PM - 11 PM)" to true,
        "Full Day (10 AM - 10 PM)" to true
    ))

    fun updateVendorSlotAvailability(slot: String, isAvailable: Boolean) {
        val updatedMap = vendorAvailabilitySlots.value.toMutableMap()
        updatedMap[slot] = isAvailable
        vendorAvailabilitySlots.value = updatedMap
    }

    // Super-Admin: Update platform dynamic commission rate percentage
    fun updatePlatformCommissionRate(rate: Double) {
        platformCommissionRate.value = rate
    }

    // Super-Admin: Toggle vendor isFeatured flag
    fun toggleVendorFeatured(vendorId: Int, isFeatured: Boolean) {
        viewModelScope.launch {
            val vendorsList = db.localVendorDao().getAllVendors().first()
            val v = vendorsList.firstOrNull { it.id == vendorId } ?: return@launch
            val updated = v.copy(isFeatured = isFeatured)
            db.localVendorDao().insertVendor(updated)
        }
    }

    // Super-Admin: Terminate and delete vendor listing entirely
    fun deleteVendorListing(vendorId: Int) {
        viewModelScope.launch {
            db.localVendorDao().deleteVendor(vendorId)
        }
    }

    // Super-Admin: Add custom promotional coupon code
    fun addCoupon(code: String, discount: Double) {
        val updatedMap = availableCoupons.value.toMutableMap()
        updatedMap[code.trim().uppercase()] = discount
        availableCoupons.value = updatedMap
    }

    // Super-Admin: Revoke promotional coupon code
    fun removeCoupon(code: String) {
        val updatedMap = availableCoupons.value.toMutableMap()
        updatedMap.remove(code.trim().uppercase())
        availableCoupons.value = updatedMap
    }

    // Super-Admin: Force override state of booking record
    fun adminForceUpdateBookingStatus(bookingId: Int, newStatus: String) {
        viewModelScope.launch {
            val bookingsList = db.bookingRecordDao().getAllBookings().first()
            val b = bookingsList.firstOrNull { it.id == bookingId } ?: return@launch
            val updated = when (newStatus) {
                "Fully Paid" -> b.copy(status = "Fully Paid", paidAmount = b.price, pendingAmount = 0.0)
                "Confirmed" -> b.copy(status = "Confirmed", paidAmount = b.price * 0.40, pendingAmount = b.price * 0.60)
                "Cancelled" -> b.copy(status = "Cancelled", pendingAmount = b.price, refundRequested = false, refundStatus = "N/A")
                "Refunded" -> b.copy(status = "Refunded", refundStatus = "Approved", paidAmount = 0.0, pendingAmount = 0.0)
                else -> b.copy(status = newStatus)
            }
            db.bookingRecordDao().insertBooking(updated)
            
            db.liveChatDao().insertChatMessage(
                LiveChat(
                    vendorId = b.vendorId,
                    vendorName = b.vendorName,
                    message = "⚙️ Super-Admin Override: Booking status was forced to '$newStatus' by super-admin direct command.",
                    isSentByCustomer = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Super-Admin: Delete booking record completely from platform database
    fun adminDeleteBookingRecord(bookingId: Int) {
        viewModelScope.launch {
            repository.deleteBooking(bookingId)
        }
    }
}
