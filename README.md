# CryptoExchangeAndroid

Android port of the CryptoExchange iOS SwiftUI project.  
Stack: **Kotlin · Jetpack Compose · MVVM + Clean Architecture · Hilt · Retrofit · Coil · Coroutines/Flow**


## Setup

### 1. Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- Android SDK 35

### 2. Configure the API Key

Copy the example and add your [CoinMarketCap PRO API key](https://pro.coinmarketcap.com/):

```
cp local.properties.example local.properties
```

Edit `local.properties`:
```properties
sdk.dir=/path/to/your/Android/Sdk
CMC_API_KEY=YOUR_COINMARKETCAP_API_KEY_HERE
```

> ⚠️ `local.properties` is in `.gitignore`. Never commit your API key.

The API key is resolved in this order at build time:
1. `local.properties` → `CMC_API_KEY`
2. Gradle property → `-PCMC_API_KEY=...`
3. Environment variable → `CMC_API_KEY` (useful for CI/CD pipelines)

> ⚠️ If no key is found, `BuildConfig.CMC_API_KEY` is an empty string and the app will throw  
> `IllegalStateException: CMC_API_KEY is not configured` at the first API call.

### 3. Open in Android Studio
1. File → Open → select the `CryptoExchangeAndroid/` folder
2. Wait for Gradle sync to finish
3. Run on an emulator or device (API 26+)

---

## Build

```bash
# Debug build
./gradlew assembleDebug

# Release build (configure signing first)
./gradlew assembleRelease
```

---

## Run Tests

```bash
# Unit tests (no device needed)
./gradlew testDebugUnitTest

# Specific test class
./gradlew testDebugUnitTest --tests "br.com.cryptoexchange.data.mapper.ExchangeMapperTest"

# All tests with report
./gradlew test
```

Test reports: `app/build/reports/tests/testDebugUnitTest/index.html`

---

## Architecture

```
app/src/main/java/br/com/cryptoexchange/
├── data/
│   ├── api/            CmcApiService (Retrofit)
│   ├── dto/            Dtos.kt (all DTOs with @SerializedName)
│   ├── mapper/         ExchangeMapper, MarketPairMapper
│   ├── repository/     ExchangeRepositoryImpl
│   ├── AppError.kt     sealed class (matches iOS AppError enum exactly)
│   └── CMCLogger.kt    [CMC]/[IMG] logs + debug state
├── domain/
│   ├── model/          Exchange, ExchangeMarketPair, MarketCurrency
│   ├── repository/     ExchangeRepository interface
│   └── usecase/        GetExchangeListUseCase, GetExchangeDetailUseCase, GetExchangeMarketPairsUseCase
├── ui/
│   ├── components/     StateViews (Loading/Empty/Error/DebugApiSheet), ExchangeRowItem, MarketPairRowItem
│   ├── navigation/     AppNavigation, NavRoutes, NavArgs
│   ├── screens/
│   │   ├── exchangelist/   ExchangeListScreen + ExchangeListViewModel
│   │   └── exchangedetail/ ExchangeDetailScreen + ExchangeDetailViewModel
│   ├── theme/          CryptoExchangeTheme
│   └── UiState.kt      sealed class: Idle/Loading/Success/Empty/Error
├── di/
│   ├── NetworkModule.kt    OkHttp + Retrofit + API key interceptor
│   └── RepositoryModule.kt Hilt bindings
├── CryptoExchangeApp.kt    @HiltAndroidApp
└── MainActivity.kt         @AndroidEntryPoint
```

---

## Key Architecture Decisions

| Decision | Reason |
|---|---|
| **Hilt** (vs manual DI) | Equivalent to iOS `DependencyContainer` but compile-time safe |
| **Coil** (vs custom ImageLoader) | Handles disk+memory cache automatically; equivalent to iOS `CachedLogoView` with NSCache |
| **StateFlow** (vs LiveData) | Direct equivalent of `@Published` properties in SwiftUI ViewModels |
| **Retrofit + Gson** | Industry standard; `@SerializedName` = explicit `CodingKeys` in Swift DTOs |
| **Two-step list** (`/exchange/map` → `/exchange/info`) | Identical to iOS: map gives ordered IDs, info gives logo+metadata |
| **ISO8601 with fractional seconds** | CMC returns `"2017-07-14T00:00:00.000Z"` — both parsers handle this |
| **DEBUG flag** | `BuildConfig.DEBUG` = Swift `#if DEBUG` — debug sheet and image logs hidden in release |

---

## Debug Features (DEBUG builds only)

- **🐛 Button** in the top bar of the Exchange List screen → Image Logs sheet showing last 20 image load results (cache hit/miss, latency, status codes)
- **Error Details button** on error states → API Debug sheet with requestId, URL, status code, JSON snippet
- OkHttp `HttpLoggingInterceptor` at `BODY` level in DEBUG builds
- `CMCLogger` tags: `[CMC]` for API calls, `[IMG]` for image loads
- API key masked in logs (last 4 chars only)
