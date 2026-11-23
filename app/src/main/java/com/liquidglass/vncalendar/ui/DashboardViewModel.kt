package com.liquidglass.vncalendar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.liquidglass.vncalendar.data.EnglishQuote
import com.liquidglass.vncalendar.data.LunarDate
import com.liquidglass.vncalendar.data.QuoteRepository
import com.liquidglass.vncalendar.data.ScheduleEvent
import com.liquidglass.vncalendar.data.ScheduleRepository
import com.liquidglass.vncalendar.data.VietnameseLunarCalendar
import com.liquidglass.vncalendar.widget.LiquidGlassWidgetProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.ZoneId

data class DashboardUiState(
    val now: ZonedDateTime,
    val lunarDate: LunarDate,
    val quote: EnglishQuote,
    val upcomingEvents: List<ScheduleEvent>,
    val hasCalendarPermission: Boolean,
    val isDarkMode: Boolean
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val zoneId = ZoneId.of("Asia/Ho_Chi_Minh")
    private val scheduleRepository = ScheduleRepository(application)

    private val _uiState = MutableStateFlow(buildInitialState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        startClockTicker()
    }

    private fun buildInitialState(): DashboardUiState {
        val now = ZonedDateTime.now(zoneId)
        val today = now.toLocalDate()
        return DashboardUiState(
            now = now,
            lunarDate = VietnameseLunarCalendar.convertSolarToLunar(
                dd = today.dayOfMonth,
                mm = today.monthValue,
                yy = today.year
            ),
            quote = QuoteRepository.quoteForDate(today),
            upcomingEvents = scheduleRepository.loadUpcomingEvents(),
            hasCalendarPermission = scheduleRepository.hasCalendarPermission(),
            isDarkMode = true
        )
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            while (true) {
                delay(1_000L)
                val now = ZonedDateTime.now(zoneId)
                val today = now.toLocalDate()
                _uiState.update { current ->
                    val dateChanged = current.now.toLocalDate() != today
                    current.copy(
                        now = now,
                        lunarDate = if (dateChanged) {
                            VietnameseLunarCalendar.convertSolarToLunar(
                                dd = today.dayOfMonth,
                                mm = today.monthValue,
                                yy = today.year
                            )
                        } else {
                            current.lunarDate
                        },
                        quote = if (dateChanged) QuoteRepository.quoteForDate(today) else current.quote
                    )
                }
            }
        }
    }

    fun shuffleQuote() {
        _uiState.update { current ->
            current.copy(quote = QuoteRepository.randomQuote(excluding = current.quote))
        }
    }

    fun toggleTheme() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun refreshEvents() {
        _uiState.update {
            it.copy(
                hasCalendarPermission = scheduleRepository.hasCalendarPermission(),
                upcomingEvents = scheduleRepository.loadUpcomingEvents()
            )
        }
        LiquidGlassWidgetProvider.updateAll(getApplication())
    }

    fun addLocalEvent(title: String, daysFromToday: Long, hour: Int, minute: Int, location: String?) {
        if (title.isBlank()) return
        val targetDate = LocalDate.now(zoneId).plusDays(daysFromToday.coerceAtLeast(0))
        scheduleRepository.addLocalEvent(title, targetDate, hour, minute, location)
        refreshEvents()
    }

    fun deleteLocalEvent(eventId: String) {
        scheduleRepository.deleteLocalEvent(eventId)
        refreshEvents()
    }
}
