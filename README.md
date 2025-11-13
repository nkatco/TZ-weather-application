## Стек и требования
### Языки и инструменты
- Kotlin 2.0.21, JDK 17
- Android Gradle Plugin 8.9.3, Gradle (совместимый)
- Jetpack Compose (BOM 2025.11.00), Material3 (включая pull-to-refresh)
- Coroutines/Flow 1.9.0
- Hilt 2.52
- Retrofit 2.11.0
- Kotlinx Serialization JSON 1.7.3
- OkHttp Logging 4.12.0
- Room 2.8.3
### Android SDK
- compileSdk = 36
- minSdk = 24

### Из разрешений - только <uses-permission android:name="android.permission.INTERNET" /> в app/src/main/AndroidManifest.xml

## Архитектура проекта - MVVM
- app/                     — точка входа, навигация и wiring фич
- features/main/           — экран погоды (Compose UI + ViewModel)
- domain/                  — сущности (Forecast), use-cases, интерфейсы
- data/repository/         — реализации репозиториев (Flow<Result<T>>)
- data/remote/             — Retrofit API, интерсепторы, dto, safeApiCall
- data/local/              — Room (кэш: таблицы, dao, datasources)
Ключевая зависимость: features → domain → data* (через DI).

## Как работает кэш?
Ключ кэша формируется из параметров запроса, а уже в Room хранится пара: (payloadJson, updatedAtMillis). Локальный datasource выдаёт (json, updatedAt); репозиторий сразу парсит и эмитит (если удалось). Обновление из сети всегда выполняется параллельно с получением данных из кэша и, если тело изменилось — перезаписывает кэш и эмитит новое значение.

## API ключ
В модуле data/remote ключ читается из BuildConfig:
```
defaultConfig {
    val apiKey = project.findProperty("WEATHER_API_KEY") as String?
        ?: throw GradleException("Missing WEATHER_API_KEY in gradle.properties")
    buildConfigField("String", "WEATHER_API_KEY", "\"$apiKey\"")
}
```

## Сборка и запуск
### 1) Android Studio (рекомендуется)
1. Откройте проект.
2. Убедитесь, что скачаны Android API 36 и инструменты Build-Tools.
3. В gradle.properties добавьте WEATHER_API_KEY.
4. Запустите app на девайсе/эмуляторе (Build/Run).
### 2) Командная строка
```
# Сборка debug APK
./gradlew :app:assembleDebug

# Сборка release (подписывать самим)
./gradlew :app:assembleRelease

# Установка и запуск на подключённом устройстве
./gradlew :app:installDebug
```
APK лежит в:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Лицензия
MIT — делайте что хотите, но без гарантий.
