package com.example.whrungsrechner.domain.usecase

import com.example.whrungsrechner.domain.model.ExchangeRate
import com.example.whrungsrechner.domain.repository.ExchangeRateRepository

/**
 * Use Case zum Abrufen der neuesten Wechselkurse.
 * Kapselt die Geschäftslogik für diesen spezifischen Anwendungsfall.
 *
 * @param repository Die Schnittstelle zum [ExchangeRateRepository], um Daten zu erhalten.
 */
class GetLatestExchangeRatesUseCase(
    private val repository: ExchangeRateRepository
) {
    /**
     * Führt den Anwendungsfall aus, um die neuesten Wechselkurse abzurufen.
     *
     * @param baseCurrencyCode Der Währungscode der Basiswährung (z.B. "USD").
     * @return Eine Liste von [ExchangeRate]-Objekten bei Erfolg, oder null bei einem Fehler.
     */
    suspend operator fun invoke(baseCurrencyCode: String): List<ExchangeRate>? {
        // Ruft die Daten vom Repository ab.
        return repository.getLatestExchangeRates(baseCurrencyCode)
    }
}