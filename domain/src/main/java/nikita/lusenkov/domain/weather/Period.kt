package nikita.lusenkov.domain.weather
sealed interface PeriodDomain {
    data object Current : PeriodDomain
    data object Today : PeriodDomain
    data class NextDays(val days: Int) : PeriodDomain
}

fun PeriodDomain.days(): Int = when (this) {
    PeriodDomain.Current, PeriodDomain.Today -> 1
    is PeriodDomain.NextDays -> this.days
}