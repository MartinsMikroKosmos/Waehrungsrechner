package com.example.whrungsrechner.domain.usecase

import com.example.whrungsrechner.domain.model.ConversionResult
import com.example.whrungsrechner.domain.model.Currency
import com.example.whrungsrechner.domain.model.ExchangeRate

/**
 * Use Case zum Umrechnen eines Betrags von einer Basiswährung in eine Zielwährung.
 * Kapselt die Umrechnungslogik.
 */
class ConvertCurrencyUseCase {
    /**
     * Führt die Währungsumrechnung durch.
     *
     * @param amount Der umzurechnende Betrag in der Basiswährung.
     * @param baseCurrency Die Basiswährung.
     * @param targetCurrency Die Zielwährung.
     * @param exchangeRates Die Liste der verfügbaren Wechselkurse, die die Basiswährung als Basis haben.
     * @return Ein [ConversionResult]-Objekt, das das Ergebnis der Umrechnung enthält.
     * Gibt null zurück, wenn der Zielkurs nicht gefunden werden kann.
     */
    operator fun invoke(
        amount: Double,
        baseCurrency: Currency,
        targetCurrency: Currency,
        exchangeRates: List<ExchangeRate>
    ): ConversionResult? {
        // Finde den Wechselkurs von der Basiswährung zur Zielwährung.
        val targetRate = exchangeRates.find {
            it.baseCurrency == baseCurrency && it.targetCurrency == targetCurrency
        }?.rate

        return if (targetRate != null) {
            val convertedAmount = amount * targetRate
            ConversionResult(
                amount = amount,
                baseCurrency = baseCurrency,
                convertedAmount = convertedAmount,
                targetCurrency = targetCurrency,
                exchangeRate = targetRate
            )
        } else {
            null // Zielkurs nicht gefunden
        }
    }
}