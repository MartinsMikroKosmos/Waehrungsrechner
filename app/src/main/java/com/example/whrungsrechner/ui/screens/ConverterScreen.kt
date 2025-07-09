
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whrungsrechner.data.ApiModule
import com.example.whrungsrechner.data.repository.ExchangeRateRepositoryImpl
import com.example.whrungsrechner.domain.usecase.ConvertCurrencyUseCase
import com.example.whrungsrechner.domain.usecase.GetAvailableCurrenciesUseCase
import com.example.whrungsrechner.domain.usecase.GetLatestExchangeRatesUseCase
import com.example.whrungsrechner.ui.viewmodel.ExchangeRateViewModel
import com.example.whrungsrechner.ui.viewmodel.ExchangeRateViewModelFactory

/**
 * Der Hauptbildschirm für die Währungsumrechner-App.
 * Zeigt Eingabefelder für Betrag und Währungen, Ladeindikatoren und das Umrechnungsergebnis an.
 *
 * @param viewModel Die Instanz des [ExchangeRateViewModel], die die Daten für die UI bereitstellt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(viewModel: ExchangeRateViewModel = viewModel(
    factory = ExchangeRateViewModelFactory(
        getLatestExchangeRatesUseCase = GetLatestExchangeRatesUseCase(ExchangeRateRepositoryImpl(
            ApiModule.exchangeRateApiService)),
        getAvailableCurrenciesUseCase = GetAvailableCurrenciesUseCase(ExchangeRateRepositoryImpl(
            ApiModule.exchangeRateApiService)),
        convertCurrencyUseCase = ConvertCurrencyUseCase()
    )
)) {
    // Beobachte LiveData aus dem ViewModel als Compose State
    val currencies by viewModel.currencies.observeAsState(emptyList())
    val selectedBaseCurrency by viewModel.selectedBaseCurrency.observeAsState("USD")
    val selectedTargetCurrency by viewModel.selectedTargetCurrency.observeAsState("EUR")
    val amount by viewModel.amount.observeAsState("1.0")
    val convertedAmount by viewModel.convertedAmount.observeAsState("0.0")
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Währungsumrechner") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Eingabefeld für den Betrag
                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        // Erlaube nur numerische Eingaben (mit optionalem Dezimalpunkt)
                        if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            viewModel.setAmount(newValue)
                        }
                    },
                    label = { Text("Betrag") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Dropdowns für Basis- und Zielwährung
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currencies?.let {
                        CurrencyDropdown(
                            label = "Basiswährung",
                            selectedCurrency = selectedBaseCurrency,
                            currencies = it,
                            onCurrencySelected = { viewModel.setSelectedBaseCurrency(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    currencies?.let {
                        CurrencyDropdown(
                            label = "Zielwährung",
                            selectedCurrency = selectedTargetCurrency,
                            currencies = it,
                            onCurrencySelected = { viewModel.setSelectedTargetCurrency(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Ergebnis-Anzeige
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Umrechnungs-Ergebnis:",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$convertedAmount ${selectedTargetCurrency}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Ladeindikator und Fehlermeldung
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }

                errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Wiederverwendbare Composable für ein Währungs-Dropdown-Menü.
 * Verwendet eine Card-Darstellung anstelle eines OutlinedTextFields.
 *
 * @param label Der Text, der als Label für das Dropdown angezeigt wird.
 * @param selectedCurrency Die aktuell ausgewählte Währung.
 * @param currencies Die Liste der verfügbaren Währungscodes.
 * @param onCurrencySelected Callback, der aufgerufen wird, wenn eine Währung ausgewählt wird.
 * @param modifier Der Modifier für dieses Composable.
 */
@Composable
fun CurrencyDropdown(
    label: String,
    selectedCurrency: String,
    currencies: List<String>,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Standardhöhe für Textfelder
                .clickable { expanded = true },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedCurrency,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Pfeil",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth() // Dropdown soll die Breite der Card einnehmen
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}
