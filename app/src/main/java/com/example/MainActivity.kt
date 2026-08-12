package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.ChannelsAndKeywordsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TelegramAuthScreen
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import com.example.ui.theme.TeleJobTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeleJobTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    TeleJobMainApp()
                }
            }
        }
    }
}

@Composable
fun TeleJobMainApp() {
    val mainViewModel: MainViewModel = viewModel()
    var selectedScreenIndex by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        NavItem("الوظائف", Icons.Default.WorkHistory),
        NavItem("ربط تلغرام", Icons.Default.Send),
        NavItem("القنوات والكلمات", Icons.Default.Group),
        NavItem("الإعدادات", Icons.Default.Settings)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CyberSurface,
        bottomBar = {
            NavigationBar(
                containerColor = CyberSurface,
                contentColor = CyberPrimary
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedScreenIndex == index,
                        onClick = { selectedScreenIndex = index },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = {
                            Text(
                                text = item.label,
                                fontWeight = if (selectedScreenIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CyberPrimary,
                            indicatorColor = CyberPrimary,
                            unselectedIconColor = CyberTextSecondary,
                            unselectedTextColor = CyberTextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (selectedScreenIndex) {
            0 -> HomeScreen(viewModel = mainViewModel, onNavigateToAuth = { selectedScreenIndex = 1 }, modifier = modifier)
            1 -> TelegramAuthScreen(viewModel = mainViewModel, modifier = modifier)
            2 -> ChannelsAndKeywordsScreen(viewModel = mainViewModel, modifier = modifier)
            3 -> SettingsScreen(viewModel = mainViewModel, modifier = modifier)
        }
    }
}

private data class NavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
