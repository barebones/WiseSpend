package com.sandesh.wisespend.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sandesh.wisespend.ui.screens.TextGray
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarWidget(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val appContext = LocalContext.current.applicationContext

    val locale = remember {
        val config = appContext.resources.configuration
        config.locales[0] ?: Locale.getDefault()
    }

    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    var expanded by remember { mutableStateOf(false) }


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous month",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { expanded = !expanded }
                ) {
                    Text(
                        text = "${
                            currentMonth.month.getDisplayName(
                                TextStyle.FULL,
                                locale
                            )
                        } ${currentMonth.year}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next month",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val dayHeaders = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dayHeaders.forEach { day ->
                    Text(
                        text = day,
                        color = TextGray,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = !expanded,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                val weekDates = getWeekContaining(selectedDate)
                CalendarRow(
                    dates = weekDates,
                    currentMonth = currentMonth,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(300)) + fadeIn(tween(200)),
                exit = shrinkVertically(tween(300)) + fadeOut(tween(200))
            ) {
                val weeks = getMonthWeeks(currentMonth)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    weeks.forEach { week ->
                        CalendarRow(
                            dates = week,
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            onDateSelected = { date ->
                                onDateSelected(date)
                                if (YearMonth.from(date) != currentMonth) {
                                    currentMonth = YearMonth.from(date)
                                }
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatSelectedDate(selectedDate),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CalendarRow(
    dates: List<LocalDate?>,
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dates.forEach { date ->
            if (date == null) {
                Box(modifier = Modifier.weight(1f).aspectRatio(1f))
            } else {
                val isSelected = date == selectedDate
                val isToday = date == LocalDate.now()
                val isCurrentMonth = YearMonth.from(date) == currentMonth

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                else -> Color.Transparent
                            }
                        )
                        .then(
                            if (isToday && !isSelected)
                                Modifier.border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    RoundedCornerShape(10.dp)
                                )
                            else Modifier
                        )
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                            isToday -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

fun getWeekContaining(date: LocalDate): List<LocalDate?> {
    val monday = date.with(DayOfWeek.MONDAY)
    return (0..6).map { monday.plusDays(it.toLong()) }
}

fun getMonthWeeks(month: YearMonth): List<List<LocalDate?>> {
    val firstOfMonth = month.atDay(1)
    val startPad = firstOfMonth.dayOfWeek.value - 1
    val totalDays = month.lengthOfMonth()
    val totalCells = startPad + totalDays
    val rows = (totalCells + 6) / 7

    return (0 until rows).map { row ->
        (0..6).map { col ->
            val offset = row * 7 + col - startPad
            firstOfMonth.plusDays(offset.toLong())
        }
    }
}

fun formatSelectedDate(date: LocalDate): String {
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val monthName = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    return "$dayName, ${date.dayOfMonth} $monthName ${date.year}"
}

@Preview(showBackground = true)
@Composable
fun CalendarWidgetPreview(){
    CalendarWidget(
        selectedDate = LocalDate.now(),
        onDateSelected = {}
    )
}
