package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.WeddingDetails
import com.example.ui.theme.HeritageGoldAccent
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanningDashboardScreen(viewModel: MainViewModel) {
    val details by viewModel.weddingDetails.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val checklist by viewModel.checklistItem.collectAsState()
    val goldPrice by viewModel.goldPriceRate.collectAsState()

    // Config Input States
    var showConfigDialog by remember { mutableStateOf(false) }
    var brideInput by remember { mutableStateOf("") }
    var groomInput by remember { mutableStateOf("") }
    var locationInput by remember { mutableStateOf("") }
    var budgetInput by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("") }
    var categorySelected by remember { mutableStateOf("Hindu") }

    // Collaborator Input States
    var showCollabDialog by remember { mutableStateOf(false) }
    var collabNameInput by remember { mutableStateOf("") }

    // Computations
    val totalBudget = details?.totalBudget ?: 1500000.0
    val totalSpent = expenses.sumOf { it.amount } + bookings.sumOf { it.price * 0.40 } // total booked deposits + raw manual costs
    val remainingBudget = maxOf(0.0, totalBudget - totalSpent)
    val budgetRatio = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f

    // EMI Computations
    val emiPrinicpal by viewModel.emiApprovedAmount.collectAsState()
    val emiMonths by viewModel.emiMonthsSelected.collectAsState()

    // Monthly EMI formula: [P x R x (1+R)^N]/[(1+R)^N-1]
    // where P = principal, R = monthly interest (10.5% annual / 12 months = 0.00875), N = tenure months
    val monthlyRate = 0.105 / 12
    val compound = Math.pow(1 + monthlyRate, emiMonths.toDouble())
    val emiMonthlyInstallment = if (compound > 1) (emiPrinicpal * monthlyRate * compound) / (compound - 1) else 0.0

    val timelines = listOf(
        Triple("Shree Ganesh Puja", "Morning 09:00 AM - Auspicious start", "Day -1"),
        Triple("Sangeet & Mehendi Beats", "Evening 06:30 PM - Music & Henna", "Day -1"),
        Triple("Sacred Haldi Ceremony", "Morning 10:00 AM - Yellow celebrations", "Wedding Day"),
        Triple("Vedic Wedding Saat Pheras", "Evening 07:00 PM - Main Vows & Rituals", "Wedding Day"),
        Triple("Grand Reception Gala Dinner", "Evening 08:30 PM - Royal feast", "Day +1")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp) // padding to clear bottom navigation
    ) {
        // Grand Dashboard Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = "${details?.brideName ?: "Ananya"} & ${details?.groomName ?: "Rohan"}'s Wedding",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = " ${details?.location ?: "Udaipur, Rajasthan"} • ${details?.weddingDate ?: "Nov 28, 2026"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Configure Button
                Button(
                    onClick = {
                        brideInput = details?.brideName ?: "Ananya"
                        groomInput = details?.groomName ?: "Rohan"
                        locationInput = details?.location ?: "Udaipur"
                        budgetInput = (details?.totalBudget ?: 1500000.0).toString()
                        dateInput = details?.weddingDate ?: "2026-11-28"
                        categorySelected = details?.weddingType ?: "Hindu"
                        showConfigDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                    modifier = Modifier.height(34.dp).testTag("config_wedding_details")
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Project Properties", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Budget spent progression meter
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Auspicious Financial Dashboard",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Capital Budget Allocation", fontSize = 11.sp, color = Color.Gray)
                        Text(formatIndianRupee(totalBudget), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Bookings spent / Deposits", fontSize = 11.sp, color = Color.Gray)
                        Text(formatIndianRupee(totalSpent), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.secondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { budgetRatio.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.LightGray.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                        Text(
                            text = " Leftovers: ${formatIndianRupee(remainingBudget)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Text(
                        text = "${(budgetRatio * 100).toInt()}% Used",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Indian Gold & Silver Updates
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = HeritageGoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Auspicious Gold Rate (24K)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            color = Color(0xFF2E7D32),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${formatIndianRupee(goldPrice)} / 10g",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Last updated: Today UTC",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "💡 Advice: Gold prices dropped by 0.4% today. Ideal Muhurtham moment to buy wedding jewelry ornaments and coins!",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }

        // Multi-month EMI Calculator Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WedZen Wedding Financing & EMI Calculator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // EMI Principal Slider Input
                Text(
                    text = "Configure Borrow / Budget Loan Capital: ${formatIndianRupee(emiPrinicpal)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Slider(
                    value = emiPrinicpal.toFloat(),
                    onValueChange = { viewModel.emiApprovedAmount.value = it.toDouble() },
                    valueRange = 100000f..200000f, // 1 Lakh to 20 Lakhs
                    steps = 19,
                    modifier = Modifier.testTag("emi_principal_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("₹100,000 Minimum", fontSize = 10.sp, color = Color.Gray)
                    Text("₹2,000,000 Maximum", fontSize = 10.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Month toggles
                Text(
                    text = "Select Loan Repayment Tenure (Months)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val monthsList = listOf(6, 12, 18, 24, 36)
                    monthsList.forEach { m ->
                        val isSel = (emiMonths == m)
                        OutlinedButton(
                            onClick = { viewModel.emiMonthsSelected.value = m },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .height(32.dp)
                        ) {
                            Text(
                                "$m M",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Final breakdown results
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Simulated Interest rate (Compounded):", fontSize = 11.sp, color = Color.Gray)
                            Text("10.5% Annual APR", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Monthly Installment EMI:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = "${formatIndianRupee(emiMonthlyInstallment)} / Month",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Checklist snapshot
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Auspicious Action Milestones",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                checklist.take(4).forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleChecklistItem(item.id, !item.isCompleted) }
                            .padding(vertical = 4.dp)
                    ) {
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
                }
            }
        }

        // Family Collaboration Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Family Collaboration Council",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Button(
                        onClick = { showCollabDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(30.dp)
                            .testTag("add_collaborator_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Member", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Current planning squad (voters and booking auditors):",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    text = details?.familyMembers ?: "No family members added yet.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Text(
                    text = "💡 Invite links allow relatives to vote on menu options, shortlists, and comment on the cost tracker synchronously.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    lineHeight = 15.sp
                )
            }
        }

        // Grand Event timeline
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Ritual Timeline Sequence",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                timelines.forEachIndexed { idx, timeline ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(end = 12.dp, top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            if (idx < timelines.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(40.dp)
                                        .background(Color.Gray.copy(alpha = 0.5f))
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = timeline.third,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = timeline.first,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = timeline.second,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Active Booking list (Audit tracker)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Auspicious Bookings Logs (${bookings.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                if (bookings.isEmpty()) {
                    Text(
                        text = "No vendors booked yet. Visit the Marketplace to rent venue, catering and priests!",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    bookings.forEach { booking ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = booking.vendorCategory.uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = booking.vendorName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Invoice: ${booking.invoiceNumber}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "Scheduled For: ${booking.bookingDate}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = formatIndianRupee(booking.price),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp
                                    )
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                                    ) {
                                        Text(
                                            text = "Cancel Contract",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Red,
                                            modifier = Modifier
                                                .clickable { viewModel.cancelActiveBooking(booking.id) }
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
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

    // PROJECT CONFIGURATION DIALOG
    if (showConfigDialog) {
        Dialog(onDismissRequest = { showConfigDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = "Modify Wedding Project", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = brideInput,
                        onValueChange = { brideInput = it },
                        label = { Text("Bride's Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = groomInput,
                        onValueChange = { groomInput = it },
                        label = { Text("Groom's Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = locationInput,
                        onValueChange = { locationInput = it },
                        label = { Text("Wedding Destination / Venue Location") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { budgetInput = it },
                        label = { Text("Allocated Capital Budget (INR)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dateInput,
                        onValueChange = { dateInput = it },
                        label = { Text("Ceremony Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Tradition Heritage Template", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    val weddingTypes = listOf("Hindu", "Muslim", "Assamese", "Bengali", "Punjabi", "South Indian", "Christian")
                    weddingTypes.forEach { type ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = (categorySelected == type), onClick = { categorySelected = type })
                            Text(text = "$type Style Ceremony", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.updateWeddingDetails(
                                bride = brideInput,
                                groom = groomInput,
                                date = dateInput,
                                location = locationInput,
                                budget = budgetInput.toDoubleOrNull() ?: 1500000.0,
                                type = categorySelected
                            )
                            showConfigDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_wedding_details_edit")
                    ) {
                        Text("Save & Update Ceremonies")
                    }
                }
            }
        }
    }

    // FAMILY COLLABORATOR DIALOG
    if (showCollabDialog) {
        Dialog(onDismissRequest = { showCollabDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Add Collaborating Member", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Assign voting rights to organize shortlists and budgets.", fontSize = 11.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = collabNameInput,
                        onValueChange = { collabNameInput = it },
                        label = { Text("Relative Name (e.g. Mami Ji)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("collab_name_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (collabNameInput.isNotEmpty()) {
                                viewModel.addFamilyCollaborator(collabNameInput)
                                collabNameInput = ""
                                showCollabDialog = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_collab_member")
                    ) {
                        Text("Join Wedding Planner")
                    }
                }
            }
        }
    }
}
