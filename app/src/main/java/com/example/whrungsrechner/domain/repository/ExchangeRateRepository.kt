package com.example.whrungsrechner.domain.repository

import com.example.whrungsrechner.domain.model.ExchangeRate

interface ExchangeRateRepository {
    /**
     * Ruft die neuesten Wechselkurse für eine bestimmte Basiswährung ab.
     *
     * @param baseCurrencyCode Der Währungscode der Basiswährung (z.B. "USD").
     * @return Eine Liste von [ExchangeRate]-Objekten, die die Kurse von der Basiswährung zu anderen Währungen darstellen.
     * Gibt null zurück, wenn ein Fehler auftritt oder keine Daten verfügbar sind.
     */
    suspend fun getLatestExchangeRates(baseCurrencyCode: String): List<ExchangeRate>?

    /**
     * Ruft eine Liste aller verfügbaren Währungscodes ab.
     * Dies könnte aus den neuesten Wechselkursen extrahiert oder separat bereitgestellt werden.
     *
     * @return Eine Liste von Währungscodes (Strings).
     * Gibt null zurück, wenn ein Fehler auftritt oder keine Daten verfügbar sind.
     */
    suspend fun getAvailableCurrencyCodes(): List<String>?
}