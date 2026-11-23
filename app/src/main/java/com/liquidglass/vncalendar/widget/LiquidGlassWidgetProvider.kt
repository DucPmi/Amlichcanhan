package com.liquidglass.vncalendar.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.liquidglass.vncalendar.MainActivity
import com.liquidglass.vncalendar.R
import com.liquidglass.vncalendar.data.QuoteRepository
import com.liquidglass.vncalendar.data.ScheduleRepository
import com.liquidglass.vncalendar.data.VietnameseLunarCalendar
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class LiquidGlassWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateSingleWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_REFRESH,
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> updateAll(context)
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.liquidglass.vncalendar.ACTION_REFRESH_WIDGET"

        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, LiquidGlassWidgetProvider::class.java)
            )
            ids.forEach { updateSingleWidget(context, manager, it) }
            LockScreenCompactWidgetProvider.updateAll(context)
        }

        private fun updateSingleWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val zoneId = ZoneId.of("Asia/Ho_Chi_Minh")
            val now = ZonedDateTime.now(zoneId)
            val today = now.toLocalDate()
            val lunar = VietnameseLunarCalendar.convertSolarToLunar(
                dd = today.dayOfMonth,
                mm = today.monthValue,
                yy = today.year
            )
            val quote = QuoteRepository.quoteForDate(today)
            val nextEvent = ScheduleRepository(context).loadUpcomingEvents().firstOrNull()

            val views = RemoteViews(context.packageName, R.layout.widget_liquid_glass)

            val solarText = "${weekdayFull(now.dayOfWeek)}, %02d/%02d/%d".format(
                today.dayOfMonth, today.monthValue, today.year
            )
            val lunarText = buildString {
                append("ÂL: ${lunar.formattedDate} • ${lunar.yearCanChi}")
                lunar.specialLabel?.let { append(" ($it)") }
            }

            views.setTextViewText(R.id.widget_solar_date, solarText)
            views.setTextViewText(R.id.widget_lunar_date, lunarText)

            if (nextEvent != null) {
                views.setTextViewText(
                    R.id.widget_next_event_badge,
                    nextEvent.relativeDayLabel(today, zoneId)
                )
                views.setTextViewText(
                    R.id.widget_next_event,
                    "${nextEvent.formattedTime(zoneId)} • ${nextEvent.title}"
                )
            } else {
                views.setTextViewText(R.id.widget_next_event_badge, "Lịch")
                views.setTextViewText(R.id.widget_next_event, "Chưa có sự kiện sắp tới")
            }

            views.setTextViewText(
                R.id.widget_quote,
                "“${quote.text}” — ${quote.author}"
            )

            // Chạm vào widget mở app chính
            val openAppIntent = Intent(context, MainActivity::class.java)
            val openPending = PendingIntent.getActivity(
                context,
                appWidgetId,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, openPending)

            // Chạm nút làm mới cập nhật lại widget ngay lập tức
            val refreshIntent = Intent(context, LiquidGlassWidgetProvider::class.java).apply {
                action = ACTION_REFRESH
            }
            val refreshPending = PendingIntent.getBroadcast(
                context,
                appWidgetId + 1000,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_refresh_btn, refreshPending)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        internal fun weekdayFull(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Thứ Hai"
            DayOfWeek.TUESDAY -> "Thứ Ba"
            DayOfWeek.WEDNESDAY -> "Thứ Tư"
            DayOfWeek.THURSDAY -> "Thứ Năm"
            DayOfWeek.FRIDAY -> "Thứ Sáu"
            DayOfWeek.SATURDAY -> "Thứ Bảy"
            DayOfWeek.SUNDAY -> "Chủ Nhật"
        }

        internal fun weekdayShort(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
            DayOfWeek.MONDAY -> "T2"
            DayOfWeek.TUESDAY -> "T3"
            DayOfWeek.WEDNESDAY -> "T4"
            DayOfWeek.THURSDAY -> "T5"
            DayOfWeek.FRIDAY -> "T6"
            DayOfWeek.SATURDAY -> "T7"
            DayOfWeek.SUNDAY -> "CN"
        }
    }
}
