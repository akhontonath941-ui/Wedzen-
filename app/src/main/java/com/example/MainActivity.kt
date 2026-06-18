package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val themeMode by viewModel.isDarkMode.collectAsState()

            MyApplicationTheme(darkTheme = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContentManager(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppContentManager(viewModel: MainViewModel) {
    val showAuth by viewModel.showAuthScreen.collectAsState()

    if (showAuth) {
        AuthScreen(viewModel = viewModel)
    } else {
        MainNavigationContainer(viewModel = viewModel)
    }
}

@Composable
fun MainNavigationContainer(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var insideChatScreen by remember { mutableStateOf(false) }

    val tabs = listOf(
        Triple("Marketplace", Icons.Default.ShoppingCart, "tab_marketplace"),
        Triple("Planning", Icons.Default.DateRange, "tab_planning"),
        Triple("AI Planner", Icons.Default.AutoAwesome, "tab_ai"),
        Triple("Expenses", Icons.Default.List, "tab_expenses"),
        Triple("Panels", Icons.Default.AccountBox, "tab_panels")
    )

    Scaffold(
        bottomBar = {
            if (!insideChatScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    tabs.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = { Icon(tab.second, contentDescription = tab.first) },
                            label = { Text(tab.first, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag(tab.third)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (insideChatScreen) PaddingValues(0.dp) else innerPadding)
        ) {
            if (insideChatScreen) {
                ChatScreen(
                    viewModel = viewModel,
                    onNavigateBack = { insideChatScreen = false }
                )
            } else {
                when (selectedTab) {
                    0 -> MarketplaceScreen(
                        viewModel = viewModel,
                        onNavigateToPlanner = { selectedTab = 2 },
                        onNavigateToChat = { insideChatScreen = true }
                    )
                    1 -> PlanningDashboardScreen(viewModel = viewModel)
                    2 -> AiPlannerScreen(viewModel = viewModel)
                    3 -> ExpenseTrackerScreen(viewModel = viewModel)
                    4 -> PanelsScreen(viewModel = viewModel)
                }
            }
        }
    }
}
