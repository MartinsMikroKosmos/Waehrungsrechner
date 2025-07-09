package com.example.whrungsrechner.domain.model

/**
 * Repräsentiert das Ergebnis einer Währungsumrechnung im Domain-Layer.
 *
 * @param amount Der ursprüngliche Betrag in der Basiswährung.
 * @param baseCurrency Die Basiswährung.
 * @param convertedAmount Der umgerechnete Betrag in der Zielwährung.
 * @param targetCurrency Die Zielwährung.
 * @param exchangeRate Der verwendete Wechselkurs.
 */
data class ConversionResult(
    val amount: Double,
    val baseCurrency: Currency,
    val convertedAmount: Double,
    val targetCurrency: Currency,
    val exchangeRate: Double
)
