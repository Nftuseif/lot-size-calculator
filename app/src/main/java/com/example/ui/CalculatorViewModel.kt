package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.PositionSizeCalculator
import com.example.calculator.PositionSizeCalculator.ForexPair
import com.example.calculator.PositionSizeCalculator.CryptoAsset
import com.example.data.CalculationHistory
import com.example.data.TradingDatabase
import com.example.data.TradingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs

import com.example.data.GeminiContent
import com.example.data.GeminiGenerationConfig
import com.example.data.GeminiPart
import com.example.data.GeminiRequest
import com.example.data.GeminiRetrofitClient
import java.util.Locale
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TradingRepository

    init {
        val database = TradingDatabase.getDatabase(application)
        repository = TradingRepository(database.calculationHistoryDao())
    }

    val historyList: StateFlow<List<CalculationHistory>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Chat Bot states
    var chatMessages by mutableStateOf<List<ChatMessage>>(listOf(
        ChatMessage(
            text = "Hello! I am SizeWise AI, your risk management and position sizing assistant. How can I help you manage your trading risk today?",
            isUser = false
        )
    ))
    var chatInputText by mutableStateOf("")
    var isChatLoading by mutableStateOf(false)
    var chatError by mutableStateOf<String?>(null)

    fun sendChatMessage(text: String) {
        if (text.isBlank() || isChatLoading) return

        val userMsg = ChatMessage(text = text, isUser = true)
        chatMessages = chatMessages + userMsg
        chatInputText = ""
        isChatLoading = true
        chatError = null

        viewModelScope.launch {
            try {
                val apiKey = com.example.BuildConfig.GEMINI_API_KEY

                // Compile history context
                val historyContext = historyList.value.take(5).joinToString("\n") { h ->
                    "- ${h.assetClass} on ${h.symbol}: Balance \$${h.accountBalance}, Risk ${h.riskPercentage}%, SL ${h.stopLossValue}, Size ${h.calculatedPositionSize} (${if (h.isLong) "LONG" else "SHORT"})${if (h.notes.isNotEmpty()) " Notes: ${h.notes}" else ""}"
                }.ifEmpty { "No history logged yet." }

                val balance = getBalance()
                val riskPct = getRiskPercentage()
                val riskAmt = getRiskAmount()
                val fResult = forexResult
                val cResult = cryptoResult

                val systemPrompt = """
                    You are SizeWise AI, a professional risk management and position sizing support agent for the SizeWise Trading Calculator.
                    Your role is to help traders understand position sizing, leverage, stop losses, and risk management strategies.
                    
                    Here is the current user's live context from the calculator screen:
                    [ACCOUNT BALANCE]
                    - Current Balance: ${'$'}${String.format(Locale.US, "%,.2f", balance)}
                    - Risk Exposure: ${String.format(Locale.US, "%.2f", riskPct)}% (${'$'}${String.format(Locale.US, "%,.2f", riskAmt)})
                    
                    [CURRENT FOREX INPUTS & RESULTS]
                    - Selected Pair: ${selectedForexPair.name} (Exchange Rate: ${forexExchangeRateInput})
                    - Stop Loss: ${forexStopLossPips} pips
                    - Calculated Size: ${String.format(Locale.US, "%.2f", fResult.standardLots)} Standard Lots (Mini: ${String.format(Locale.US, "%.1f", fResult.miniLots)}, Micro: ${String.format(Locale.US, "%.1f", fResult.microLots)}, Units: ${String.format(Locale.US, "%,.0f", fResult.units)})
                    
                    [CURRENT CRYPTO INPUTS & RESULTS]
                    - Selected Asset: ${selectedCryptoAsset.symbol}
                    - Entry Price: ${cryptoEntryPriceInput}, Stop Loss Price: ${cryptoStopLossPriceInput} (${if (isCryptoLong) "LONG" else "SHORT"})
                    - Calculated Size: ${String.format(Locale.US, "%.4f", cResult.coins)} coins (Value: ${'$'}${String.format(Locale.US, "%,.2f", cResult.positionValue)})
                    
                    [RECENT JOURNAL HISTORY]
                    ${historyContext}
                    
                    Instructions:
                    1. Direct, expert, highly professional tone. Do not use verbose introductions or self-promoting language.
                    2. Maintain conversation history and answer context-aware questions. If the user asks about their "current trade" or "current risk", refer to the numbers above.
                    3. Give actionable feedback on whether the leverage required is safe or if the risk is too high (e.g. over-leveraged).
                    4. Always suggest protective trading habits (such as adhering to a 1% risk rule). Keep answers concise and optimized for a clean support thread format.
                """.trimIndent()

                // Map message history to Gemini contents list
                // To maintain context properly, map the recent chat history (last 10 messages)
                val geminiContents = chatMessages.takeLast(10).map { msg ->
                    GeminiContent(
                        parts = listOf(GeminiPart(text = msg.text)),
                        role = if (msg.isUser) "user" else "model"
                    )
                }

                val requestBody = GeminiRequest(
                    contents = geminiContents,
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                    generationConfig = GeminiGenerationConfig(temperature = 0.7f)
                )

                val response = GeminiRetrofitClient.service.generateContent(apiKey, requestBody)
                val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "I received an empty response. Please try again."

                chatMessages = chatMessages + ChatMessage(text = replyText, isUser = false)
            } catch (e: Exception) {
                chatError = e.message ?: "Failed to connect to the assistant"
                chatMessages = chatMessages + ChatMessage(
                    text = "Sorry, I am having trouble connecting to my brain right now. Please check your internet connection and try again.",
                    isUser = false
                )
            } finally {
                isChatLoading = false
            }
        }
    }

    fun selectQuickPrompt(promptText: String) {
        sendChatMessage(promptText)
    }

    // Shared / Profile states
    var accountBalance by mutableStateOf("10000")
    var riskPercentage by mutableStateOf("1")
    var riskAmountOverride by mutableStateOf("") // If user wants to type direct USD instead of %
    var isRiskAmountMode by mutableStateOf(false) // Toggle between % and direct USD risk input

    // Forex states
    var selectedForexPair by mutableStateOf(PositionSizeCalculator.forexPairs.first())
    var forexStopLossPips by mutableStateOf("20")
    var forexExchangeRateInput by mutableStateOf(PositionSizeCalculator.forexPairs.first().defaultExchangeRate.toString())
    var customPairPipValueInput by mutableStateOf("10.0")

    // Forex Auxiliary rates for conversions
    var usdJpyRateInput by mutableStateOf("155.20")
    var usdCadRateInput by mutableStateOf("1.3650")
    var usdChfRateInput by mutableStateOf("0.9050")
    var gbpUsdRateInput by mutableStateOf("1.2650")

    // Crypto states
    var selectedCryptoAsset by mutableStateOf(PositionSizeCalculator.cryptoAssets.first())
    var cryptoEntryPriceInput by mutableStateOf(PositionSizeCalculator.cryptoAssets.first().defaultPrice.toString())
    var cryptoStopLossPriceInput by mutableStateOf((PositionSizeCalculator.cryptoAssets.first().defaultPrice * 0.95).toString())
    var isCryptoLong by mutableStateOf(true)

    // Notes for logging
    var calculationNotes by mutableStateOf("")

    // Helper functions to get parsed numbers safely
    private fun getBalance(): Double = accountBalance.toDoubleOrNull() ?: 0.0
    
    fun getRiskPercentage(): Double {
        if (isRiskAmountMode) {
            val balance = getBalance()
            val amt = riskAmountOverride.toDoubleOrNull() ?: 0.0
            return if (balance > 0) (amt / balance) * 100.0 else 0.0
        }
        return riskPercentage.toDoubleOrNull() ?: 0.0
    }

    fun getRiskAmount(): Double {
        if (isRiskAmountMode) {
            return riskAmountOverride.toDoubleOrNull() ?: 0.0
        }
        val balance = getBalance()
        val pct = riskPercentage.toDoubleOrNull() ?: 0.0
        return balance * (pct / 100.0)
    }

    // Dynamic calculations
    val forexResult: PositionSizeCalculator.ForexResult
        get() {
            val balance = getBalance()
            val rPct = getRiskPercentage()
            val sl = forexStopLossPips.toDoubleOrNull() ?: 0.0
            val currentRate = forexExchangeRateInput.toDoubleOrNull() ?: selectedForexPair.defaultExchangeRate
            val customPipVal = customPairPipValueInput.toDoubleOrNull() ?: 10.0
            
            return PositionSizeCalculator.calculateForexLotSize(
                accountBalance = balance,
                riskPercentage = rPct,
                stopLossInPips = sl,
                pair = selectedForexPair,
                customPairPipValue = customPipVal,
                exchangeRate = currentRate,
                usdJpyRate = usdJpyRateInput.toDoubleOrNull() ?: 155.20,
                usdCadRate = usdCadRateInput.toDoubleOrNull() ?: 1.3650,
                usdChfRate = usdChfRateInput.toDoubleOrNull() ?: 0.9050,
                gbpUsdRate = gbpUsdRateInput.toDoubleOrNull() ?: 1.2650
            )
        }

    val cryptoResult: PositionSizeCalculator.CryptoResult
        get() {
            val balance = getBalance()
            val rPct = getRiskPercentage()
            val entry = cryptoEntryPriceInput.toDoubleOrNull() ?: 0.0
            val sl = cryptoStopLossPriceInput.toDoubleOrNull() ?: 0.0
            
            return PositionSizeCalculator.calculateCryptoPositionSize(
                accountBalance = balance,
                riskPercentage = rPct,
                entryPrice = entry,
                stopLossPrice = sl
            )
        }

    // State changes
    fun onForexPairSelected(pair: ForexPair) {
        selectedForexPair = pair
        forexExchangeRateInput = pair.defaultExchangeRate.toString()
    }

    fun onCryptoAssetSelected(asset: CryptoAsset) {
        selectedCryptoAsset = asset
        cryptoEntryPriceInput = asset.defaultPrice.toString()
        // Automatically default Stop Loss to 5% below for Long, 5% above for Short
        updateCryptoStopLoss(asset.defaultPrice, isCryptoLong)
    }

    fun toggleCryptoDirection() {
        isCryptoLong = !isCryptoLong
        val entry = cryptoEntryPriceInput.toDoubleOrNull() ?: selectedCryptoAsset.defaultPrice
        updateCryptoStopLoss(entry, isCryptoLong)
    }

    private fun updateCryptoStopLoss(entry: Double, isLong: Boolean) {
        val factor = if (isLong) 0.95 else 1.05
        cryptoStopLossPriceInput = String.format("%.4f", entry * factor).replace(",", ".")
    }

    // Actions
    fun saveForexCalculation() {
        viewModelScope.launch {
            val result = forexResult
            if (result.standardLots <= 0) return@launch

            val record = CalculationHistory(
                assetClass = "FOREX",
                symbol = selectedForexPair.name,
                accountBalance = getBalance(),
                riskPercentage = getRiskPercentage(),
                riskAmount = getRiskAmount(),
                stopLossValue = forexStopLossPips.toDoubleOrNull() ?: 0.0,
                entryPrice = null,
                stopLossPrice = null,
                calculatedPositionSize = result.standardLots,
                positionValue = result.units, // for forex, units of base currency is exposure
                isLong = true, // Forex default long/short doesn't change lot math but we could store it
                notes = calculationNotes.trim()
            )
            repository.insert(record)
            calculationNotes = ""
        }
    }

    fun saveCryptoCalculation() {
        viewModelScope.launch {
            val result = cryptoResult
            if (result.coins <= 0) return@launch

            val record = CalculationHistory(
                assetClass = "CRYPTO",
                symbol = selectedCryptoAsset.symbol,
                accountBalance = getBalance(),
                riskPercentage = getRiskPercentage(),
                riskAmount = getRiskAmount(),
                stopLossValue = abs((cryptoEntryPriceInput.toDoubleOrNull() ?: 0.0) - (cryptoStopLossPriceInput.toDoubleOrNull() ?: 0.0)),
                entryPrice = cryptoEntryPriceInput.toDoubleOrNull(),
                stopLossPrice = cryptoStopLossPriceInput.toDoubleOrNull(),
                calculatedPositionSize = result.coins,
                positionValue = result.positionValue,
                isLong = isCryptoLong,
                notes = calculationNotes.trim()
            )
            repository.insert(record)
            calculationNotes = ""
        }
    }

    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
