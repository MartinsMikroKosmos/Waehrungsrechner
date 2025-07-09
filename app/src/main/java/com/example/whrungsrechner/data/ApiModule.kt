package com.example.whrungsrechner.data

import com.example.whrungsrechner.data.api.ExchangeRateApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Objekt für die Bereitstellung der Retrofit-Instanz und des API-Dienstes.
 * Dies dient als einfache Form der Abhängigkeitsinjektion.
 */
object ApiModule {

    // Die Basis-URL für die Exchange Rate API.
    // Wichtig: Die URL muss mit einem "/" enden, wenn keine spezifischen Pfade in den @GET-Annotationen definiert sind.
    // Die API-Endpunkte sind jetzt im Format: https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/{currency_code}.json
    // oder https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/{date}/{currency_code}.json
    private const val BASE_URL = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/"

    /**
     * Erstellt und konfiguriert einen OkHttpClient.
     * Fügt einen HttpLoggingInterceptor hinzu, um HTTP-Anfragen und -Antworten im Logcat zu protokollieren.
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            // Setzt den Logging-Level auf BODY, um Header und Body zu protokollieren.
            level = HttpLoggingInterceptor.Level.BODY
        })
        // Setzt Timeouts für die Verbindung, das Lesen und das Schreiben.
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Erstellt und konfiguriert eine Retrofit-Instanz.
     * Verwendet den OkHttpClient und den GsonConverterFactory für die JSON-Serialisierung/Deserialisierung.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Setzt die Basis-URL für alle API-Aufrufe.
            .client(okHttpClient) // Verwendet den konfigurierten OkHttpClient.
            .addConverterFactory(GsonConverterFactory.create()) // Fügt den Gson-Konverter hinzu.
            .build()
    }

    /**
     * Erstellt eine Instanz des [ExchangeRateApiService].
     * Dies ist die Schnittstelle, die wir zuvor definiert haben.
     */
    val exchangeRateApiService: ExchangeRateApiService by lazy {
        retrofit.create(ExchangeRateApiService::class.java)
    }
}