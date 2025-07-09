package com.example.whrungsrechner.domain.model

/**
 * Repräsentiert einen Wechselkurs im Domain-Layer.
 *
 * @param baseCurrency Die Basiswährung des Kurses.
 * @param targetCurrency Die Zielwährung des Kurses.
 * @param rate Der Wechselkurs (wie viele Einheiten der Zielwährung man für eine Einheit der Basiswährung erhält).
 */
data class ExchangeRate(
    val baseCurrency: Currency,
    val targetCurrency: Currency,
    val rate: Double
)