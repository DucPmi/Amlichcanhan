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
import com.liquidglass.vncalendar.data.VietnameseLunarCalendar
import java.time.ZoneId
import java.time.ZonedDateTime

class LockScreenCompactWidgetProvider : AppWidgetProvider() {

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
            LiquidGlassWidgetProvider.ACTION_REFRESH,
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> updateAll(context)
        }
    }

    companion object {
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, LockScreenCompactWidgetProvider::class.java)
            )
            ids.forEach { updateSingleWidget(context, manager, it) }
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

            val views = RemoteViews(context.packageName, R.layout.widget_lock_compact)

            val solarText = "${LiquidGlassWidgetProvider.weekdayShort(now.dayOfWeek)}, %02d/%02d/%d".format(
                today.dayOfMonth, today.monthValue, today.year
            )
            val lunarBadge = lunar.specialLabel?.let {
                "ÂL ${lunar.compactDate} • $it"
            } ?: "ÂL ${lunar.compactDate} • ${lunar.yearCanChi}"

            views.setTextViewText(R.id.compact_solar_date, solarText)
            views.setTextViewText(R.id.compact_lunar_pill, lunarBadge)
            views.setTextViewText(
                R.id.compact_quote,
                "“${quote.text}” — ${quote.author}"
            )

            val openIntent = Intent(context, MainActivity::class.java)
            val pending = PendingIntent.getActivity(
                context,
                appWidgetId + 2000,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.compact_widget_root, pending)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
