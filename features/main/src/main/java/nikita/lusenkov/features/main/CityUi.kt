package nikita.lusenkov.features.main

import androidx.annotation.DrawableRes
import nikita.lusenkov.domain.weather.CityId

enum class CityUi(val title: String, @DrawableRes val iconRes: Int, val id: CityId) {
    Moscow("Москва", R.drawable.moscow, CityId.MOSCOW),
    Spb("Санкт-Петербург", R.drawable.spb, CityId.SPB),
    Almaty("Алматы", R.drawable.kz, CityId.ALMATY)
}