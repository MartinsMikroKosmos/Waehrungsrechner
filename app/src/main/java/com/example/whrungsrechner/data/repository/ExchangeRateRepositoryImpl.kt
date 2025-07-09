package com.example.whrungsrechner.data.repository

import android.util.Log
import com.example.whrungsrechner.data.api.ExchangeRateApiService
import com.example.whrungsrechner.data.mapper.toCurrencyCodeList
import com.example.whrungsrechner.data.mapper.toExchangeRateList
import com.example.whrungsrechner.domain.model.ExchangeRate
import com.example.whrungsrechner.domain.repository.ExchangeRateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementierung des [ExchangeRateRepository]-Interfaces aus dem Domain-Layer.
 * Diese Klasse ist für den tatsächlichen Datenabruf von der API verantwortlich
 * und wandelt die API-Antworten (DTOs) in Domain-Modelle um.
 *
 * @param apiService Die Instanz des [ExchangeRateApiService] für API-Aufrufe.
 */
class ExchangeRateRepositoryImpl(
    private val apiService: ExchangeRateApiService
) : ExchangeRateRepository { // Implementiert das Domain-Layer Interface

    /**
     * Ruft die neuesten Wechselkurse von der API ab und wandelt sie in Domain-Modelle um.
     * Führt den Netzwerkaufruf in einem IO-Dispatcher aus.
     *
     * @param baseCurrencyCode Der Währungscode der Basiswährung (z.B. "USD").
     * @return Eine Liste von [ExchangeRate]-Objekten bei Erfolg, oder null bei einem Fehler.
     */
    override suspend fun getLatestExchangeRates(baseCurrencyCode: String): List<ExchangeRate>? {
        return withContext(Dispatchers.IO) { // Führt den Netzwerkaufruf im IO-Thread aus
            try {
                // API-Aufruf, beachte, dass die API Kleinbuchstaben erwartet
                val responseDto = apiService.getLatestRates(baseCurrencyCode.lowercase())
                // Umwandlung des DTOs in eine Liste von Domain-Modellen
                responseDto.toExchangeRateList()
            } catch (e: Exception) {
                // Protokolliert den Fehler und gibt null zurück
                Log.e("ExchangeRateRepositoryImpl", "Fehler beim Abrufen der neuesten Wechselkurse für $baseCurrencyCode: ${e.message}", e)
                null
            }
        }
    }

    /**
     * Ruft eine Liste aller verfügbaren Währungscodes von der API ab.
     * Dies geschieht, indem die neuesten Kurse für eine Standard-Basiswährung (z.B. "usd") abgefragt
     * und die Währungscodes aus der Antwort extrahiert werden.
     * Führt den Netzwerkaufruf in einem IO-Dispatcher aus.
     *
     * @return Eine sortierte Liste von Währungscodes (Strings) bei Erfolg, oder null bei einem Fehler.
     */
    override suspend fun getAvailableCurrencyCodes(): List<String>? {
        return withContext(Dispatchers.IO) { // Führt den Netzwerkaufruf im IO-Thread aus
            try {
                // Wir nutzen einen Standard-API-Aufruf, um alle verfügbaren Währungen zu erhalten.
                // Die API liefert alle Währungen, wenn wir z.B. "usd" als Basis verwenden.
                val responseDto = apiService.getLatestRates("usd") // Nutze "usd" als Standardbasis
                // Umwandlung des DTOs in eine Liste von Währungscodes
                responseDto.toCurrencyCodeList()
            } catch (e: Exception) {
                // Protokolliert den Fehler und gibt null zurück
                Log.e("ExchangeRateRepositoryImpl", "Fehler beim Abrufen der verfügbaren Währungscodes: ${e.message}", e)
                null
            }
        }
    }
}
