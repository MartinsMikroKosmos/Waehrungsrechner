package com.example.whrungsrechner.data.mapper

import com.example.whrungsrechner.data.api.CurrencyRatesDto
import com.example.whrungsrechner.domain.model.Currency
import com.example.whrungsrechner.domain.model.ExchangeRate

/**
 * Erweiterungsfunktionen zum Umwandeln von Data Transfer Objects (DTOs)
 * in Domain-Modelle.
 */

/**
 * Wandelt ein [CurrencyRatesDto] in eine Liste von [ExchangeRate]-Domain-Modellen um.
 *
 * @return Eine Liste von [ExchangeRate]-Objekten, die aus dem DTO erstellt wurden.
 */
fun CurrencyRatesDto.toExchangeRateList(): List<ExchangeRate> {
    val exchangeRates = mutableListOf<ExchangeRate>()
    val baseCurrency = Currency(this.base.uppercase()) // Basiswährungscode in Großbuchstaben umwandeln

    this.rates.forEach { (targetCurrencyCode, rate) ->
        // Füge den Wechselkurs von der Basiswährung zur Zielwährung hinzu
        exchangeRates.add(
            ExchangeRate(
                baseCurrency = baseCurrency,
                targetCurrency = Currency(targetCurrencyCode.uppercase()), // Zielwährungscode in Großbuchstaben umwandeln
                rate = rate
            )
        )
    }
    return exchangeRates
}

/**
 * Wandelt ein [CurrencyRatesDto] in eine sortierte Liste aller verfügbaren Währungscodes um.
 *
 * @return Eine sortierte Liste von Währungscodes (Strings), die im DTO enthalten sind.
 */
fun CurrencyRatesDto.toCurrencyCodeList(): List<String> {
    // Sammle alle Währungscodes (Basis und Zielwährungen), wandle sie in Großbuchstaben um und sortiere sie.
    val allCurrencyCodes = mutableSetOf<String>()
    allCurrencyCodes.add(this.base.uppercase()) // Basiswährung hinzufügen
    this.rates.keys.forEach { allCurrencyCodes.add(it.uppercase()) } // Alle Zielwährungen hinzufügen
    return allCurrencyCodes.sorted()
}
