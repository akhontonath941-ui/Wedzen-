package com.example.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.LocalVendor
import com.example.ui.theme.HeritageGoldAccent
import com.example.ui.MainViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun getDrawableIdByName(name: String): Int {
    return when (name) {
        "img_home_hero" -> R.drawable.img_home_hero
        "img_catering_food" -> R.drawable.img_catering_food
        "img_makeup_artist" -> R.drawable.img_makeup_artist
        "img_decor_venue" -> R.drawable.img_decor_venue
        else -> R.drawable.img_launcher_logo
    }
}

fun formatIndianRupee(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: MainViewModel,
    onNavigateToPlanner: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val activeCategory by viewModel.selectedCategory.collectAsState()
    val activeRegion by viewModel.selectedRegion.collectAsState()
    val vendors by viewModel.filteredVendors.collectAsState()
    val selectedVendor by viewModel.selectedVendor.collectAsState()
    val commissionRate by viewModel.platformCommissionRate.collectAsState()

    var showPaymentDialog by remember { mutableStateOf(false) }
    var selectedBookingDate by remember { mutableStateOf("") }
    var chosenPaymentMethod by remember { mutableStateOf("UPI Link") }
    val context = LocalContext.current

    val categories = listOf("All", "Venue", "Catering", "Makeup", "Decoration", "Photographer", "Mehendi", "DJ & Music", "Traditional", "Priest")
    val regions = listOf("All", "Universal", "Assamese", "Punjabi", "Bengali", "South Indian", "Muslim")

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Top row with Title and Slogan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "WedZen Marketplace",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Auspicious Indian Wedding Vendors",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }

                    // Luxury Gold Badge
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "PREMIUM",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Search catering, venues, makeup...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("marketplace_search")
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Banner image with Indian wedding vibe
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_home_hero),
                    contentDescription = "Wedding Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Dark Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Plan without hassle",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Up to 30% savings with unified regional wed-packages",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Categories list (Horizontal Scroll)
            Text(
                text = "Select Service Category",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = 13.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = activeCategory == cat
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .clickable { viewModel.selectedCategory.value = cat }
                            .testTag("cat_tag_$cat")
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Traditional Regional Templates Selection
            Text(
                text = "Regional Tradition Templates",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 13.dp)
            ) {
                items(regions) { region ->
                    val isSelected = activeRegion == region
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .clickable { viewModel.selectedRegion.value = region }
                            .testTag("region_tag_$region")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (region == "All") Icons.Default.Favorite else Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isSelected) Color.Black else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = region,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Results counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${vendors.size} Premium Vendors Found",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                if (activeCategory != "All" || activeRegion != "All" || query.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            viewModel.selectedCategory.value = "All"
                            viewModel.selectedRegion.value = "All"
                            viewModel.searchQuery.value = ""
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Reset Filters", fontSize = 12.sp)
                    }
                }
            }

            // Grid of Vendors list
            if (vendors.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No vendors match your filters",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Text(
                            "Try clearing tags or typing other key terms.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    vendors.forEach { vendor ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { viewModel.selectedVendor.value = vendor }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                // Image Thumbnail
                                Image(
                                    painter = painterResource(id = getDrawableIdByName(vendor.imageResName)),
                                    contentDescription = vendor.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    // Row with category pill and region
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = vendor.category,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 11.sp
                                        )

                                        if (vendor.regionalTemplate != "Universal") {
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = vendor.regionalTemplate,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = vendor.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = HeritageGoldAccent,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "${vendor.rating} (${vendor.reviewsCount} reviews)",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            modifier = Modifier.padding(start = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = vendor.location,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = formatIndianRupee(vendor.price),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // IMMERSIVE VENDOR DETAIL POPUP DIALOG
    selectedVendor?.let { vendor ->
        Dialog(onDismissRequest = { viewModel.selectedVendor.value = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Image(
                            painter = painterResource(id = getDrawableIdByName(vendor.imageResName)),
                            contentDescription = vendor.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Top buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { viewModel.selectedVendor.value = null },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.6f))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = CircleShape
                            ) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = "Shortlist",
                                    tint = HeritageGoldAccent,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = vendor.category.uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = HeritageGoldAccent, modifier = Modifier.size(16.dp))
                                Text(text = " ${vendor.rating}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = vendor.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Text(
                                text = vendor.location,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Text(
                            text = "Service Structure Profile",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = vendor.description.ifEmpty { "A highly classical and elite service tailored to provide a deeply emotional touch for couples celebrating their premium vows. Rated exceptionally well of premium aesthetics." },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Auspicious Pricing Package:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = formatIndianRupee(vendor.price),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Incl. 18% wedding GST",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Schedule Input Box
                        Text(
                            text = "Choose Auspicious Date",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        var isDateExpanded by remember { mutableStateOf(false) }
                        OutlinedTextField(
                            value = selectedBookingDate,
                            onValueChange = { selectedBookingDate = it },
                            placeholder = { Text("YYYY-MM-DD") },
                            leadingIcon = {
                                IconButton(onClick = {
                                    val calendar = Calendar.getInstance()
                                    DatePickerDialog(
                                        context,
                                        { _: DatePicker, year: Int, month: Int, day: Int ->
                                            selectedBookingDate = "$year-${month + 1}-$day"
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("booking_date_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Primary Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Chat button
                            OutlinedButton(
                                onClick = {
                                    viewModel.activeChatVendorId.value = vendor.id
                                    viewModel.activeChatVendorName.value = vendor.name
                                    viewModel.selectedVendor.value = null
                                    onNavigateToChat()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .padding(end = 4.dp)
                            ) {
                                Icon(Icons.Default.MailOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat Vendor", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                            }

                            // Book button
                            Button(
                                onClick = {
                                    val finalDate = if (selectedBookingDate.isNotEmpty()) selectedBookingDate else "2026-11-28"
                                    viewModel.selectedBookingDate.value = finalDate
                                    viewModel.startVendorCheckout(vendor)
                                    showPaymentDialog = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(48.dp)
                                    .padding(start = 4.dp)
                                    .testTag("book_now_trigger")
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Book Unified", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // BEAUTIFULLY CUSTOMIZED SECURE MULTI-GATEWAY PAYMENTS DIALOG
    if (showPaymentDialog) {
        val checkoutStep by viewModel.checkoutStep.collectAsState()
        val checkoutVendor by viewModel.checkoutVendor.collectAsState()
        val selectedBookingPackage by viewModel.selectedBookingPackage.collectAsState()
        val selectedBookingDate by viewModel.selectedBookingDate.collectAsState()
        val selectedTimeSlot by viewModel.selectedTimeSlot.collectAsState()
        val selectedGuestCount by viewModel.selectedGuestCount.collectAsState()
        val selectedBookingLocation by viewModel.selectedBookingLocation.collectAsState()
        val appliedCouponCode by viewModel.appliedCouponCode.collectAsState()
        val couponDiscountValue by viewModel.couponDiscountValue.collectAsState()
        val isFullPaymentMode by viewModel.isFullPaymentMode.collectAsState()
        
        val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
        val simulatedUpiId by viewModel.simulatedUpiId.collectAsState()
        val simulatedCardNo by viewModel.simulatedCardNo.collectAsState()
        val simulatedCardExpiry by viewModel.simulatedCardExpiry.collectAsState()
        val simulatedCardCvv by viewModel.simulatedCardCvv.collectAsState()
        val simulatedNetBankName by viewModel.simulatedNetBankName.collectAsState()
        val simulatedWalletName by viewModel.simulatedWalletName.collectAsState()
        val simulatedEmiMonths by viewModel.simulatedEmiMonths.collectAsState()
        
        val simulatePaymentFailure by viewModel.simulatePaymentFailure.collectAsState()
        val paymentErrorMessage by viewModel.paymentErrorMessage.collectAsState()
        val slotLockedCountDownSecs by viewModel.slotLockedCountDownSecs.collectAsState()
        val activeCheckoutBookingId by viewModel.activeCheckoutBookingId.collectAsState()

        // Core Countdown ticker implementation for locking slot
        LaunchedEffect(checkoutStep) {
            if (checkoutStep == "RazorpaySim") {
                while (viewModel.slotLockedCountDownSecs.value > 0 && viewModel.checkoutStep.value == "RazorpaySim") {
                    kotlinx.coroutines.delay(1000)
                    viewModel.slotLockedCountDownSecs.value -= 1
                }
                if (viewModel.slotLockedCountDownSecs.value <= 0 && viewModel.checkoutStep.value == "RazorpaySim") {
                    viewModel.simulatePaymentFailure("Auspicious booking slot reservation lock expired (10 mins)")
                }
            }
        }

        if (checkoutVendor != null) {
            val vendor = checkoutVendor!!
            
            // Base price based on selected package
            val priceMultiplier = when (selectedBookingPackage) {
                "Standard Budget" -> 0.75
                "Premium Royal Deluxe" -> 1.0
                "Luxury Kohinoor Signature" -> 1.40
                else -> 1.0
            }
            val basePrice = vendor.price * priceMultiplier
            val afterDiscount = (basePrice - couponDiscountValue).coerceAtLeast(0.0)
            val gstAmount = afterDiscount * 0.18
            val totalCharged = afterDiscount + gstAmount
            val advanceRequirement = totalCharged * 0.40
            val pendingRemaining = totalCharged - advanceRequirement
            
            val activeChargeToPayNow = if (isFullPaymentMode) totalCharged else advanceRequirement

            Dialog(onDismissRequest = { 
                // Don't close if slot is locked and payment is mid-way unless user explicitly cancels
                if (checkoutStep != "RazorpaySim") {
                    showPaymentDialog = false 
                }
            }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        
                        // DIALOG HEADER
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Lock, 
                                    contentDescription = "Secure", 
                                    tint = HeritageGoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when(checkoutStep) {
                                        "Overview" -> "Secure Wedding Checkout"
                                        "RazorpaySim" -> "Razorpay Secure Engine"
                                        "Receipt" -> "Booking Confirmed! ✨"
                                        else -> "Secure Checkout"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { showPaymentDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // STEP 1 & 2: OVERVIEW ENGINE (AMAZON / FLIPKART STYLE)
                        if (checkoutStep == "Overview") {
                            Text(
                                "1. Customize Wedding Package",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            
                            // Horizontal Row of Package Cards (Selectable)
                            val pkgs = listOf("Standard Budget", "Premium Royal Deluxe", "Luxury Kohinoor Signature")
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                pkgs.forEach { pkg ->
                                    val isSelected = (selectedBookingPackage == pkg)
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                                             else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.selectedBookingPackage.value = pkg }
                                            .border(
                                                width = if (isSelected) 2.dp else 0.dp,
                                                color = if (isSelected) HeritageGoldAccent else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                pkg.substringBefore(" "), 
                                                fontWeight = FontWeight.Bold, 
                                                fontSize = 11.sp,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
                                            )
                                            Text(
                                                if (pkg == "Standard Budget") "-25% Off" else if (pkg == "Luxury Kohinoor Signature") "+40% Premium" else "Base Rate",
                                                fontSize = 9.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                "2. Select Guest & Slot Particulars",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            // Guest count input slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Auspicious Guest Count:", fontSize = 11.sp)
                                Text("${selectedGuestCount} Guests", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Slider(
                                value = selectedGuestCount.toFloat(),
                                onValueChange = { viewModel.selectedGuestCount.value = it.toInt() },
                                valueRange = 50f..1000f,
                                steps = 19,
                                colors = SliderDefaults.colors(
                                    thumbColor = HeritageGoldAccent,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            // Time slot chooser dropdown simulation
                            Text("Auspicious Time Slot:", fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                            val slots = listOf(
                                "Morning (9 AM - 2 PM)", 
                                "Afternoon (2 PM - 7 PM)", 
                                "Evening Celebration (4 PM - 11 PM)", 
                                "Full Day (10 AM - 10 PM)"
                            )
                            var timeSlotExpanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = selectedTimeSlot,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        IconButton(onClick = { timeSlotExpanded = true }) {
                                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.rotate(-90f))
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = TextStyle(fontSize = 12.sp)
                                )
                                DropdownMenu(
                                    expanded = timeSlotExpanded,
                                    onDismissRequest = { timeSlotExpanded = false }
                                ) {
                                    slots.forEach { s ->
                                        DropdownMenuItem(
                                            text = { Text(s, fontSize = 12.sp) },
                                            onClick = {
                                                viewModel.selectedTimeSlot.value = s
                                                timeSlotExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // PROMO COUPON CODE SECTION (Matches Amazon layout)
                            Text(
                                "3. Secure Promotions & Discounts",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                var tempCouponCode by remember { mutableStateOf(appliedCouponCode) }
                                OutlinedTextField(
                                    value = tempCouponCode,
                                    onValueChange = { tempCouponCode = it },
                                    placeholder = { Text("Code: e.g. SHUBHUTSAV", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1.5f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = HeritageGoldAccent
                                    ),
                                    textStyle = TextStyle(fontSize = 12.sp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        viewModel.applyCheckoutCoupon(tempCouponCode)
                                    },
                                    modifier = Modifier.weight(0.8f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Apply", fontSize = 11.sp)
                                }
                            }
                            if (appliedCouponCode.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF007A33), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Success: Code $appliedCouponCode successfully applied! Saved ${formatIndianRupee(couponDiscountValue)}", color = Color(0xFF007A33), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                }
                            } else {
                                Text("Tip: Type SHUBHUTSAV for ₹50,000 flat off or WEDHAPPY10 for 10% off", color = Color.Gray, fontSize = 9.sp, modifier = Modifier.padding(top = 3.dp))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // BILL DETAILS BREAKDOWN
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Real-time Trust breakdown:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("$selectedBookingPackage:", fontSize = 11.sp)
                                        Text(formatIndianRupee(basePrice), fontSize = 11.sp)
                                    }
                                    if (couponDiscountValue > 0) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Coupons Applied Discount:", fontSize = 11.sp, color = Color(0xFF007A33))
                                            Text("- " + formatIndianRupee(couponDiscountValue), fontSize = 11.sp, color = Color(0xFF007A33))
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Professional Mandap GST (18%):", fontSize = 11.sp)
                                        Text(formatIndianRupee(gstAmount), fontSize = 11.sp)
                                    }
                                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Gross Booking Value:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(formatIndianRupee(totalCharged), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // ADVANCE SPLIT AND OPTION CHOOSER
                            Text(
                                "4. Choose Booking Payment Mode",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.isFullPaymentMode.value = false }
                                    ) {
                                        RadioButton(
                                            selected = !isFullPaymentMode,
                                            onClick = { viewModel.isFullPaymentMode.value = false }
                                        )
                                        Column {
                                            Text("Auspicious Partial Advance (40% to lock)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Pay ${formatIndianRupee(advanceRequirement)} today. Remaining ${formatIndianRupee(pendingRemaining)} due later.", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.isFullPaymentMode.value = true }
                                    ) {
                                        RadioButton(
                                            selected = isFullPaymentMode,
                                            onClick = { viewModel.isFullPaymentMode.value = true }
                                        )
                                        Column {
                                            Text("Complete 100% Full Payment (No debts)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Pay ${formatIndianRupee(totalCharged)} today. Reassured with priority backup slot protection.", fontSize = 10.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // SLOT RESERVATION WARNING NOTE
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Slot Reservation Lock: Clicking below will temporarily lock your date ($selectedBookingDate) for 10 minutes, securing scheduling priority. Failure to complete in 10 mins automatically releases the slot.",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // PROCEED TO GATEWAY BUTTON
                            Button(
                                onClick = {
                                    viewModel.proceedToPayment()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("proceed_to_payment_gateway"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Auspiciously Lock Slot & Pay ₹" + String.format("%,.0f", activeChargeToPayNow))
                            }

                            // Secure badges
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Amazon Secure | Razorpay | RBI Verified PCI-DSS", color = Color.Gray, fontSize = 9.sp)
                            }
                        }

                        // STEP 3: RAZORPAY SECURITY ENGINE SIMULATOR (HIGH-FIDELITY MODALPOPUP)
                        else if (checkoutStep == "RazorpaySim") {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1938)), // Dark Theme Razorpay Header
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("RAZORPAY SECURE PAYMENT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text("Merchant: WedZen multi-vendor Marketplace", color = Color.LightGray, fontSize = 9.sp)
                                    }
                                    
                                    // Visual Live Countdown Timer
                                    val mins = slotLockedCountDownSecs / 60
                                    val secs = slotLockedCountDownSecs % 60
                                    val timeStr = String.format("%02d:%02d", mins, secs)
                                    
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = if (slotLockedCountDownSecs < 120) Color(0xFFE53935) else Color(0xFF00C853)),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            "Slot Reserved $timeStr", 
                                            color = Color.White, 
                                            fontWeight = FontWeight.Bold, 
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                "Total Due Charged: ₹" + String.format("%,.2f", activeChargeToPayNow),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Payment Method Select Options
                            Text("Choose Payment Gateway Channel:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                            val rzpMethods = listOf("UPI", "Card", "NetBanking", "Wallet", "EMI")
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                rzpMethods.forEach { method ->
                                    val isMethodSelected = (selectedPaymentMethod == method)
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isMethodSelected) MaterialTheme.colorScheme.primaryContainer 
                                                             else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.selectedPaymentMethod.value = method }
                                            .height(36.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(method, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Conditional Sub-Forms based on selection
                            when (selectedPaymentMethod) {
                                "UPI" -> {
                                    OutlinedTextField(
                                        value = simulatedUpiId,
                                        onValueChange = { viewModel.simulatedUpiId.value = it },
                                        label = { Text("Enter UPI ID (VPA) for verification", fontSize = 11.sp) },
                                        placeholder = { Text("rohan@okaxis") },
                                        modifier = Modifier.fillMaxWidth(),
                                        textStyle = TextStyle(fontSize = 12.sp)
                                    )
                                    Text("Validates instantly using RBI VPA directory", color = Color.Gray, fontSize = 8.sp, modifier = Modifier.padding(start = 2.dp, top = 2.dp))
                                }
                                "Card" -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedTextField(
                                            value = simulatedCardNo,
                                            onValueChange = { viewModel.simulatedCardNo.value = it },
                                            label = { Text("Debit/Credit Card Number", fontSize = 11.sp) },
                                            modifier = Modifier.fillMaxWidth(),
                                            textStyle = TextStyle(fontSize = 12.sp)
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            OutlinedTextField(
                                                value = simulatedCardExpiry,
                                                onValueChange = { viewModel.simulatedCardExpiry.value = it },
                                                label = { Text("Expiry (MM/YY)", fontSize = 11.sp) },
                                                modifier = Modifier.weight(1f),
                                                textStyle = TextStyle(fontSize = 12.sp)
                                            )
                                            OutlinedTextField(
                                                value = simulatedCardCvv,
                                                onValueChange = { viewModel.simulatedCardCvv.value = it },
                                                label = { Text("CVV", fontSize = 11.sp) },
                                                modifier = Modifier.weight(1f),
                                                textStyle = TextStyle(fontSize = 12.sp)
                                            )
                                        }
                                    }
                                }
                                "NetBanking" -> {
                                    val banks = listOf("State Bank of India (SBI)", "ICICI Bank", "HDFC Bank", "Axis Bank", "Punjab National Bank (PNB)")
                                    var bankExpanded by remember { mutableStateOf(false) }
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = simulatedNetBankName,
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                IconButton(onClick = { bankExpanded = true }) {
                                                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.rotate(-90f))
                                                }
                                            },
                                            label = { Text("Select Secured NetBank Portal", fontSize = 10.sp) },
                                            modifier = Modifier.fillMaxWidth(),
                                            textStyle = TextStyle(fontSize = 12.sp)
                                        )
                                        DropdownMenu(
                                            expanded = bankExpanded,
                                            onDismissRequest = { bankExpanded = false }
                                        ) {
                                            banks.forEach { b ->
                                                DropdownMenuItem(
                                                    text = { Text(b, fontSize = 11.sp) },
                                                    onClick = {
                                                        viewModel.simulatedNetBankName.value = b
                                                        bankExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                "Wallet" -> {
                                    val wallets = listOf("Paytm Wallet", "Amazon Pay", "PhonePe Wallet", "MobiKwik Air")
                                    wallets.forEach { w ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { viewModel.simulatedWalletName.value = w }
                                                .padding(vertical = 4.dp)
                                        ) {
                                            RadioButton(selected = (simulatedWalletName == w), onClick = { viewModel.simulatedWalletName.value = w })
                                            Text(w, fontSize = 11.sp)
                                        }
                                    }
                                }
                                "EMI" -> {
                                    Text("Simulated Interest-free 12 Months EMI plans:", fontSize = 10.sp, color = Color.Gray)
                                    val emis = listOf(3, 6, 9, 12, 18)
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                                        emis.forEach { m ->
                                            val isSelected = (simulatedEmiMonths == m)
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                                ),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable { viewModel.simulatedEmiMonths.value = m }
                                            ) {
                                                Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text("${m} Months", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                    Text("₹" + String.format("%.0f/mo", activeChargeToPayNow / m), fontSize = 8.sp, color = Color.Gray)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // SIMULATION MODE SWITCHES FOR SECURE VERIFICATION TESTING
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Security Simulation Mode:", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    Text(
                                        if (simulatePaymentFailure) "Active: Payment Failure Scenario" 
                                        else "Active: Secure Signature Verification Succeeds",
                                        fontSize = 8.sp,
                                        color = if (simulatePaymentFailure) Color(0xFFE53935) else Color(0xFF007A33)
                                    )
                                }
                                Button(
                                    onClick = { viewModel.simulatePaymentFailure.value = !simulatePaymentFailure },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (simulatePaymentFailure) Color(0xFFE53935) else Color(0xFF007A33)
                                    ),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(if (simulatePaymentFailure) "Make Success" else "Make Failure", fontSize = 8.sp)
                                }
                            }

                            if (paymentErrorMessage.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                        .border(1.dp, Color(0xFFD32F2F), RoundedCornerShape(4.dp))
                                        .background(Color(0xFFFFEBEE))
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(paymentErrorMessage, color = Color(0xFFD32F2F), fontSize = 10.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // ACTION TRIGGER BUTTON FOR SECURE PAY
                            Button(
                                onClick = {
                                    if (simulatePaymentFailure) {
                                        viewModel.simulatePaymentFailure("Razorpay verification failed: Insufficient account funds")
                                    } else {
                                        viewModel.simulateRazorpayPaymentSuccess()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_razorpay_pay_now"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (simulatePaymentFailure) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (simulatePaymentFailure) "Simulate Failed Payment Transaction" 
                                    else "Authorize Secure Payment ₹" + String.format("%,.0f", activeChargeToPayNow)
                                )
                            }
                            
                            // Abort and release slot
                            TextButton(
                                onClick = {
                                    viewModel.simulatePaymentFailure("User Aborted checkout sequence.")
                                    showPaymentDialog = false
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)
                            ) {
                                Text("Abort & Relinquish Secured Slot", color = Color.Gray, fontSize = 10.sp)
                            }
                        }

                        // STEP 4: RECEIPT WITH SECURE BACKEND CODE, INVOICING, AND LAYOUT SUMMARY
                        else if (checkoutStep == "Receipt") {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle, 
                                    contentDescription = "Success", 
                                    tint = Color(0xFF007A33), 
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Vows Confirmed Permanently!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF007A33))
                                Text("Verified signature against Razorpay webhook nodes.", color = Color.Gray, fontSize = 10.sp)
                                
                                Spacer(modifier = Modifier.height(16.dp))

                                // Detailed Transaction Receipt summary box
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text("Receipt Voucher (Printable)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                                        
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Auspicious Vendor:", fontSize = 10.sp)
                                            Text(vendor.name, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Package Layout:", fontSize = 10.sp)
                                            Text(selectedBookingPackage, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Locked Date:", fontSize = 10.sp)
                                            Text(selectedBookingDate, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Secured Time Slot:", fontSize = 10.sp)
                                            Text(selectedTimeSlot, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Invoice Number:", fontSize = 10.sp)
                                            Text("WZ-2026-${(1000..9999).random()}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Razorpay Sign ID:", fontSize = 10.sp)
                                            Text("pay_" + (100000000..999999999).random(), fontSize = 9.sp, color = Color.Gray)
                                        }
                                        
                                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Amount Paid Today:", fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                                            Text(formatIndianRupee(activeChargeToPayNow), fontWeight = FontWeight.Bold, color = Color(0xFF007A33), fontSize = 11.sp)
                                        }
                                        val left = totalCharged - activeChargeToPayNow
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Remaining Balance:", fontSize = 10.sp)
                                            Text(formatIndianRupee(left), fontWeight = FontWeight.SemiBold, color = if (left > 0) Color(0xFFC62828) else Color.Gray, fontSize = 10.sp)
                                        }

                                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                                        
                                        // Commission Split visualizer in multi-vendor
                                        val commissionPct = commissionRate / 100.0
                                        val adminComm = totalCharged * commissionPct
                                        val vendorGet = totalCharged * (1.0 - commissionPct)
                                        Text("Secured Platform Payout Split:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Platform Facilitation Comm (${commissionRate.toInt()}%):", fontSize = 9.sp)
                                            Text(formatIndianRupee(adminComm), fontSize = 9.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Vendor Net Payout (${(100 - commissionRate).toInt()}%):", fontSize = 9.sp)
                                            Text(formatIndianRupee(vendorGet), fontSize = 9.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF007A33), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Automated Invoice secure PDF has been downloaded. Copy sent to: ${viewModel.currentUserName.value.lowercase()}@okwedding.com and notified Vendor dashboard.",
                                        fontSize = 9.sp,
                                        color = Color(0xFF004D40)
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = {
                                        showPaymentDialog = false
                                        viewModel.selectedVendor.value = null
                                        viewModel.checkoutVendor.value = null
                                        viewModel.checkoutStep.value = "Overview"
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Excellent: Done")
                                }
                                
                                TextButton(
                                    onClick = {
                                        showPaymentDialog = false
                                        viewModel.activeChatVendorId.value = vendor.id
                                        viewModel.activeChatVendorName.value = vendor.name
                                        viewModel.selectedVendor.value = null
                                        viewModel.checkoutVendor.value = null
                                        viewModel.checkoutStep.value = "Overview"
                                        onNavigateToChat()
                                    },
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Icon(Icons.Default.MailOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Open Coordinator Live Chat", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
