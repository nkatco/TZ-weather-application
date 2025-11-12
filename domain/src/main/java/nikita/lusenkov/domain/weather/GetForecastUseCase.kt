package nikita.lusenkov.domain.weather

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val kz: GetKzForecastUseCase,
    private val moscow: GetMoscowForecastUseCase,
    private val spb: GetSpbForecastUseCase
) {
    operator fun invoke(city: CityId, period: PeriodDomain): Flow<Result<Forecast>> {
        val days = period.days()
        return when (city) {
            CityId.ALMATY -> kz(days)
            CityId.MOSCOW -> moscow(days)
            CityId.SPB    -> spb(days)
        }
    }
}