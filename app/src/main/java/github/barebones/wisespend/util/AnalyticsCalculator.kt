package github.barebones.wisespend.util

import github.barebones.wisespend.data.model.Expense
import github.barebones.wisespend.ui.components.DayValue
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

enum class AnalyticsRange(val label: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year")
}

data class AnalyticsResult(
    val points: List<DayValue>,
    val total: Double,
    val average: Double,
    val selectedIndex: Int
)

object AnalyticsCalculator {

    private val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-M-d")

    private fun String.toLocalDate(): LocalDate =
        LocalDate.parse(this, dateFormatter)

    fun compute(expenses: List<Expense>, range: AnalyticsRange): AnalyticsResult {
        return when (range) {
            AnalyticsRange.DAY -> computeCurrentDayByHour(expenses)
            AnalyticsRange.WEEK -> computeCurrentWeek(expenses)
            AnalyticsRange.MONTH -> computeCurrentMonthByWeek(expenses)
            AnalyticsRange.YEAR -> computeCurrentYearByMonth(expenses)
        }
    }

    // Hourly spending for the current day
    private fun computeCurrentDayByHour(expenses: List<Expense>): AnalyticsResult {
        val today = LocalDate.now()
        val hours = (0..23).toList()

        val sums = hours.associateWith { hour ->
            expenses.filter {
                it.date.toLocalDate() == today &&
                        it.time?.substringBefore(":")?.toIntOrNull() == hour
            }.sumOf { it.amount }
        }

        val currentHour = java.time.LocalTime.now().hour

        return buildResult(
            labels = hours.map { String.format(Locale.getDefault(), "%02d", it) },
            values = hours.map { sums[it] ?: 0.0 },
            selectedIndex = hours.indexOf(currentHour).coerceAtLeast(0)
        )
    }

    // Last 7 calendar days, one bar per day "labeled by weekday initial"
    private fun computeLastNDays(expenses: List<Expense>, days: Int): AnalyticsResult {
        val today = LocalDate.now()
        val dateRange = (days - 1 downTo 0).map { today.minusDays(it.toLong()) }

        val sums = dateRange.associateWith { date ->
            expenses.filter { it.date.toLocalDate() == date }.sumOf { it.amount }
        }

        return buildResult(
            labels = dateRange.map { it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(3) },
            values = dateRange.map { sums[it] ?: 0.0 },
            selectedIndex = dateRange.indexOf(today).coerceAtLeast(0)
        )
    }

    // Sun...Sat of the current week
    private fun computeCurrentWeek(expenses: List<Expense>): AnalyticsResult {
        val today = LocalDate.now()
        // most recent Sunday on/before today
        val daysSinceSunday = today.dayOfWeek.value % 7
        val sunday = today.minusDays(daysSinceSunday.toLong())
        val weekDates = (0..6).map { sunday.plusDays(it.toLong()) }

        val sums = weekDates.associateWith { date ->
            expenses.filter { it.date.toLocalDate() == date }.sumOf { it.amount }
        }

        return buildResult(
            labels = weekDates.map { it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(3) },
            values = weekDates.map { sums[it] ?: 0.0 },
            selectedIndex = weekDates.indexOf(today).coerceAtLeast(0)
        )
    }

    // Weeks 1-5 of the current month
    private fun computeCurrentMonthByWeek(expenses: List<Expense>): AnalyticsResult {
        val today = LocalDate.now()
        val firstOfMonth = today.withDayOfMonth(1)
        val lastOfMonth = today.withDayOfMonth(today.lengthOfMonth())

        val weekBuckets = mutableListOf<Pair<LocalDate, LocalDate>>()
        var cursor = firstOfMonth
        while (!cursor.isAfter(lastOfMonth)) {
            val weekEnd = minOf(cursor.plusDays(6), lastOfMonth)
            weekBuckets.add(cursor to weekEnd)
            cursor = weekEnd.plusDays(1)
        }

        val sums = weekBuckets.map { (start, end) ->
            expenses.filter { val d = it.date.toLocalDate(); !d.isBefore(start) && !d.isAfter(end) }
                .sumOf { it.amount }
        }

        val selectedIndex = weekBuckets.indexOfFirst { (start, end) ->
            !today.isBefore(start) && !today.isAfter(end)
        }.coerceAtLeast(0)

        return buildResult(
            labels = weekBuckets.mapIndexed { i, _ -> "W${i + 1}" },
            values = sums,
            selectedIndex = selectedIndex
        )
    }

    // Jan...Dec of the current year
    private fun computeCurrentYearByMonth(expenses: List<Expense>): AnalyticsResult {
        val today = LocalDate.now()
        val months = (1..12).map { today.withMonth(it).withDayOfMonth(1) }

        val sums = months.map { monthStart ->
            expenses.filter {
                val d = it.date.toLocalDate()
                d.year == monthStart.year && d.month == monthStart.month
            }.sumOf { it.amount }
        }

        return buildResult(
            labels = months.map { it.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(3) },
            values = sums,
            selectedIndex = (today.monthValue - 1).coerceIn(0, 11)
        )
    }

    private fun buildResult(labels: List<String>, values: List<Double>, selectedIndex: Int): AnalyticsResult {
        val maxValue = values.maxOrNull()?.takeIf { it > 0 } ?: 1.0
        val points = labels.mapIndexed { i, label ->
            DayValue(
                label = label,
                value = values[i].toInt(),
                heightFraction = (values[i] / maxValue).toFloat().coerceIn(0.04f, 1f)
            )
        }
        val total = values.sum()
        val nonZeroCount = values.count { it > 0 }.coerceAtLeast(1)
        return AnalyticsResult(
            points = points,
            total = total,
            average = total / nonZeroCount,
            selectedIndex = selectedIndex
        )
    }
}