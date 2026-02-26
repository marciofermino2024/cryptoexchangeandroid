package br.com.cryptoexchange.di

import br.com.cryptoexchange.BuildConfig
import br.com.cryptoexchange.data.api.CmcApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://pro-api.coinmarketcap.com/"
    private const val TIMEOUT_SECONDS = 15L

    @Provides
    @Singleton
    fun provideCmcApiKeyInterceptor(): Interceptor = Interceptor { chain ->
        val apiKey = BuildConfig.CMC_API_KEY
        if (apiKey.isBlank()) {
            throw IllegalStateException(
                "CMC_API_KEY is not configured.\n" +
                "1. Copy local.properties.example to local.properties\n" +
                "2. Set CMC_API_KEY=your_key_here\n" +
                "3. Rebuild the project."
            )
        }
        val request = chain.request().newBuilder()
            .addHeader("X-CMC_PRO_API_KEY", apiKey)
            .addHeader("Accept", "application/json")
            .build()
        chain.proceed(request)
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideCmcApiService(retrofit: Retrofit): CmcApiService =
        retrofit.create(CmcApiService::class.java)
}
