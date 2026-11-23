package com.liquidglass.vncalendar.data

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.provider.CalendarContract
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

data class ScheduleEvent(
    val id: String,
    val title: String,
    val startMillis: Long,
    val isAllDay: Boolean,
    val location: String?,
    val isLocal: Boolean
) {
    fun formattedTime(zoneId: ZoneId): String {
        if (isAllDay) return "Cả ngày"
        val dt = Instant.ofEpochMilli(startMillis).atZone(zoneId)
        return dt.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun relativeDayLabel(today: LocalDate, zoneId: ZoneId): String {
        val eventDate = Instant.ofEpochMilli(startMillis).atZone(zoneId).toLocalDate()
        val days = ChronoUnit.DAYS.between(today, eventDate)
        return when {
            days < 0 -> "Đã qua"
            days == 0L -> "Hôm nay"
            days == 1L -> "Ngày mai"
            days == 2L -> "Ngày kia"
            else -> "Còn $days ngày"
        }
    }

    fun formattedShortDate(zoneId: ZoneId): String {
        val dt = Instant.ofEpochMilli(startMillis).atZone(zoneId)
        return dt.format(DateTimeFormatter.ofPattern("dd/MM"))
    }
}

class ScheduleRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("liquid_glass_events", Context.MODE_PRIVATE)
    private val zoneId = ZoneId.of("Asia/Ho_Chi_Minh")

    fun hasCalendarPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun loadUpcomingEvents(daysAhead: Long = 14L): List<ScheduleEvent> {
        val now = System.currentTimeMillis()
        val startOfToday = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endMillis = LocalDate.now(zoneId).plusDays(daysAhead).atTime(23, 59).atZone(zoneId).toInstant().toEpochMilli()

        val combined = mutableListOf<ScheduleEvent>()
        combined.addAll(loadLocalEvents().filter { it.startMillis in startOfToday..endMillis })

        if (hasCalendarPermission()) {
            combined.addAll(loadSystemCalendarEvents(now - 3_600_000L, endMillis))
        }

        return combined.sortedBy { it.startMillis }
    }

    private fun loadSystemCalendarEvents(startMillis: Long, endMillis: Long): List<ScheduleEvent> {
        val result = mutableListOf<ScheduleEvent>()
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)

        val projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.ALL_DAY,
            CalendarContract.Instances.EVENT_LOCATION
        )

        runCatching {
            context.contentResolver.query(
                builder.build(),
                projection,
                null,
                null,
                "${CalendarContract.Instances.BEGIN} ASC"
            )?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID)
                val titleIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
                val beginIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
                val allDayIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)
                val locIdx = cursor.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_LOCATION)

                while (cursor.moveToNext()) {
                    val title = cursor.getString(titleIdx)?.takeIf { it.isNotBlank() } ?: continue
                    val begin = cursor.getLong(beginIdx)
                    val allDay = cursor.getInt(allDayIdx) == 1
                    val loc = cursor.getString(locIdx)?.takeIf { it.isNotBlank() }
                    val id = "sys_${cursor.getLong(idIdx)}_$begin"
                    result.add(
                        ScheduleEvent(
                            id = id,
                            title = title,
                            startMillis = begin,
                            isAllDay = allDay,
                            location = loc,
                            isLocal = false
                        )
                    )
                }
            }
        }
        return result
    }

    private fun loadLocalEvents(): List<ScheduleEvent> {
        val raw = prefs.getString(KEY_EVENTS, null)
        if (raw == null) {
            val seeded = defaultStarterEvents()
            saveLocalEvents(seeded)
            return seeded
        }
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    add(
                        ScheduleEvent(
                            id = obj.getString("id"),
                            title = obj.getString("title"),
                            startMillis = obj.getLong("startMillis"),
                            isAllDay = obj.optBoolean("isAllDay", false),
                            location = obj.optString("location").takeIf { it.isNotBlank() },
                            isLocal = true
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun addLocalEvent(title: String, date: LocalDate, hour: Int, minute: Int, location: String? = null) {
        val millis = LocalDateTime.of(date.year, date.month, date.dayOfMonth, hour, minute)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()
        val updated = loadLocalEvents().toMutableList().apply {
            add(
                ScheduleEvent(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    startMillis = millis,
                    isAllDay = false,
                    location = location?.trim()?.takeIf { it.isNotEmpty() },
                    isLocal = true
                )
            )
        }
        saveLocalEvents(updated)
    }

    fun deleteLocalEvent(eventId: String) {
        val updated = loadLocalEvents().filterNot { it.id == eventId }
        saveLocalEvents(updated)
    }

    private fun saveLocalEvents(events: List<ScheduleEvent>) {
        val array = JSONArray()
        events.forEach { e ->
            array.put(
                JSONObject().apply {
                    put("id", e.id)
                    put("title", e.title)
                    put("startMillis", e.startMillis)
                    put("isAllDay", e.isAllDay)
                    put("location", e.location ?: "")
                }
            )
        }
        prefs.edit().putString(KEY_EVENTS, array.toString()).apply()
    }

    private fun defaultStarterEvents(): List<ScheduleEvent> {
        val today = LocalDate.now(zoneId)
        return listOf(
            ScheduleEvent(
                id = "seed_1",
                title = "Luyện tiếng Anh buổi sáng",
                startMillis = today.plusDays(1).atTime(7, 0).atZone(zoneId).toInstant().toEpochMilli(),
                isAllDay = false,
                location = "Ghi chú mẫu • chạm để xóa",
                isLocal = true
            ),
            ScheduleEvent(
                id = "seed_2",
                title = "Chạy bộ / Tập thể lực",
                startMillis = today.plusDays(2).atTime(17, 30).atZone(zoneId).toInstant().toEpochMilli(),
                isAllDay = false,
                location = "Công viên",
                isLocal = true
            )
        )
    }

    companion object {
        private const val KEY_EVENTS = "local_events_json"
    }
}
