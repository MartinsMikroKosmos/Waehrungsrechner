package com.example.whrungsrechner.domain.model

/**
 * Repräsentiert eine Währung im Domain-Layer.
 * Dieses Modell ist unabhängig von der Datenquelle oder der UI.
 *
 * @param code Der dreistellige Währungscode (z.B. "USD", "EUR").
 * @param name Der vollständige Name der Währung.
 */

data class Currency(
    val code: String,
    val name: String = ""
)
