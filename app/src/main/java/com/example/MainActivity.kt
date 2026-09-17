package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.screens.LiveCounterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WeeklyReportScreen
import com.example.ui.theme.ColorProductive
import com.example.ui.theme.ColorTikTokCyan
import com.example.ui.theme.ColorTikTokPink
import com.example.ui.theme.ColorWarning
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ReelScrollApp(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkAccessibilityStatus()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelScrollApp(viewModel: MainViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val weeklyReport by viewModel.weeklyReport.collectAsStateWithLifecycle()
    val todayCount by viewModel.todayReelsCount.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalReelsCount.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val isServiceActive by viewModel.isServiceActive.collectAsStateWithLifecycle()
    val isAccessibilityEnabled by viewModel.isAccessibilityEnabledInSettings.collectAsStateWithLifecycle()
    val isTikTokInForeground by viewModel.isTikTokInForeground.collectAsStateWithLifecycle()
    val activeSessionReels by viewModel.activeSessionReels.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkAccessibilityStatus()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorTikTokPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ReelScroll",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = (-0.5).sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "TIKTOK",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Quick indicator of service status
                    Surface(
                        shape = CircleShape,
                        color = if (isServiceActive || isAccessibilityEnabled)
                            ColorProductive.copy(alpha = 0.15f)
                        else
                            ColorWarning.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isServiceActive || isAccessibilityEnabled) ColorProductive else ColorWarning)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isServiceActive || isAccessibilityEnabled) "Live" else "Offline",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isServiceActive || isAccessibilityEnabled) ColorProductive else ColorWarning
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Weekly Report"
                        )
                    },
                    label = { Text("Weekly Report", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_weekly_report")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Live Counter"
                        )
                    },
                    label = { Text("Live Counter", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_live_counter")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Goals & Settings"
                        )
                    },
                    label = { Text("Goals & Guide", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    modifier = Modifier.testTag("nav_settings")
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> WeeklyReportScreen(
                report = weeklyReport,
                userSettings = userSettings,
                onPreviousWeek = { viewModel.goToPreviousWeek() },
                onNextWeek = { viewModel.goToNextWeek() },
                onCurrentWeek = { viewModel.goToCurrentWeek() },
                todayCount = todayCount,
                contentPadding = innerPadding
            )
            1 -> LiveCounterScreen(
                todayCount = todayCount,
                totalCount = totalCount,
                weeklyReport = weeklyReport,
                userSettings = userSettings,
                isServiceActive = isServiceActive,
                isAccessibilityEnabledInSettings = isAccessibilityEnabled,
                isTikTokInForeground = isTikTokInForeground,
                activeSessionReels = activeSessionReels,
                onSimulateReel = { viewModel.simulateReelScroll() },
                onSimulateBatch = { viewModel.simulateBatchReels(it) },
                onOpenAccessibilitySettings = { viewModel.openAccessibilitySettings(it) },
                onNavigateToWeeklyReport = { viewModel.selectTab(0) },
                contentPadding = innerPadding
            )
            2 -> SettingsScreen(
                userSettings = userSettings,
                onUpdateSettings = { viewModel.updateSettings(it) },
                onSeedSampleData = { viewModel.seedSampleData() },
                onClearData = { viewModel.clearAllData() },
                onOpenAccessibilitySettings = { viewModel.openAccessibilitySettings(it) },
                contentPadding = innerPadding
            )
        }
    }
}

