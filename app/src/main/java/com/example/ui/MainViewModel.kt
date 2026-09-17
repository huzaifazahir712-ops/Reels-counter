package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserSettings
import com.example.data.model.WeeklyProductivityReport
import com.example.data.repository.ReelRepository
import com.example.service.TikTokReelAccessibilityService
import com.example.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ReelRepository.getInstance(application)

    private val _selectedWeekKey = MutableStateFlow(DateUtils.getWeekKey())
    val selectedWeekKey: StateFlow<String> = _selectedWeekKey.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Weekly Report, 1: Live Counter, 2: Settings
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _isAccessibilityEnabledInSettings = MutableStateFlow(false)
    val isAccessibilityEnabledInSettings: StateFlow<Boolean> = _isAccessibilityEnabledInSettings.asStateFlow()

    val totalReelsCount: StateFlow<Int> = repository.totalReelsCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todayReelsCount: StateFlow<Int> = repository.getTodayReelsCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val userSettings: StateFlow<UserSettings> = repository.userSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val weeklyReport: StateFlow<WeeklyProductivityReport?> = _selectedWeekKey
        .flatMapLatest { weekKey ->
            repository.getWeeklyReportFlow(weekKey)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isServiceActive: StateFlow<Boolean> = TikTokReelAccessibilityService.isServiceActive
    val isTikTokInForeground: StateFlow<Boolean> = TikTokReelAccessibilityService.isTikTokInForeground
    val activeSessionReels: StateFlow<Int> = TikTokReelAccessibilityService.activeSessionReels

    init {
        checkAccessibilityStatus()
    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun checkAccessibilityStatus() {
        val enabled = TikTokReelAccessibilityService.isAccessibilitySettingsEnabled(getApplication())
        _isAccessibilityEnabledInSettings.value = enabled
    }

    fun goToPreviousWeek() {
        val prev = DateUtils.getShiftedWeekKey(_selectedWeekKey.value, -1)
        _selectedWeekKey.value = prev
    }

    fun goToNextWeek() {
        val current = DateUtils.getWeekKey()
        val next = DateUtils.getShiftedWeekKey(_selectedWeekKey.value, 1)
        // Avoid advancing beyond current week
        if (_selectedWeekKey.value < current) {
            _selectedWeekKey.value = next
        }
    }

    fun goToCurrentWeek() {
        _selectedWeekKey.value = DateUtils.getWeekKey()
    }

    fun simulateReelScroll(dwellTimeSeconds: Int = 25) {
        viewModelScope.launch {
            repository.recordReelScroll(
                sourceApp = "Interactive Simulator",
                dwellTimeSeconds = dwellTimeSeconds
            )
        }
    }

    fun simulateBatchReels(count: Int) {
        viewModelScope.launch {
            repository.recordBatchReels(count, sourceApp = "Interactive Simulator")
        }
    }

    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            repository.updateSettings(settings)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    fun seedSampleData() {
        viewModelScope.launch {
            repository.seedInitialSampleData()
        }
    }

    fun openAccessibilitySettings(context: Context) {
        TikTokReelAccessibilityService.openAccessibilitySettings(context)
    }
}
