package com.example.whrungsrechner.data.api

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit-Schnittstelle für die Exchange Rate API.
 * Definiert die Endpunkte und die erwarteten Rückgabetypen ([CurrencyRatesDto]).
 */
interface ExchangeRateApiService {

    /**
     * Ruft die neuesten Wechselkurse für eine bestimmte Basiswährung ab.
     *
     * @param baseCurrencyCode Der Währungscode der Basiswährung (z.B. "usd").
     * @return Eine [CurrencyRatesDto]-Objekt, das die Wechselkursdaten enthält.
     */
    @GET("{baseCurrencyCode}.json") // API-Pfad für die neuesten Kurse (z.B. usd.json)
    suspend fun getLatestRates(@Path("baseCurrencyCode") baseCurrencyCode: String): CurrencyRatesDto

    /**
     * Ruft die Wechselkurse für ein bestimmtes Datum und eine Basiswährung ab.
     *
     * @param date Das Datum im Format YYYY-MM-DD (z.B. "2023-01-01").
     * @param baseCurrencyCode Der Währungscode der Basiswährung (z.B. "usd").
     * @return Eine [CurrencyRatesDto]-Objekt.
     */
    @GET("{date}/{baseCurrencyCode}.json") // API-Pfad für historische Kurse (z.B. 2023-01-01/usd.json)
    suspend fun getHistoricalRates(
        @Path("date") date: String,
        @Path("baseCurrencyCode") baseCurrencyCode: String
    ): CurrencyRatesDto
}