package com.example.whrungsrechner.data.api

import com.google.gson.annotations.SerializedName

/**
 * DTO (Data Transfer Object) das die gesamte API-Antwort der Wechselkurs-API darstellt.
 */
data class CurrencyRatesDto(
    val date: String,
    val base: String, // Basiswährungscode (kleingeschrieben von der API)
    @SerializedName("rates")
    val rates: Map<String, Double> // Eine Map von Währungscode (kleingeschrieben) zu Wechselkurs
)