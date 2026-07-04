package com.example.calculator

import kotlin.math.abs

object PositionSizeCalculator {

    // Forex Pairs data
    data class ForexPair(
        val name: String,
        val quoteCurrency: String,
        val isJpy: Boolean = false,
        val isGold: Boolean = false,
        val defaultExchangeRate: Double,
        val pipDecimals: Int = 4
    )

    val forexPairs = listOf(
        ForexPair("EUR/USD", "USD", defaultExchangeRate = 1.0850, pipDecimals = 4),
        ForexPair("GBP/USD", "USD", defaultExchangeRate = 1.2650, pipDecimals = 4),
        ForexPair("AUD/USD", "USD", defaultExchangeRate = 0.6550, pipDecimals = 4),
        ForexPair("NZD/USD", "USD", defaultExchangeRate = 0.6100, pipDecimals = 4),
        ForexPair("USD/JPY", "JPY", isJpy = true, defaultExchangeRate = 155.20, pipDecimals = 2),
        ForexPair("EUR/JPY", "JPY", isJpy = true, defaultExchangeRate = 168.40, pipDecimals = 2),
        ForexPair("GBP/JPY", "JPY", isJpy = true, defaultExchangeRate = 196.30, pipDecimals = 2),
        ForexPair("USD/CAD", "CAD", defaultExchangeRate = 1.3650, pipDecimals = 4),
        ForexPair("USD/CHF", "CHF", defaultExchangeRate = 0.9050, pipDecimals = 4),
        ForexPair("EUR/GBP", "GBP", defaultExchangeRate = 0.8550, pipDecimals = 4),
        ForexPair("XAU/USD (Gold)", "USD", isGold = true, defaultExchangeRate = 2330.00, pipDecimals = 2),
        ForexPair("Custom Pair", "USD", defaultExchangeRate = 1.0000, pipDecimals = 4)
    )

    // Crypto Pairs data
    data class CryptoAsset(
        val name: String,
        val symbol: String,
        val defaultPrice: Double
    )

    val cryptoAssets = listOf(
        CryptoAsset("Bitcoin", "BTC/USD", 65000.0),
        CryptoAsset("Ethereum", "ETH/USD", 3500.0),
        CryptoAsset("Solana", "SOL/USD", 150.0),
        CryptoAsset("Cardano", "ADA/USD", 0.45),
        CryptoAsset("Ripple", "XRP/USD", 0.50),
        CryptoAsset("Dogecoin", "DOGE/USD", 0.12),
        CryptoAsset("Custom Crypto", "CUSTOM/USD", 1.0)
    )

    /**
     * Calculates Forex Position Size in Standard Lots.
     * 
     * Formula:
     * Lot Size = Risk Amount / (Stop Loss in Pips * Pip Value per Standard Lot)
     * 
     * Pip Value per Standard Lot (100,000 units) in USD:
     * - If Quote is USD: $10.00
     * - If Quote is JPY: 1000 JPY / (USD/JPY Rate)
     * - If Quote is CAD: 10 CAD / (USD/CAD Rate)
     * - If Quote is CHF: 10 CHF / (USD/CHF Rate)
     * - If Quote is GBP: 10 GBP * (GBP/USD Rate)
     * - For Gold: Standard Lot is 100 oz. 1 pip (0.01) is worth $1.00. (Or 1 full point ($1.00) is worth $100.00).
     *   We will define Stop Loss in Gold as "Pips/Points" where 10 pips = $1.00 price move.
     *   So 1 pip (0.10 price move) = $10.00 per Standard Lot (100 oz).
     */
    fun calculateForexLotSize(
        accountBalance: Double,
        riskPercentage: Double,
        stopLossInPips: Double,
        pair: ForexPair,
        customPairPipValue: Double = 10.0, // Let user define Pip value for custom pair
        exchangeRate: Double, // The rate of the selected pair
        usdJpyRate: Double = 155.20,
        usdCadRate: Double = 1.3650,
        usdChfRate: Double = 0.9050,
        gbpUsdRate: Double = 1.2650
    ): ForexResult {
        if (accountBalance <= 0 || riskPercentage <= 0 || stopLossInPips <= 0) {
            return ForexResult(0.0, 0.0, 0.0, 0.0)
        }

        val riskAmount = accountBalance * (riskPercentage / 100.0)

        val pipValueInUSD: Double = when {
            pair.name == "Custom Pair" -> customPairPipValue
            pair.isGold -> {
                // Gold Standard Lot = 100 oz. 
                // A 1 pip (0.10) change = $10.00. 
                10.0
            }
            pair.quoteCurrency == "USD" -> 10.0
            pair.isJpy -> {
                // Pip is 0.01. Standard Lot is 100k units. Pip Value in JPY is 1,000 JPY.
                // Converted to USD: 1000 / usdJpyRate
                if (usdJpyRate > 0) 1000.0 / usdJpyRate else 1000.0 / exchangeRate
            }
            pair.quoteCurrency == "CAD" -> {
                if (usdCadRate > 0) 10.0 / usdCadRate else 10.0 / exchangeRate
            }
            pair.quoteCurrency == "CHF" -> {
                if (usdChfRate > 0) 10.0 / usdChfRate else 10.0 / exchangeRate
            }
            pair.quoteCurrency == "GBP" -> {
                10.0 * gbpUsdRate
            }
            else -> 10.0
        }

        // Lot Size = Risk Amount / (SL * Pip Value)
        val rawLots = riskAmount / (stopLossInPips * pipValueInUSD)
        
        // Let's round to 2 decimal places (standard broker precision for lots)
        val lots = (rawLots * 100.0).toInt() / 100.0
        val miniLots = lots * 10.0
        val microLots = lots * 100.0
        val units = lots * 100_000.0

        return ForexResult(
            standardLots = lots,
            miniLots = miniLots,
            microLots = microLots,
            units = units,
            pipValueUsd = pipValueInUSD,
            riskAmount = riskAmount
        )
    }

    data class ForexResult(
        val standardLots: Double,
        val miniLots: Double,
        val microLots: Double,
        val units: Double,
        val pipValueUsd: Double = 0.0,
        val riskAmount: Double = 0.0
    )

    /**
     * Calculates Crypto Position Size in Coins.
     * 
     * Formula:
     * Position Size (Coins) = Risk Amount / abs(Entry Price - Stop Loss Price)
     * Position Value = Position Size (Coins) * Entry Price
     */
    fun calculateCryptoPositionSize(
        accountBalance: Double,
        riskPercentage: Double,
        entryPrice: Double,
        stopLossPrice: Double
    ): CryptoResult {
        if (accountBalance <= 0 || riskPercentage <= 0 || entryPrice <= 0 || stopLossPrice <= 0 || entryPrice == stopLossPrice) {
            return CryptoResult(0.0, 0.0, 0.0, 0.0)
        }

        val riskAmount = accountBalance * (riskPercentage / 100.0)
        val priceDiff = abs(entryPrice - stopLossPrice)

        val coins = riskAmount / priceDiff
        val positionValue = coins * entryPrice
        val leverageRequired = positionValue / accountBalance

        return CryptoResult(
            coins = coins,
            positionValue = positionValue,
            leverageRequired = leverageRequired,
            riskAmount = riskAmount
        )
    }

    data class CryptoResult(
        val coins: Double,
        val positionValue: Double,
        val leverageRequired: Double,
        val riskAmount: Double
    )
}
