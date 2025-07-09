package com.example.whrungsrechner.domain.usecase

import com.example.whrungsrechner.domain.repository.ExchangeRateRepository

/**
 * Use Case zum Abrufen aller verfügbaren Währungscodes.
 *
 * @param repository Die Schnittstelle zum [ExchangeRateRepository].
 */
class GetAvailableCurrenciesUseCase(
    private val repository: ExchangeRateRepository
) {
    /**
     * Führt den Anwendungsfall aus, um die Liste der verfügbaren Währungscodes abzurufen.
     *
     * @return Eine Liste von Währungscodes (Strings) bei Erfolg, oder null bei einem Fehler.
     */
    suspend operator fun invoke(): List<String>? {
        // Ruft die Daten vom Repository ab.
        return repository.getAvailableCurrencyCodes()
    }
}