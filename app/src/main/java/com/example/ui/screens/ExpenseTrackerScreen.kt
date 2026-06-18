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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerScreen(viewModel: MainViewModel) {
    val expenses by viewModel.expenses.collectAsState()
    val details by viewModel.weddingDetails.collectAsState()

    val totalBudget = details?.totalBudget ?: 1500000.0
    val totalSpent = expenses.sumOf { it.amount }

    // Aggregate values
    val venueSpent = expenses.filter { it.category == "Venue" }.sumOf { it.amount }
    val cateringSpent = expenses.filter { it.category == "Catering" }.sumOf { it.amount }
    val makeupSpent = expenses.filter { it.category == "Makeup" }.sumOf { it.amount }
    val otherSpent = expenses.filter { it.category != "Venue" && it.category != "Catering" && it.category != "Makeup" }.sumOf { it.amount }

    val venueRatio = if (totalSpent > 0) (venueSpent / totalSpent).toFloat() else 0f
    val cateringRatio = if (totalSpent > 0) (cateringSpent / totalSpent).toFloat() else 0f
    val makeupRatio = if (totalSpent > 0) (makeupSpent / totalSpent).toFloat() else 0f
    val otherRatio = if (totalSpent > 0) (otherSpent / totalSpent).toFloat() else 0f

    // Inputs
    val titleInput by viewModel.expenseTitleInput.collectAsState()
    val amtInput by viewModel.expenseAmountInput.collectAsState()
    val catInput by viewModel.expenseCategorySelected.collectAsState()
    val dateInput by viewModel.expenseDateInput.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var isSimulatedCameraUploaded by remember { mutableStateOf(false) }

    val categories = listOf("Venue", "Catering", "Makeup", "Decoration", "Jewelry", "Traditional", "Others")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Appbar header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Wedding Expense Logs",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Real-time cost trackers & analytics charts",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("add_expense_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Bill", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Budget summary analytics card
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
                    text = "Auspicious Expense Analysis",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Aggregate Spent", fontSize = 11.sp, color = Color.Gray)
                        Text(formatIndianRupee(totalSpent), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Capital Limit", fontSize = 11.sp, color = Color.Gray)
                        Text(formatIndianRupee(totalBudget), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Section breakdown bars
                Text(text = "Expense Partition by Departments", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(8.dp))

                // Venue bar
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Venues & Hotels", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text("${(venueRatio * 100).toInt()}% • ${formatIndianRupee(venueSpent)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { venueRatio },
                        color = Color(0xFFC62828), // Deep Red
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }

                // Catering bar
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Catering Food & Beverages", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text("${(cateringRatio * 100).toInt()}% • ${formatIndianRupee(cateringSpent)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { cateringRatio },
                        color = Color(0xFFFF8F00), // Amber
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }

                // Makeup bar
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Studios & Makeup Artists", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text("${(makeupRatio * 100).toInt()}% • ${formatIndianRupee(makeupSpent)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { makeupRatio },
                        color = Color(0xFFAD1457), // Hot Pink/Magenta
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }

                // Other bar
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Other Services & Gold", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Text("${(otherRatio * 100).toInt()}% • ${formatIndianRupee(otherSpent)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = { otherRatio },
                        color = Color(0xFF2E7D32), // Green
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }

        // List of past manual logs & digital inputs
        Text(
            text = "Receipt Transaction Logs (${expenses.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        if (expenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.LightGray)
                    Text("No billing entries loaded yet.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                expenses.forEach { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Category Icon mapping
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (item.category) {
                                            "Venue" -> Icons.Default.LocationOn
                                            "Catering" -> Icons.Default.ShoppingCart
                                            "Makeup" -> Icons.Default.Star
                                            else -> Icons.Default.Favorite
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = item.category, fontSize = 10.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "•  ${item.date}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = formatIndianRupee(item.amount),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                IconButton(
                                    onClick = { viewModel.deleteExpense(item.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // EXPENSE ADDITION MODAL DIALOG
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
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
                    Text(
                        text = "Add Wedding Expense Bill",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { viewModel.expenseTitleInput.value = it },
                        label = { Text("Expense Item Description") },
                        modifier = Modifier.fillMaxWidth().testTag("add_expense_title")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = amtInput,
                        onValueChange = { viewModel.expenseAmountInput.value = it },
                        label = { Text("Amount (INR)") },
                        modifier = Modifier.fillMaxWidth().testTag("add_expense_amount")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dateInput,
                        onValueChange = { viewModel.expenseDateInput.value = it },
                        label = { Text("Payment Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated Receipt uploading
                    Button(
                        onClick = { isSimulatedCameraUploaded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSimulatedCameraUploaded) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (isSimulatedCameraUploaded) Icons.Default.CheckCircle else Icons.Default.Star,
                            contentDescription = null,
                            tint = if (isSimulatedCameraUploaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSimulatedCameraUploaded) "Simulated Digital Receipt Attached!" else "Upload Bill Receipt (Camera Scan)",
                            color = if (isSimulatedCameraUploaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Accounting Category", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    categories.forEach { cat ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (catInput == cat),
                                onClick = { viewModel.expenseCategorySelected.value = cat }
                            )
                            Text(text = "$cat Department", fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.addExpense()
                            isSimulatedCameraUploaded = false
                            showAddDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_expense_row")
                    ) {
                        Text("Add Record to Audit Ledger")
                    }
                }
            }
        }
    }
}
