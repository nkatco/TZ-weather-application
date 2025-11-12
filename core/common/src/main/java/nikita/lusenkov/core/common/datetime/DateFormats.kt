package nikita.lusenkov.core.common.datetime

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

object DateFormats {
    val ru: Locale = Locale("ru")
    @RequiresApi(Build.VERSION_CODES.O)
    val day: DateTimeFormatter  = DateTimeFormatter.ofPattern("d MMMM").withLocale(ru)
    val hour: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm").withLocale(ru)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDayLabel(date: LocalDate, clock: Clock, locale: Locale = DateFormats.ru): String {
    val today = LocalDate.now(clock)
    return when {
        date == today -> "Сегодня"
        date == today.plusDays(1) -> "Завтра"
        else -> DateFormats.day.withLocale(locale).format(date)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatHour(dt: LocalDateTime, locale: Locale = DateFormats.ru): String =
    dt.format(DateFormats.hour.withLocale(locale))
