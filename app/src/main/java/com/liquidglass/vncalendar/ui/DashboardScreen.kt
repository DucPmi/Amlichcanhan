package com.liquidglass.vncalendar.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liquidglass.vncalendar.data.ScheduleEvent
import com.liquidglass.vncalendar.ui.components.LiquidAmbientBackground
import com.liquidglass.vncalendar.ui.components.LiquidGlassCard
import com.liquidglass.vncalendar.ui.components.LiquidGlassPill
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LiquidGlassDashboardScreen(viewModel: DashboardViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val zoneId = remember { ZoneId.of("Asia/Ho_Chi_Minh") }

    var showAddDialog by remember { mutableStateOf(false) }

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.refreshEvents()
        if (granted) {
            Toast.makeText(context, "Đã đồng bộ lịch trên máy", Toast.LENGTH_SHORT).show()
        }
    }

    val isDark = state.isDarkMode
    val primaryText = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
    val secondaryText = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
    val accentCyan = if (isDark) Color(0xFF7DD3FC) else Color(0xFF0284C7)
    val accentAmber = if (isDark) Color(0xFFFDE68A) else Color(0xFFD97706)

    LiquidAmbientBackground(isDark = isDark) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = vietnameseGreeting(state.now.hour),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = secondaryText
                    )
                    Text(
                        text = "Hôm nay của bạn",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryText
                    )
                }

                LiquidGlassPill(
                    isDark = isDark,
                    onClick = { viewModel.toggleTheme() }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDark) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = "Đổi giao diện",
                            tint = primaryText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isDark) "Sáng" else "Tối",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = primaryText
                        )
                    }
                }
            }

            LiquidGlassCard(isDark = isDark, modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = state.now.format(DateTimeFormatter.ofPattern("HH:mm")),
                                fontSize = 54.sp,
                                fontWeight = FontWeight.Light,
                                letterSpacing = (-1.5).sp,
                                color = primaryText
                            )
                            Text(
                                text = state.now.format(DateTimeFormatter.ofPattern(":ss")),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                color = secondaryText,
                                modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
                            )
                        }

                        LiquidGlassPill(isDark = isDark, accentColor = accentCyan) {
                            Text(
                                text = "UTC+7 VN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryText
                            )
                        }
                    }

                    Text(
                        text = "${vietnameseWeekday(state.now.dayOfWeek)}, ${state.now.dayOfMonth} tháng ${state.now.monthValue}, ${state.now.year}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryText
                    )

                    HorizontalDivider(
                        color = if (isDark) Color.White.copy(alpha = 0.14f) else Color.Black.copy(alpha = 0.08f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(accentAmber.copy(alpha = if (isDark) 0.22f else 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.NightsStay,
                                    contentDescription = "Lịch Âm",
                                    tint = accentAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "ÂM LỊCH VIỆT NAM",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp,
                                    color = secondaryText
                                )
                                Text(
                                    text = "${state.lunarDate.formattedDate} • Năm ${state.lunarDate.yearCanChi}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = primaryText
                                )
                                Text(
                                    text = "Ngày ${state.lunarDate.dayCanChi} • Tháng ${state.lunarDate.monthCanChi}",
                                    fontSize = 12.sp,
                                    color = secondaryText
                                )
                            }
                        }
                    }

                    state.lunarDate.specialLabel?.let { label ->
                        LiquidGlassPill(isDark = isDark, accentColor = accentAmber) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryText
                            )
                        }
                    }
                }
            }

            LiquidGlassCard(isDark = isDark, modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.FormatQuote,
                                contentDescription = null,
                                tint = accentCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DAILY QUOTE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.1.sp,
                                color = secondaryText
                            )
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    val full = "“${state.quote.text}” — ${state.quote.author}"
                                    clipboard.setText(AnnotatedString(full))
                                    Toast.makeText(context, "Đã sao chép quote", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentCopy,
                                    contentDescription = "Sao chép",
                                    tint = secondaryText,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.shuffleQuote() },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = "Đổi quote",
                                    tint = primaryText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    AnimatedContent(
                        targetState = state.quote,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "QuoteTransition"
                    ) { quote ->
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "“${quote.text}”",
                                fontSize = 17.sp,
                                lineHeight = 25.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Normal,
                                color = primaryText
                            )
                            Text(
                                text = "— ${quote.author}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = accentCyan
                            )
                        }
                    }
                }
            }

            LiquidGlassCard(isDark = isDark, modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                tint = accentCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LỊCH SẮP TỚI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.1.sp,
                                color = secondaryText
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!state.hasCalendarPermission) {
                                LiquidGlassPill(
                                    isDark = isDark,
                                    accentColor = accentCyan,
                                    onClick = {
                                        calendarPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
                                    }
                                ) {
                                    Text(
                                        text = "Đồng bộ lịch",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = primaryText
                                    )
                                }
                            }

                            LiquidGlassPill(
                                isDark = isDark,
                                onClick = { showAddDialog = true }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.Add,
                                        contentDescription = "Thêm lịch",
                                        tint = primaryText,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Thêm",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = primaryText
                                    )
                                }
                            }
                        }
                    }

                    if (state.upcomingEvents.isEmpty()) {
                        Text(
                            text = "Chưa có sự kiện nào trong 14 ngày tới. Nhấn 'Thêm' hoặc 'Đồng bộ lịch' để hiển thị.",
                            fontSize = 13.sp,
                            color = secondaryText,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        val today = state.now.toLocalDate()
                        state.upcomingEvents.take(8).forEachIndexed { idx, event ->
                            EventRow(
                                event = event,
                                today = today,
                                zoneId = zoneId,
                                isDark = isDark,
                                primaryText = primaryText,
                                secondaryText = secondaryText,
                                accentColor = if (event.isLocal) accentAmber else accentCyan,
                                onDelete = if (event.isLocal) {
                                    { viewModel.deleteLocalEvent(event.id) }
                                } else null
                            )
                            if (idx < state.upcomingEvents.take(8).lastIndex) {
                                HorizontalDivider(
                                    color = if (isDark) Color.White.copy(alpha = 0.09f) else Color.Black.copy(alpha = 0.06f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showAddDialog) {
        AddEventDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, daysOffset, hour, minute, loc ->
                viewModel.addLocalEvent(title, daysOffset, hour, minute, loc)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun EventRow(
    event: ScheduleEvent,
    today: LocalDate,
    zoneId: ZoneId,
    isDark: Boolean,
    primaryText: Color,
    secondaryText: Color,
    accentColor: Color,
    onDelete: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(accentColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = primaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildString {
                    append("${event.formattedShortDate(zoneId)} • ${event.formattedTime(zoneId)}")
                    if (!event.location.isNullOrBlank()) {
                        append(" • ${event.location}")
                    }
                },
                fontSize = 12.sp,
                color = secondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        LiquidGlassPill(isDark = isDark) {
            Text(
                text = event.relativeDayLabel(today, zoneId),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = primaryText
            )
        }
        if (onDelete != null) {
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Xóa sự kiện",
                    tint = secondaryText,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

@Composable
private fun AddEventDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, daysFromToday: Long, hour: Int, minute: Int, location: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var daysOffset by remember { mutableLongStateOf(0L) }
    var hour by remember { mutableIntStateOf(9) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm lịch sắp tới") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tên sự kiện") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Địa điểm / Ghi chú (tuỳ chọn)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ngày: " + when (daysOffset) {
                        0L -> "Hôm nay"
                        1L -> "Ngày mai"
                        else -> "+$daysOffset ngày"
                    })
                    Row {
                        TextButton(onClick = { if (daysOffset > 0) daysOffset-- }) { Text("-1 ngày") }
                        TextButton(onClick = { daysOffset++ }) { Text("+1 ngày") }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Giờ: %02d:00".format(hour))
                    Row {
                        TextButton(onClick = { hour = (hour + 23) % 24 }) { Text("-1 giờ") }
                        TextButton(onClick = { hour = (hour + 1) % 24 }) { Text("+1 giờ") }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(title, daysOffset, hour, 0, location) },
                enabled = title.isNotBlank()
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

private fun vietnameseWeekday(dayOfWeek: DayOfWeek): String = when (dayOfWeek) {
    DayOfWeek.MONDAY -> "Thứ Hai"
    DayOfWeek.TUESDAY -> "Thứ Ba"
    DayOfWeek.WEDNESDAY -> "Thứ Tư"
    DayOfWeek.THURSDAY -> "Thứ Năm"
    DayOfWeek.FRIDAY -> "Thứ Sáu"
    DayOfWeek.SATURDAY -> "Thứ Bảy"
    DayOfWeek.SUNDAY -> "Chủ Nhật"
}

private fun vietnameseGreeting(hour: Int): String = when (hour) {
    in 5..10 -> "Chào buổi sáng"
    in 11..13 -> "Chào buổi trưa"
    in 14..17 -> "Chào buổi chiều"
    else -> "Chào buổi tối"
}
