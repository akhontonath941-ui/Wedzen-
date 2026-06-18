package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.data.BookingRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelsScreen(viewModel: MainViewModel) {
    val activeRole by viewModel.userRole.collectAsState()
    val username by viewModel.currentUserName.collectAsState()
    val language by viewModel.appLanguage.collectAsState()
    val darkThemeEnabled by viewModel.isDarkMode.collectAsState()

    var showingInvoiceBooking by remember { mutableStateOf<BookingRecord?>(null) }
    var showingCancelRefundBooking by remember { mutableStateOf<BookingRecord?>(null) }
    var cancelReasonInput by remember { mutableStateOf("") }

    // Super-Admin override local states
    var newCouponCode by remember { mutableStateOf("") }
    var newCouponDiscountStr by remember { mutableStateOf("") }
    var customCommissionStr by remember { mutableStateOf("") }

    // Checklist variables
    val checklist by viewModel.checklistItem.collectAsState()
    val listInput by viewModel.checklistItemTitle.collectAsState()
    val listCatInput by viewModel.checklistItemCategory.collectAsState()

    // Vendor inputs
    val vendorNameInput by viewModel.newVendorName.collectAsState()
    val vendorPriceInput by viewModel.newVendorPrice.collectAsState()
    val vendorDescInput by viewModel.newVendorDescription.collectAsState()
    val vendorCatInput by viewModel.newVendorCategory.collectAsState()
    val vendorRegInput by viewModel.newVendorRegion.collectAsState()

    // Bookings log
    val bookings by viewModel.bookings.collectAsState()
    val vendors by viewModel.allVendors.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Grand header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "Auspicious Panel Center",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp
                )
                Text(
                    text = "Manage multi-stakeholder roles, profile properties, and checklist",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        // Segmented User Role toggler selection
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Select Active Administrative Dashboard",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val roles = listOf("Customer", "Vendor", "Admin")
                    roles.forEach { role ->
                        val isSelected = activeRole == role
                        OutlinedButton(
                            onClick = { viewModel.userRole.value = role },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .padding(horizontal = 2.dp)
                                .testTag("role_btn_$role")
                        ) {
                            Text(
                                text = role,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // RENDER SCREEN CURRENT INTERFACE CONDITIONALLY
        when (activeRole) {
            "Customer" -> {
                // Profile & Language Setting Cards
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Customer Profile Properties", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Username display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Auspicious Client Auditor Name", fontSize = 12.sp, color = Color.Gray)
                            Text(username, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        // Language Selector dropdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Set App Language (Multi-Lingual)", fontSize = 12.sp, color = Color.Gray)

                            var showLangDropdown by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { showLangDropdown = true },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text(language, fontSize = 11.sp)
                                }

                                DropdownMenu(
                                    expanded = showLangDropdown,
                                    onDismissRequest = { showLangDropdown = false }
                                ) {
                                    val langs = listOf("English", "Hindi", "Assamese", "Bengali", "Tamil")
                                    langs.forEach { l ->
                                        DropdownMenuItem(
                                            text = { Text(l) },
                                            onClick = {
                                                viewModel.appLanguage.value = l
                                                showLangDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        // Theme switch simulated
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Auspicious Midnight Theme (Dark)", fontSize = 12.sp, color = Color.Gray)
                            Switch(
                                checked = darkThemeEnabled,
                                onCheckedChange = { viewModel.isDarkMode.value = it }
                            )
                        }
                    }
                }

                // Customer checklist priority manager
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Auspicious Wedding Checklist Organiser", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Add and audit traditional ritual targets manually:", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Text Field Input
                        OutlinedTextField(
                            value = listInput,
                            onValueChange = { viewModel.checklistItemTitle.value = it },
                            placeholder = { Text("e.g. Gather puja materials for priest") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checklist_title_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Dropdown choice
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Checklist Category:", fontSize = 12.sp, color = Color.Gray)

                            var isCatDrop by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { isCatDrop = true },
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(listCatInput, fontSize = 11.sp)
                                }
                                DropdownMenu(expanded = isCatDrop, onDismissRequest = { isCatDrop = false }) {
                                    val cats = listOf("General", "Catering", "Traditional", "Priest", "Decorations")
                                    cats.forEach { c ->
                                        DropdownMenuItem(
                                            text = { Text(c) },
                                            onClick = {
                                                viewModel.checklistItemCategory.value = c
                                                isCatDrop = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.addChecklistItem() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("checklist_submit_btn")
                        ) {
                            Text("Add Custom Task", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Checklist logs
                        Text(text = "Current Milestones (${checklist.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(6.dp))

                        checklist.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Checkbox(
                                        checked = item.isCompleted,
                                        onCheckedChange = { viewModel.toggleChecklistItem(item.id, it) }
                                    )
                                    Text(
                                        text = item.title,
                                        fontSize = 13.sp,
                                        color = if (item.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }

                                 IconButton(onClick = { viewModel.removeChecklistItem(item.id) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // CUSTOMER BOOKINGS & SECURED CONTRACTS LISTING
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "My Auspicious Bookings & Orders", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Verify statuses, payments, clear balances, and manage secure invoices:", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        if (bookings.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No bookings processed yet!", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text("Explore our local curated WedZen Marketplace category tabs to instantly lock a professional package to your date.", fontSize = 10.sp, color = Color.Gray, style = TextStyle(textAlign = TextAlign.Center))
                                }
                            }
                        } else {
                            bookings.forEach { b ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .border(
                                            width = if (b.status == "Confirmed") 1.dp else 0.dp,
                                            color = if (b.status == "Confirmed") Color(0xFF007A33).copy(alpha = 0.5f) else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(b.vendorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text(b.vendorCategory.uppercase() + " | " + b.bookingPackage, fontSize = 9.sp, color = Color.Gray)
                                            }
                                            
                                            // Booking status colored badge
                                            val badgeColor = when (b.status) {
                                                "Confirmed" -> Color(0xFFE8F5E9)
                                                "Slot Locked" -> Color(0xFFFFF8E1)
                                                "Refunded" -> Color(0xFFE3F2FD)
                                                else -> Color(0xFFFFEBEE)
                                            }
                                            val badgeTextColor = when (b.status) {
                                                "Confirmed" -> Color(0xFF2E7D32)
                                                "Slot Locked" -> Color(0xFFF57F17)
                                                "Refunded" -> Color(0xFF1565C0)
                                                else -> Color(0xFFC62828)
                                            }
                                            
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = badgeColor),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = b.status,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = badgeTextColor,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Booking date & guest parameters details
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Date: ${b.bookingDate}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                                Text("Slot: ${b.timeSlot}", fontSize = 10.sp, color = Color.Gray)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Venue: ${b.location}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                                Text("Guests: ${b.guestCount}", fontSize = 10.sp, color = Color.Gray)
                                            }
                                        }

                                        Divider(modifier = Modifier.padding(vertical = 6.dp))

                                        // Price paid / remaining summary ledger lines
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Advance Paid: " + formatIndianRupee(b.paidAmount), fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                                if (b.pendingAmount > 0) {
                                                    Text("Due balance: " + formatIndianRupee(b.pendingAmount), fontSize = 10.sp, color = Color(0xFFC62828))
                                                } else {
                                                    Text("Balance Closed", fontSize = 11.sp, color = Color.Gray)
                                                }
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Total Charged: " + formatIndianRupee(b.price), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text("GST included", fontSize = 9.sp, color = Color.Gray)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Horizontal row of contextual customer control actions
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            // 1. Invoice details pop-trigger
                                            OutlinedButton(
                                                onClick = { showingInvoiceBooking = b },
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f).height(32.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Invoice", fontSize = 10.sp)
                                            }

                                            // 2. Clear remaining 60% balance trigger button (if partial payment is incomplete)
                                            if (b.status == "Confirmed" && b.pendingAmount > 0) {
                                                Button(
                                                    onClick = { viewModel.payRemainingBalance(b.id) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1.3f).height(32.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("Pay final 60%", fontSize = 10.sp)
                                                }
                                            }

                                            // 3. Complete Refund Cancellation trigger button
                                            if (b.status == "Confirmed") {
                                                OutlinedButton(
                                                    onClick = { showingCancelRefundBooking = b },
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1.2f).height(32.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        contentColor = Color(0xFFC62828)
                                                    ),
                                                    contentPadding = PaddingValues(0.dp),
                                                    border = BorderStroke(1.dp, Color(0xFFC62828))
                                                ) {
                                                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFC62828))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("Cancel & Refund", fontSize = 9.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Vendor" -> {
                // Earning stats
                val bookedCount = bookings.size
                val totalEarnings = bookings.sumOf { it.price }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Vendor Business Earnings", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Booking Orders", fontSize = 11.sp, color = Color.Gray)
                                Text("$bookedCount Placed", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Estimated Earnings", fontSize = 11.sp, color = Color.Gray)
                                Text(formatIndianRupee(totalEarnings), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }

                // VENDOR SCHEDULE SLOTS AVAILABILITY TRACKER
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    val availabilitySlots by viewModel.vendorAvailabilitySlots.collectAsState()
                    val scope = rememberCoroutineScope()
                    
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Vendor Calendars & Slot Availability", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Toggle your studio's scheduling slots to prevent automated client booking blockages:", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        availabilitySlots.forEach { (slotName, isAvailable) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.updateVendorSlotAvailability(slotName, !isAvailable) }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.DateRange, 
                                        contentDescription = null, 
                                        tint = if (isAvailable) Color(0xFF007A33) else Color.Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(slotName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                                Switch(
                                    checked = isAvailable,
                                    onCheckedChange = { viewModel.updateVendorSlotAvailability(slotName, it) }
                                )
                            }
                        }
                    }
                }

                // VENDOR ACTIVE CONTRACT REQUESTS & ORDER TRACKER
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    val scope = rememberCoroutineScope()
                    
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Active Marketplace Bookings & Orders", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Accept client bookings, verify details, and manage cancellations instantly:", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        if (bookings.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No incoming active bookings", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text("When a customer books your vendor service from the checkout window, their transaction order details appear here.", fontSize = 10.sp, color = Color.Gray, style = TextStyle(textAlign = TextAlign.Center))
                                }
                            }
                        } else {
                            bookings.forEach { b ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Invoice: ${b.invoiceNumber}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(b.bookingPackage.uppercase() + " - " + b.timeSlot, fontSize = 9.sp, color = Color.Gray)
                                            }
                                            
                                            // Booking status colored badge
                                            val badgeColor = when (b.status) {
                                                "Confirmed" -> Color(0xFFE8F5E9)
                                                "Slot Locked" -> Color(0xFFFFF8E1)
                                                "Refunded" -> Color(0xFFE3F2FD)
                                                else -> Color(0xFFFFEBEE)
                                            }
                                            val badgeTextColor = when (b.status) {
                                                "Confirmed" -> Color(0xFF2E7D32)
                                                "Slot Locked" -> Color(0xFFF57F17)
                                                "Refunded" -> Color(0xFF1565C0)
                                                else -> Color(0xFFC62828)
                                            }
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = badgeColor),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = b.status,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = badgeTextColor,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Date: ${b.bookingDate}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                                Text("Guests: ${b.guestCount} heads", fontSize = 10.sp, color = Color.Gray)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Location: ${b.location}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                                Text("Service ID: ${b.vendorId}", fontSize = 10.sp, color = Color.Gray)
                                            }
                                        }

                                        Divider(modifier = Modifier.padding(vertical = 6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Auspicious Collected: " + formatIndianRupee(b.paidAmount), fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                                if (b.pendingAmount > 0) {
                                                    Text("Client balance pending: " + formatIndianRupee(b.pendingAmount), fontSize = 10.sp, color = Color(0xFFE65100))
                                                }
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Contract Net: " + formatIndianRupee(b.price), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text("Platform Fees (10%): " + formatIndianRupee(b.price * 0.10), fontSize = 9.sp, color = Color.Gray)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Vendor action choices
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            // 1. Accept contract (Only if pending or locked slot is waiting verify)
                                            if (b.status == "Slot Locked" || b.status == "Pending") {
                                                Button(
                                                    onClick = { viewModel.confirmBooking(b.id) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f).height(32.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("Accept Vows", fontSize = 10.sp)
                                                }
                                            }

                                            // 2. Reject booking and trigger automatic full advance refund return
                                            if (b.status == "Confirmed" || b.status == "Slot Locked") {
                                                OutlinedButton(
                                                    onClick = { viewModel.vendorRejectBooking(b.id) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f).height(32.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        contentColor = Color(0xFFC62828)
                                                    ),
                                                    contentPadding = PaddingValues(0.dp),
                                                    border = BorderStroke(1.dp, Color(0xFFC62828))
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFC62828))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("Reject Vows", fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Register vendor services
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Publish Service Listing to Marketplace", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Register details as a local service provider:", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = vendorNameInput,
                            onValueChange = { viewModel.newVendorName.value = it },
                            label = { Text("Vendor Business / Studio Name") },
                            modifier = Modifier.fillMaxWidth().testTag("add_vendor_name")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = vendorPriceInput,
                            onValueChange = { viewModel.newVendorPrice.value = it },
                            label = { Text("Standard Price package (INR)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("add_vendor_price")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = vendorDescInput,
                            onValueChange = { viewModel.newVendorDescription.value = it },
                            label = { Text("Complete Service description & Specialties") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category choice
                        val categoriesList = listOf("Catering", "Decoration", "Venue", "Makeup", "Photographer", "Mehendi", "DJ & Music")
                        Text("Choose Category", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        categoriesList.forEach { cat ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = (vendorCatInput == cat), onClick = { viewModel.newVendorCategory.value = cat })
                                Text(cat, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Regional choice
                        val regionalList = listOf("Universal", "Assamese", "Punjabi", "Bengali", "South Indian", "Muslim")
                        Text("Tradition Specialty Constraint", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        regionalList.forEach { reg ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = (vendorRegInput == reg), onClick = { viewModel.newVendorRegion.value = reg })
                                Text("$reg Traditions", fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.registerNewVendor() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("submit_vendor_form")
                        ) {
                            Text("Publish Business Live", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            "Admin" -> {
                // Admin dashboard
                val commissionRate by viewModel.platformCommissionRate.collectAsState()
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    val totalVolume = bookings.sumOf { it.price }
                    val totalCollected = bookings.sumOf { it.paidAmount }
                    val adminEstProfits = totalVolume * (commissionRate / 100.0)
                    
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Super-Admin Control Console", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                        Text(text = "Global Financial Ledger & Commission Settlement Matrix (Real-time):", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Multi-vendor statistics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Gross Volume (GMV)", fontSize = 9.sp, color = Color.Gray)
                                    Text(formatIndianRupee(totalVolume), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Total Cash Settled", fontSize = 9.sp, color = Color.Gray)
                                    Text(formatIndianRupee(totalCollected), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Est. Admin Commission (${commissionRate}%)", fontSize = 9.sp, color = Color(0xFF2E7D32))
                                    Text(formatIndianRupee(adminEstProfits), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                                }
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Secured Escrow Balance", fontSize = 9.sp, color = Color(0xFFE65100))
                                    Text(formatIndianRupee(totalVolume - totalCollected), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFE65100))
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        // Dynamic Commission percentage editor
                        Text("Dynamic Commission Fee Rate Control", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Instantly override the global facilitation commission percent applied across listings:", fontSize = 10.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(5.0, 8.0, 10.0, 12.0, 15.0).forEach { pct ->
                                val activePct = commissionRate == pct
                                Button(
                                    onClick = { viewModel.updatePlatformCommissionRate(pct) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (activePct) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f),
                                        contentColor = if (activePct) MaterialTheme.colorScheme.onPrimary else Color.DarkGray
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(32.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("${pct.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customCommissionStr,
                                onValueChange = { customCommissionStr = it },
                                label = { Text("Custom Facilitation Percentage", fontSize = 10.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).height(50.dp),
                                textStyle = TextStyle(fontSize = 11.sp)
                            )
                            Button(
                                onClick = {
                                    val newRate = customCommissionStr.toDoubleOrNull()
                                    if (newRate != null && newRate in 0.0..100.0) {
                                        viewModel.updatePlatformCommissionRate(newRate)
                                        customCommissionStr = ""
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("Set", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // VENDOR DIRECTORY & PLATFORM CURATION OVERRIDE
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Vendor Directory & Curation Override", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Feature premium sellers or delete/block non-compliant listings:", fontSize = 11.sp, color = Color.Gray)
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Text(
                                    text = "${vendors.size} Active",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (vendors.isEmpty()) {
                            Text(
                                "No active vendors cataloged in the system. Go to direct vendor registration console in Vendor Role tab to add.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                style = TextStyle(textAlign = TextAlign.Center)
                            )
                        } else {
                            vendors.forEach { vendor ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = vendor.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                maxLines = 1
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = vendor.category, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                                Text("•", fontSize = 10.sp, color = Color.Gray)
                                                Text(text = "Specialty: ${vendor.regionalTemplate}", fontSize = 10.sp, color = Color.Gray)
                                                Text("•", fontSize = 10.sp, color = Color.Gray)
                                                Text(text = formatIndianRupee(vendor.price), fontSize = 10.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            // Toggle Feature Button / Switch
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = if (vendor.isFeatured) "FEATURED" else "STANDARD",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (vendor.isFeatured) Color(0xFF2E7D32) else Color.DarkGray
                                                )
                                                Switch(
                                                    checked = vendor.isFeatured,
                                                    onCheckedChange = { viewModel.toggleVendorFeatured(vendor.id, it) }
                                                )
                                            }

                                            // Terminate / Delete vendor icon
                                            IconButton(
                                                onClick = { viewModel.deleteVendorListing(vendor.id) },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Terminate Vendor Listing",
                                                    tint = Color(0xFFC62828),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ADMIN REFUND & CANCELLATION DISPUTE CONTROL CENTER
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Refund & Dispute Control Center", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Approve payouts, reverse Razorpay payments, and enforce cancellation fees:", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        val refundList = bookings.filter { it.refundRequested || it.refundStatus == "Requested" }

                        if (refundList.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "No active refund disputes or cancellation reversions pending super-admin verification.",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(14.dp),
                                    style = TextStyle(textAlign = TextAlign.Center)
                                )
                            }
                        } else {
                            refundList.forEach { b ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Invoice: ${b.invoiceNumber}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("Disputed Item: ${b.vendorName}", fontSize = 10.sp, color = Color.Gray)
                                            }
                                            
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (b.refundStatus == "Approved" || b.status == "Refunded") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                                ),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = b.refundStatus ?: "Requested",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (b.refundStatus == "Approved" || b.status == "Refunded") Color(0xFF2E7D32) else Color(0xFFC62828),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        
                                        Text(
                                            text = "Reason: ${b.rejectionReason ?: "Cancellation Request Submit"}",
                                            fontSize = 11.sp,
                                            color = Color.DarkGray,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Divider(modifier = Modifier.padding(vertical = 6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text("Advance Collected: " + formatIndianRupee(b.paidAmount), fontSize = 11.sp, color = Color.Gray)
                                                Text("Admin Surcharge Withheld (10%): " + formatIndianRupee(b.paidAmount * 0.10), fontSize = 10.sp, color = Color(0xFFC62828))
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Net Refund Due: " + formatIndianRupee(b.refundAmount), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                                Text("90% Returned", fontSize = 9.sp, color = Color.Gray)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Action buttons if the refundStatus is "Requested" or status is not fully Refuned
                                        if (b.status == "Cancelled" && b.refundStatus == "Requested") {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                // 1. Approve payout dispatch
                                                Button(
                                                    onClick = { viewModel.adminApproveRefund(b.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f).height(32.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("Approve Refund", fontSize = 10.sp)
                                                }

                                                // 2. Reject payout refund claim
                                                OutlinedButton(
                                                    onClick = { viewModel.adminRejectRefund(b.id) },
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f).height(32.dp),
                                                    contentPadding = PaddingValues(0.dp),
                                                    border = BorderStroke(1.dp, Color(0xFFC62828))
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFC62828))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("Reject Dispute", fontSize = 10.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // GLOBAL LIVE BOOKINGS OVERRIDE LEDGER
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Global Live Bookings Override Ledger", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Super-admin master database override. Force states or delete bookings:", fontSize = 11.sp, color = Color.Gray)
                            }
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Text(
                                    text = "${bookings.size} Total",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (bookings.isEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "No booking records or transactions registered in system database yet.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(14.dp),
                                    style = TextStyle(textAlign = TextAlign.Center)
                                )
                            }
                        } else {
                            bookings.forEach { b ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("ID: #${b.id} — Invoice: ${b.invoiceNumber}", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                Text("Client booked: ${b.vendorName}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                            }
                                            
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = when (b.status) {
                                                        "Fully Paid" -> Color(0xFFE0F2F1)
                                                        "Confirmed" -> Color(0xFFE8F5E9)
                                                        "Cancelled" -> Color(0xFFFFEBEE)
                                                        "Refunded" -> Color(0xFFECEFF1)
                                                        else -> Color(0xFFFFF8E1)
                                                    }
                                                )
                                            ) {
                                                Text(
                                                    text = b.status,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (b.status) {
                                                        "Fully Paid" -> Color(0xFF00796B)
                                                        "Confirmed" -> Color(0xFF2E7D32)
                                                        "Cancelled" -> Color(0xFFC62828)
                                                        "Refunded" -> Color(0xFF37474F)
                                                        else -> Color(0xFFF57F17)
                                                    },
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Slot: ${b.bookingDate} (${b.timeSlot})",
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                            Text(
                                                text = "Paid: ${formatIndianRupee(b.paidAmount)} / Due: ${formatIndianRupee(b.pendingAmount)}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.DarkGray
                                            )
                                        }

                                        Divider(modifier = Modifier.padding(vertical = 6.dp))

                                        // Override Operations
                                        Text("Force Status Transitions:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // 1. Force Confirmed
                                            if (b.status != "Confirmed") {
                                                Button(
                                                    onClick = { viewModel.adminForceUpdateBookingStatus(b.id, "Confirmed") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.height(26.dp).weight(1f),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Lock Confirm", fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                                }
                                            }

                                            // 2. Force Paid
                                            if (b.status != "Fully Paid") {
                                                Button(
                                                    onClick = { viewModel.adminForceUpdateBookingStatus(b.id, "Fully Paid") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.height(26.dp).weight(1f),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Mark Paid", fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                                }
                                            }

                                            // 3. Force Cancelled
                                            if (b.status != "Cancelled" && b.status != "Refunded") {
                                                Button(
                                                    onClick = { viewModel.adminForceUpdateBookingStatus(b.id, "Cancelled") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.height(26.dp).weight(1f),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Force Cancel", fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                                }
                                            }

                                            // 4. Force Refunded
                                            if (b.status == "Cancelled" || b.status == "Refund Requested") {
                                                Button(
                                                    onClick = { viewModel.adminForceUpdateBookingStatus(b.id, "Refunded") },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.height(26.dp).weight(1f),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Text("Force Refund", fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                                }
                                            }

                                            // 5. Delete Completely
                                            IconButton(
                                                onClick = { viewModel.adminDeleteBookingRecord(b.id) },
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Booking Record",
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // MARKETING COUPON CONTROL LEDGER
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val couponsMap by viewModel.availableCoupons.collectAsState()

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Auspicious Discount Vouchers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Dynamic marketing coupon registry. Add or revoke family planner promotions:", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Mini form to insert new coupons
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Generate Dynamic Coupon Code", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newCouponCode,
                                        onValueChange = { newCouponCode = it },
                                        label = { Text("Code (e.g. WEDVIP)", fontSize = 9.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).height(46.dp),
                                        textStyle = TextStyle(fontSize = 11.sp)
                                    )
                                    OutlinedTextField(
                                        value = newCouponDiscountStr,
                                        onValueChange = { newCouponDiscountStr = it },
                                        label = { Text("Val (e.g. 0.15 or 10000)", fontSize = 9.sp) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f).height(46.dp),
                                        textStyle = TextStyle(fontSize = 11.sp)
                                    )
                                }

                                Button(
                                    onClick = {
                                        val d = newCouponDiscountStr.toDoubleOrNull()
                                        if (newCouponCode.isNotBlank() && d != null && d > 0.0) {
                                            viewModel.addCoupon(newCouponCode, d)
                                            newCouponCode = ""
                                            newCouponDiscountStr = ""
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Inject Coupon Live", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // List of active coupons from database Map
                        if (couponsMap.isEmpty()) {
                            Text(
                                text = "Zero marketing vouchers exist in registry. Create one using the generator above.",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                style = TextStyle(textAlign = TextAlign.Center)
                            )
                        } else {
                            couponsMap.forEach { (couponCode, couponVal) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = couponCode,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        val desc = if (couponVal < 1.0) {
                                            "Get ${(couponVal * 100).toInt()}% off instantly on standard booking totals"
                                        } else {
                                            "Get a flat ₹${String.format("%,.0f", couponVal)} cashback/discount applied at checkout"
                                        }
                                        Text(text = desc, fontSize = 10.sp, color = Color.Gray)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFE8F5E9)
                                            )
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.removeCoupon(couponCode) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Revoke Promo",
                                                tint = Color.Red,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAX INVOICE DETAILS MODAL
        if (showingInvoiceBooking != null) {
            val b = showingInvoiceBooking!!
            AlertDialog(
                onDismissRequest = { showingInvoiceBooking = null },
                title = { Text("Tax Invoice Details — WZ-2026", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("GSTIN Registration: 18AAAFW1234F1Z0 (WedZen Unified Platform)", fontSize = 11.sp, color = Color.Gray)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Vendor Service:", fontSize = 11.sp)
                            Text(b.vendorName, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Invoice Number:", fontSize = 11.sp)
                            Text(b.invoiceNumber, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Transaction Date:", fontSize = 11.sp)
                            Text(b.bookingDate, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Razorpay Order Ref:", fontSize = 11.sp)
                            Text(b.razorpayOrderId ?: "N/A", fontSize = 10.sp, color = Color.Gray)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Razorpay Payment Ref:", fontSize = 11.sp)
                            Text(b.razorpayPaymentId ?: "Simulated Gateway", fontSize = 10.sp, color = Color.Gray)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Net Base Charge:", fontSize = 11.sp)
                            Text(formatIndianRupee(b.price - b.gstAmount + b.discountAmount), fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Applied Coupon Promo:", fontSize = 11.sp, color = Color(0xFF007A33))
                            Text("- " + formatIndianRupee(b.discountAmount), fontSize = 11.sp, color = Color(0xFF007A33))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("18% GST Surcharge:", fontSize = 11.sp)
                            Text(formatIndianRupee(b.gstAmount), fontSize = 11.sp)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Invoice Amount:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(formatIndianRupee(b.price), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Amount Settle Status:", fontSize = 11.sp)
                            Text(if (b.pendingAmount == 0.0) "Fully Paid" else "40% Advance Lock", fontWeight = FontWeight.Bold, color = if (b.pendingAmount == 0.0) Color(0xFF007A33) else Color(0xFFD32F2F), fontSize = 11.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showingInvoiceBooking = null }) {
                        Text("Close Tax Copy")
                    }
                }
            )
        }

        // REFUND & FILE DISPUTE REGISTRATION MODAL
        if (showingCancelRefundBooking != null) {
            val b = showingCancelRefundBooking!!
            val totalPaid = b.paidAmount
            val cancelFee = totalPaid * 0.10
            val extRefund = totalPaid - cancelFee
            AlertDialog(
                onDismissRequest = { showingCancelRefundBooking = null },
                title = { Text("Cancel Booking Contract", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Deduction Penalty Notice (Client Cancellation Policy):", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color(0xFFD32F2F))
                        Text("In accordance with regional vendor scheduling guarantees, a standard 10% platform cancellation surcharge will be withheld, and 90% processed back to source.", fontSize = 10.sp, color = Color.Gray)
                        
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Advance paid:", fontSize = 11.sp)
                                    Text(formatIndianRupee(totalPaid), fontSize = 11.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Cancellation penalty (10%):", fontSize = 11.sp, color = Color(0xFFD32F2F))
                                    Text("- " + formatIndianRupee(cancelFee), fontSize = 11.sp, color = Color(0xFFD32F2F))
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Net Estimated refund payout due:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF007A33))
                                    Text(formatIndianRupee(extRefund), fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF007A33))
                                }
                            }
                        }

                        OutlinedTextField(
                            value = cancelReasonInput,
                            onValueChange = { cancelReasonInput = it },
                            placeholder = { Text("Enter cancellation reason...") },
                            label = { Text("Reason for Cancellation") },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            textStyle = TextStyle(fontSize = 12.sp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.requestBookingCancelAndRefund(b.id, cancelReasonInput)
                            showingCancelRefundBooking = null
                            cancelReasonInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Text("Confirm Refund & Release Slot")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showingCancelRefundBooking = null }) {
                        Text("Keep Contract")
                    }
                }
            )
        }
    }
}

// Simple layout scaling helper
private fun Modifier.scale(scale: Float): Modifier = this
