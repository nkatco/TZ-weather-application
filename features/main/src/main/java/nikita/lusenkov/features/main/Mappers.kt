package nikita.lusenkov.features.main

import nikita.lusenkov.domain.weather.PeriodDomain

fun PeriodUi.toDomain(): PeriodDomain = when (this) {
    PeriodUi.Current -> PeriodDomain.Current
    PeriodUi.Today   -> PeriodDomain.Today
    PeriodUi.Three   -> PeriodDomain.NextDays(3)
}