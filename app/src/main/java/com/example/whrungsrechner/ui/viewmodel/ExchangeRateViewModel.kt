package com.example.whrungsrechner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.whrungsrechner.domain.model.Currency
import com.example.whrungsrechner.domain.model.ExchangeRate
import com.example.whrungsrechner.domain.usecase.ConvertCurrencyUseCase
import com.example.whrungsrechner.domain.usecase.GetAvailableCurrenciesUseCase
import com.example.whrungsrechner.domain.usecase.GetLatestExchangeRatesUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel für die Währungsumrechner-App.
 * Verantwortlich für die Bereitstellung der Daten für die UI und die Verarbeitung der Geschäftslogik.
 * Nutzt die Use Cases aus dem Domain-Layer.
 *
 * @param getLatestExchangeRatesUseCase Use Case zum Abrufen der neuesten Wechselkurse.
 * @param getAvailableCurrenciesUseCase Use Case zum Abrufen aller verfügbaren Währungscodes.
 * @param convertCurrencyUseCase Use Case zum Umrechnen von Währungen.
 */
class ExchangeRateViewModel(
    private val getLatestExchangeRatesUseCase: GetLatestExchangeRatesUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase,
    private val convertCurrencyUseCase: ConvertCurrencyUseCase
) : ViewModel() {

    // LiveData, das die Liste der verfügbaren Währungen enthält.
    private val _currencies = MutableLiveData<List<String>?>(emptyList())
    val currencies: MutableLiveData<List<String>?> = _currencies

    // LiveData, das die aktuellen Wechselkurse enthält (als Domain-Modelle).
    private val _exchangeRates = MutableLiveData<List<ExchangeRate>?>(emptyList())
    // Diese LiveData ist intern, da die UI nur das Ergebnis der Umrechnung benötigt,
    // nicht die rohen Wechselkurse.
    // val exchangeRates: LiveData<List<ExchangeRate>> = _exchangeRates // Nicht direkt exponiert

    // LiveData für den Ladezustand (true, wenn Daten geladen werden, false sonst).
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData für Fehlermeldungen (z.B. bei Netzwerkproblemen).
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    // LiveData für die ausgewählte Basiswährung
    private val _selectedBaseCurrency = MutableLiveData("USD") // Standard-Basiswährung
    val selectedBaseCurrency: LiveData<String> = _selectedBaseCurrency

    // LiveData für die ausgewählte Zielwährung
    private val _selectedTargetCurrency = MutableLiveData("EUR") // Standard-Zielwährung
    val selectedTargetCurrency: LiveData<String> = _selectedTargetCurrency

    // LiveData für den Eingabewert des Benutzers
    private val _amount = MutableLiveData("1.0") // Standard-Betrag
    val amount: LiveData<String> = _amount

    // LiveData für das Ergebnis der Umrechnung
    private val _convertedAmount = MutableLiveData("0.0")
    val convertedAmount: LiveData<String> = _convertedAmount

    init {
        // Beim Initialisieren des ViewModels die verfügbaren Währungen und die neuesten Wechselkurse abrufen.
        fetchAvailableCurrencies()
        fetchLatestRates(_selectedBaseCurrency.value ?: "USD")
    }

    /**
     * Ruft die Liste der verfügbaren Währungscodes ab.
     */
    private fun fetchAvailableCurrencies() {
        viewModelScope.launch {
            val availableCurrencies = getAvailableCurrenciesUseCase()
            if (availableCurrencies != null) {
                _currencies.value = availableCurrencies
            } else {
                _errorMessage.value = "Fehler beim Laden der verfügbaren Währungen."
            }
        }
    }

    /**
     * Ruft die neuesten Wechselkurse für eine bestimmte Basiswährung ab.
     * Aktualisiert die LiveData-Objekte basierend auf der API-Antwort.
     *
     * @param baseCurrencyCode Der Währungscode der Basiswährung, für die die Kurse abgerufen werden sollen.
     */
    fun fetchLatestRates(baseCurrencyCode: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _selectedBaseCurrency.value = baseCurrencyCode

        viewModelScope.launch {
            val rates = getLatestExchangeRatesUseCase(baseCurrencyCode)
            _isLoading.value = false

            if (rates != null) {
                _exchangeRates.value = rates
                // Aktualisiere die Liste der Währungen, falls sie noch leer ist oder sich geändert hat.
                // Dies stellt sicher, dass alle Währungen in den Dropdowns verfügbar sind.
                if (_currencies.value.isNullOrEmpty() || _currencies.value != rates.map { it.targetCurrency.code }.toSet().plus(baseCurrencyCode).toList().sorted()) {
                    _currencies.value = rates.map { it.targetCurrency.code }.toSet().plus(baseCurrencyCode).toList().sorted()
                }
                convertCurrency() // Führe die Umrechnung nach dem Laden der neuen Kurse durch
            } else {
                _errorMessage.value = "Fehler beim Laden der Wechselkurse für $baseCurrencyCode. Bitte versuchen Sie es später erneut."
                _exchangeRates.value = emptyList() // Leere die Kurse bei Fehler
                _convertedAmount.value = "0.0" // Setze das Ergebnis zurück
            }
        }
    }

    /**
     * Setzt die ausgewählte Basiswährung und ruft die neuen Kurse ab.
     * @param currencyCode Der Währungscode der neuen Basiswährung.
     */
    fun setSelectedBaseCurrency(currencyCode: String) {
        if (_selectedBaseCurrency.value != currencyCode) {
            _selectedBaseCurrency.value = currencyCode
            fetchLatestRates(currencyCode) // Neue Kurse für die neue Basiswährung abrufen
        }
    }

    /**
     * Setzt die ausgewählte Zielwährung.
     * @param currencyCode Der Währungscode der neuen Zielwährung.
     */
    fun setSelectedTargetCurrency(currencyCode: String) {
        if (_selectedTargetCurrency.value != currencyCode) {
            _selectedTargetCurrency.value = currencyCode
            convertCurrency() // Umrechnung mit neuer Zielwährung durchführen
        }
    }

    /**
     * Aktualisiert den Betrag, der umgerechnet werden soll.
     * @param newAmount Der neue Betrag als String.
     */
    fun setAmount(newAmount: String) {
        _amount.value = newAmount
        convertCurrency() // Umrechnung nach Änderung des Betrags durchführen
    }

    /**
     * Führt die Währungsumrechnung basierend auf den aktuellen Werten durch.
     * Nutzt den ConvertCurrencyUseCase.
     */
    fun convertCurrency() {
        val amountValue = _amount.value?.toDoubleOrNull() ?: 0.0
        val baseCurrencyCode = _selectedBaseCurrency.value
        val targetCurrencyCode = _selectedTargetCurrency.value
        val rates = _exchangeRates.value

        if (baseCurrencyCode != null && targetCurrencyCode != null && rates != null && rates.isNotEmpty()) {
            val baseCurrency = Currency(baseCurrencyCode)
            val targetCurrency = Currency(targetCurrencyCode)

            val conversionResult = convertCurrencyUseCase(
                amount = amountValue,
                baseCurrency = baseCurrency,
                targetCurrency = targetCurrency,
                exchangeRates = rates
            )

            if (conversionResult != null) {
                _convertedAmount.value = String.format("%.2f", conversionResult.convertedAmount)
            } else {
                _convertedAmount.value = "N/A" // Zielkurs nicht gefunden
            }
        } else {
            _convertedAmount.value = "0.0" // Keine Daten oder ungültige Eingabe
        }
    }
}

/**
 * Factory-Klasse zum Erstellen von [ExchangeRateViewModel]-Instanzen.
 * Notwendig, da das ViewModel Konstruktor-Parameter (Use Cases) hat.
 */
class ExchangeRateViewModelFactory(
    private val getLatestExchangeRatesUseCase: GetLatestExchangeRatesUseCase,
    private val getAvailableCurrenciesUseCase: GetAvailableCurrenciesUseCase,
    private val convertCurrencyUseCase: ConvertCurrencyUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExchangeRateViewModel::class.java)) {
            return ExchangeRateViewModel(
                getLatestExchangeRatesUseCase,
                getAvailableCurrenciesUseCase,
                convertCurrencyUseCase
            ) as T
        }
        throw IllegalArgumentException("Unbekannte ViewModel-Klasse")
    }
}
