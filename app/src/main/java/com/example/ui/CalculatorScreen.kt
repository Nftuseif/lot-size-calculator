package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.calculator.PositionSizeCalculator
import com.example.data.CalculationHistory
import com.example.ui.theme.TradingGreen
import com.example.ui.theme.TradingRed
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.SoftGold
import com.example.ui.theme.SlateDark
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAdvancedForexRates by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AccentCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "SizeWise",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Position Calculator",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SlateDark,
                    titleContentColor = TextPrimary
                ),
                modifier = Modifier.border(0.dp, Color.Transparent)
            )
        },
        containerColor = SlateDark,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable { focusManager.clearFocus() }, // Tap outside to dismiss keyboard
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Visual High-Tech Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_trading_banner_1783172663647),
                        contentDescription = "Trading Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // High-tech translucent overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        SlateDark.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    // Headline HUD text overlay
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "AUTO RISK ESTIMATOR",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentCyan,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Precision Position Sizing",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Global Account Size and Risk Parameters Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "ACCOUNT PROFILE",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }

                        // Account Balance Input
                        OutlinedTextField(
                            value = viewModel.accountBalance,
                            onValueChange = { viewModel.accountBalance = it },
                            label = { Text("Account Balance (USD)") },
                            leadingIcon = {
                                Text(
                                    text = "$",
                                    color = AccentCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = AccentCyan
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("account_balance_input")
                        )

                        // Risk Input Mode Toggle Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SlateSurfaceVariant),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Risk by Percentage (%)",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.isRiskAmountMode = false }
                                    .background(if (!viewModel.isRiskAmountMode) AccentCyan.copy(alpha = 0.15f) else Color.Transparent)
                                    .padding(vertical = 10.dp),
                                textAlign = TextAlign.Center,
                                color = if (!viewModel.isRiskAmountMode) AccentCyan else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Risk by Cash Amount ($)",
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.isRiskAmountMode = true }
                                    .background(if (viewModel.isRiskAmountMode) AccentCyan.copy(alpha = 0.15f) else Color.Transparent)
                                    .padding(vertical = 10.dp),
                                textAlign = TextAlign.Center,
                                color = if (viewModel.isRiskAmountMode) AccentCyan else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        // Risk input based on selected mode
                        if (!viewModel.isRiskAmountMode) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Risk Percentage: ${viewModel.riskPercentage}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Equivalent: \$${String.format(Locale.US, "%.2f", viewModel.getRiskAmount())}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TradingRed,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Slider(
                                    value = viewModel.riskPercentage.toFloatOrNull() ?: 1f,
                                    onValueChange = { viewModel.riskPercentage = String.format(Locale.US, "%.1f", it).replace(",", ".") },
                                    valueRange = 0.1f..10.0f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = AccentCyan,
                                        activeTrackColor = AccentCyan,
                                        inactiveTrackColor = SlateSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("risk_slider")
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = viewModel.riskAmountOverride,
                                onValueChange = { viewModel.riskAmountOverride = it },
                                label = { Text("Risk Amount (USD)") },
                                leadingIcon = {
                                    Text(
                                        text = "$",
                                        color = TradingRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                },
                                trailingIcon = {
                                    Text(
                                        text = "${String.format(Locale.US, "%.2f", viewModel.getRiskPercentage())}%",
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TradingRed,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedLabelColor = TradingRed
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("risk_amount_input")
                            )
                        }
                    }
                }
            }

            // Asset Tabs (Forex vs Crypto vs AI Assistant)
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SlateDark,
                    contentColor = AccentCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AccentCyan,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "FOREX LOTS",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (selectedTab == 0) AccentCyan else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "CRYPTO PAIRS",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (selectedTab == 1) AccentCyan else TextSecondary
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                "AI ASSISTANT",
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (selectedTab == 2) AccentCyan else TextSecondary
                            )
                        }
                    )
                }
            }

            // Tab Content: FOREX LOT CALCULATOR
            if (selectedTab == 0) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Pair Dropdown Selector
                            var expandedDropdown by remember { mutableStateOf(false) }
                            Column {
                                Text(
                                    text = "Trading Pair",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SlateSurfaceVariant)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                        .clickable { expandedDropdown = true }
                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                                        .testTag("forex_pair_dropdown_trigger")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CurrencyExchange,
                                                contentDescription = null,
                                                tint = AccentCyan,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = viewModel.selectedForexPair.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        }
                                        Icon(
                                            imageVector = if (expandedDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = TextSecondary
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expandedDropdown,
                                        onDismissRequest = { expandedDropdown = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .background(SlateSurfaceVariant)
                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    ) {
                                        PositionSizeCalculator.forexPairs.forEach { pair ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(pair.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                        Text("Quote: ${pair.quoteCurrency}", color = TextSecondary, fontSize = 12.sp)
                                                    }
                                                },
                                                onClick = {
                                                    viewModel.onForexPairSelected(pair)
                                                    expandedDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Dynamic Quote Exchange Rate Input
                            OutlinedTextField(
                                value = viewModel.forexExchangeRateInput,
                                onValueChange = { viewModel.forexExchangeRateInput = it },
                                label = { Text("Current Exchange Rate (${viewModel.selectedForexPair.name})") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedLabelColor = AccentCyan
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("exchange_rate_input")
                            )

                            // Custom Pip Value Option (only shown for Custom Pair)
                            if (viewModel.selectedForexPair.name == "Custom Pair") {
                                OutlinedTextField(
                                    value = viewModel.customPairPipValueInput,
                                    onValueChange = { viewModel.customPairPipValueInput = it },
                                    label = { Text("Pip Value in USD (for 1 standard lot)") },
                                    leadingIcon = { Text("$", color = SoftGold, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SoftGold,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("custom_pip_value_input")
                                )
                            }

                            // Stop Loss Input in Pips
                            OutlinedTextField(
                                value = viewModel.forexStopLossPips,
                                onValueChange = { viewModel.forexStopLossPips = it },
                                label = { Text("Stop Loss (Pips)") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = TradingRed
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedLabelColor = AccentCyan
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("forex_sl_input")
                            )

                            // Collapsible Custom Conversions panel
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateSurfaceVariant)
                                    .clickable { showAdvancedForexRates = !showAdvancedForexRates }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = SoftGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Cross Conversion Rates",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SoftGold
                                        )
                                    }
                                    Icon(
                                        imageVector = if (showAdvancedForexRates) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = SoftGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                AnimatedVisibility(visible = showAdvancedForexRates) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            text = "These rates convert quote currencies (JPY, CAD, CHF, GBP) back to USD. Keep them updated with market rates for 100% accurate results.",
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            lineHeight = 15.sp
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = viewModel.usdJpyRateInput,
                                                onValueChange = { viewModel.usdJpyRateInput = it },
                                                label = { Text("USD/JPY", style = TextStyle(fontSize = 10.sp)) },
                                                textStyle = TextStyle(fontSize = 13.sp),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                            OutlinedTextField(
                                                value = viewModel.usdCadRateInput,
                                                onValueChange = { viewModel.usdCadRateInput = it },
                                                label = { Text("USD/CAD", style = TextStyle(fontSize = 10.sp)) },
                                                textStyle = TextStyle(fontSize = 13.sp),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = viewModel.usdChfRateInput,
                                                onValueChange = { viewModel.usdChfRateInput = it },
                                                label = { Text("USD/CHF", style = TextStyle(fontSize = 10.sp)) },
                                                textStyle = TextStyle(fontSize = 13.sp),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                            OutlinedTextField(
                                                value = viewModel.gbpUsdRateInput,
                                                onValueChange = { viewModel.gbpUsdRateInput = it },
                                                label = { Text("GBP/USD", style = TextStyle(fontSize = 10.sp)) },
                                                textStyle = TextStyle(fontSize = 13.sp),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                singleLine = true,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Forex Calculations Result HUD Card
                item {
                    val result = viewModel.forexResult
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(containerColor = AccentCyan) // Vibrant M3 Purple Background
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "CALCULATED POSITION SIZE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 2.sp
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = "${String.format(Locale.US, "%.2f", result.standardLots)} LOTS",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.testTag("calculated_lots_text")
                                    )
                                    Text(
                                        text = "Standard Lot Size",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }

                                // Semi-transparent divider line
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color.White.copy(alpha = 0.25f))
                                )

                                // Translucent glassmorphic breakdown specifications grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.12f))
                                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .padding(vertical = 10.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Mini Lots", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                        Text(
                                            text = String.format(Locale.US, "%.1f", result.miniLots),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.12f))
                                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .padding(vertical = 10.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Micro Lots", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                        Text(
                                            text = String.format(Locale.US, "%.1f", result.microLots),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.12f))
                                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .padding(vertical = 10.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Total Units", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                        Text(
                                            text = String.format(Locale.US, "%,.0f", result.units),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Pip Value: ", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                        Text(
                                            text = "\$${String.format(Locale.US, "%.2f", result.pipValueUsd)}",
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Risk: ", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                        Text(
                                            text = "\$${String.format(Locale.US, "%.2f", result.riskAmount)}",
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        // Notes & Save Button rendered cleanly on the canvas below the card
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = viewModel.calculationNotes,
                                onValueChange = { viewModel.calculationNotes = it },
                                label = { Text("Add Trade Notes (Optional)") },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 13.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedLabelColor = AccentCyan
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = { viewModel.saveForexCalculation() },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                shape = RoundedCornerShape(12.dp),
                                enabled = result.standardLots > 0,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("save_forex_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LOG CALCULATION TO JOURNAL",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Tab Content: CRYPTO PAIRS CALCULATOR
            if (selectedTab == 1) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Crypto Dropdown Selector
                            var expandedCrypto by remember { mutableStateOf(false) }
                            Column {
                                Text(
                                    text = "Select Crypto Asset",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SlateSurfaceVariant)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                        .clickable { expandedCrypto = true }
                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                                        .testTag("crypto_asset_dropdown_trigger")
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.MonetizationOn,
                                                contentDescription = null,
                                                tint = AccentCyan,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = viewModel.selectedCryptoAsset.symbol,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        }
                                        Icon(
                                            imageVector = if (expandedCrypto) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = TextSecondary
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = expandedCrypto,
                                        onDismissRequest = { expandedCrypto = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.85f)
                                            .background(SlateSurfaceVariant)
                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    ) {
                                        PositionSizeCalculator.cryptoAssets.forEach { asset ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(asset.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                        Text(asset.symbol, color = TextSecondary, fontSize = 12.sp)
                                                    }
                                                },
                                                onClick = {
                                                    viewModel.onCryptoAssetSelected(asset)
                                                    expandedCrypto = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Order Direction Button Toggle (Long / Buy vs Short / Sell)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateSurfaceVariant),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { if (!viewModel.isCryptoLong) viewModel.toggleCryptoDirection() }
                                        .background(if (viewModel.isCryptoLong) TradingGreen.copy(alpha = 0.2f) else Color.Transparent)
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = if (viewModel.isCryptoLong) TradingGreen else TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "LONG / BUY",
                                            fontWeight = FontWeight.Bold,
                                            color = if (viewModel.isCryptoLong) TradingGreen else TextSecondary,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { if (viewModel.isCryptoLong) viewModel.toggleCryptoDirection() }
                                        .background(if (!viewModel.isCryptoLong) TradingRed.copy(alpha = 0.2f) else Color.Transparent)
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDownward,
                                            contentDescription = null,
                                            tint = if (!viewModel.isCryptoLong) TradingRed else TextSecondary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "SHORT / SELL",
                                            fontWeight = FontWeight.Bold,
                                            color = if (!viewModel.isCryptoLong) TradingRed else TextSecondary,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            // Entry Price Input
                            OutlinedTextField(
                                value = viewModel.cryptoEntryPriceInput,
                                onValueChange = { viewModel.cryptoEntryPriceInput = it },
                                label = { Text("Entry Price (USD)") },
                                leadingIcon = { Text("$", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedLabelColor = AccentCyan
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("crypto_entry_input")
                            )

                            // Stop Loss Price Input
                            val cryptoEntryVal = viewModel.cryptoEntryPriceInput.toDoubleOrNull() ?: 0.0
                            val cryptoSlVal = viewModel.cryptoStopLossPriceInput.toDoubleOrNull() ?: 0.0
                            val percentageSl = if (cryptoEntryVal > 0) abs(cryptoEntryVal - cryptoSlVal) / cryptoEntryVal * 100.0 else 0.0

                            OutlinedTextField(
                                value = viewModel.cryptoStopLossPriceInput,
                                onValueChange = { viewModel.cryptoStopLossPriceInput = it },
                                label = { Text("Stop Loss Price (USD)") },
                                leadingIcon = { Text("$", color = TradingRed, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                                trailingIcon = {
                                    Text(
                                        text = "${String.format(Locale.US, "%.2f", percentageSl)}% off",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TradingRed,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedLabelColor = TradingRed
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("crypto_sl_input")
                            )
                        }
                    }
                }

                // Crypto Position Results HUD Card
                item {
                    val result = viewModel.cryptoResult
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp)),
                            colors = CardDefaults.cardColors(containerColor = AccentCyan) // Vibrant M3 Purple Background
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "CALCULATED CRYPTO POSITION",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 2.sp
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = "${String.format(Locale.US, "%.4f", result.coins)} ${viewModel.selectedCryptoAsset.symbol.substringBefore("/")}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.testTag("calculated_crypto_size_text")
                                    )
                                    Text(
                                        text = "Suggested Position Size",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }

                                // Semi-transparent divider line
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color.White.copy(alpha = 0.25f))
                                )

                                // Translucent glassmorphic leverage/exposure breakdown
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.12f))
                                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .padding(vertical = 10.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Position Value", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                        Text(
                                            text = "\$${String.format(Locale.US, "%,.2f", result.positionValue)}",
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.12f))
                                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .padding(vertical = 10.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Leverage Req.", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                        Text(
                                            text = "${String.format(Locale.US, "%.2f", result.leverageRequired)}x",
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.12f))
                                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .padding(vertical = 10.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Risk Capital", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                        Text(
                                            text = "\$${String.format(Locale.US, "%.2f", result.riskAmount)}",
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Notes & Save Button rendered cleanly on the canvas below the card
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = viewModel.calculationNotes,
                                onValueChange = { viewModel.calculationNotes = it },
                                label = { Text("Add Trade Notes (Optional)") },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 13.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentCyan,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedLabelColor = AccentCyan
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = { viewModel.saveCryptoCalculation() },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                shape = RoundedCornerShape(12.dp),
                                enabled = result.coins > 0,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("save_crypto_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LOG CALCULATION TO JOURNAL",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            if (selectedTab == 2) {
                item {
                    AiSupportChatCard(viewModel = viewModel)
                }
            }

            // Journal / Saved History Title
            if (selectedTab != 2) {
                item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = SoftGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TRADING JOURNAL LOGS (${historyList.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SoftGold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    if (historyList.isNotEmpty()) {
                        Text(
                            text = "CLEAR ALL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TradingRed,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .clickable { viewModel.clearHistory() }
                                .padding(4.dp)
                                .testTag("clear_history_button")
                        )
                    }
                }
            }

            // Empty state placeholder
            if (historyList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = SlateSurface.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = TextSecondary.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Your calculations journal is empty",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Enter trade parameters above and tap log button.",
                                fontSize = 11.sp,
                                color = TextSecondary.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Calculations History Cards List
            items(historyList, key = { it.id }) { item ->
                AnimatedVisibility(
                    visible = true,
                    exit = shrinkVertically(animationSpec = spring()) + fadeOut()
                ) {
                    HistoryCardItem(
                        item = item,
                        onDeleteClick = { viewModel.deleteHistoryItem(item.id) }
                    )
                }
            }
        }

            // Add bottom navigation safe padding spacing so list items are fully viewable
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HistoryCardItem(
    item: CalculationHistory,
    onDeleteClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }
    val isForex = item.assetClass == "FOREX"
    val accentColor = if (isForex) TradingGreen else AccentCyan

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .testTag("history_item_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = SlateSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Symbol & Direction tag & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Type Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .border(0.5.dp, accentColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.assetClass,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.symbol,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    
                    if (!isForex) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (item.isLong) TradingGreen.copy(alpha = 0.15f) else TradingRed.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (item.isLong) "LONG" else "SHORT",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isLong) TradingGreen else TradingRed,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dateFormatter.format(Date(item.timestamp)),
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete record",
                            tint = TradingRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main output metric
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SlateSurfaceVariant)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CALCULATED SIZE:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = if (isForex) "${String.format(Locale.US, "%.2f", item.calculatedPositionSize)} LOTS" else "${String.format(Locale.US, "%.4f", item.calculatedPositionSize)} ${item.symbol.substringBefore("/")}",
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = accentColor,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Parameter details list
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Account Bal.", fontSize = 10.sp, color = TextSecondary)
                    Text(
                        "\$${String.format(Locale.US, "%,.0f", item.accountBalance)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Risk Capital", fontSize = 10.sp, color = TextSecondary)
                    Text(
                        "\$${String.format(Locale.US, "%.2f", item.riskAmount)} (${String.format(Locale.US, "%.1f", item.riskPercentage)}%)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TradingRed
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(if (isForex) "Stop Loss" else "SL Price", fontSize = 10.sp, color = TextSecondary)
                    Text(
                        if (isForex) "${item.stopLossValue.toInt()} Pips" else "\$${String.format(Locale.US, "%.2f", item.stopLossPrice ?: 0.0)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }
            }

            // Note string if present
            if (item.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderSlate.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .background(SlateDark.copy(alpha = 0.3f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SoftGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.notes,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// Convenient custom type aliases for colors
private val TextPrimary = Color(0xFFECEFF1)
private val TextSecondary = Color(0xFF90A4AE)
private val BorderSlate = Color(0xFF2C384A)

@Composable
fun AiSupportChatCard(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val messages = viewModel.chatMessages
    val isLoading = viewModel.isChatLoading
    val inputText = viewModel.chatInputText
    val errorMsg = viewModel.chatError

    val scrollState = rememberScrollState()

    // Scroll to bottom when a new message is received or loading state changes
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(1.dp, BorderSlate, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SlateSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isLoading) SoftGold else TradingGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLoading) "SizeWise AI is typing..." else "SizeWise AI Support (Active)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLoading) SoftGold else TradingGreen,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Reset Button
                Text(
                    text = "RESET CHAT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TradingRed,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .clickable {
                            viewModel.chatMessages = listOf(
                                ChatMessage(
                                    text = "Hello! I am SizeWise AI, your risk management and position sizing assistant. How can I help you manage your trading risk today?",
                                    isUser = false
                                )
                            )
                        }
                        .padding(4.dp)
                )
            }

            // Quick Prompt / Suggestions horizontal scroll row
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Suggested Questions:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val suggestions = listOf(
                        "📊 Analyze current risk settings",
                        "📉 Explain standard/mini/micro lots",
                        "⚙️ How does stop loss protect me?",
                        "💼 Troubleshoot leverage risk"
                    )
                    items(suggestions) { prompt ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .background(SlateDark.copy(alpha = 0.5f))
                                .clickable(enabled = !isLoading) { viewModel.selectQuickPrompt(prompt) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = prompt,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AccentCyan
                            )
                        }
                    }
                }
            }

            // Chat Messages Thread area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateDark.copy(alpha = 0.5f))
                    .border(1.dp, BorderSlate, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    messages.forEach { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            if (!msg.isUser) {
                                // Agent Avatar Icon
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(AccentCyan.copy(alpha = 0.2f))
                                        .border(1.dp, AccentCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = null,
                                        tint = AccentCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            // Message Bubble
                            Box(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .widthIn(max = 260.dp)
                                    .clip(
                                        if (msg.isUser) {
                                            RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp)
                                        } else {
                                            RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp)
                                        }
                                    )
                                    .background(
                                        if (msg.isUser) AccentCyan.copy(alpha = 0.25f) else SlateSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (msg.isUser) AccentCyan else BorderSlate,
                                        shape = if (msg.isUser) {
                                            RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp)
                                        } else {
                                            RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp)
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    lineHeight = 18.sp
                                )
                            }

                            if (msg.isUser) {
                                Spacer(modifier = Modifier.width(8.dp))
                                // User Avatar Icon
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(TradingGreen.copy(alpha = 0.2f))
                                        .border(1.dp, TradingGreen, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = TradingGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (isLoading) {
                        // Thinking/Typing placeholder
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(AccentCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = AccentCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp))
                                    .background(SlateSurfaceVariant.copy(alpha = 0.3f))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Analyzing trading context...",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { viewModel.chatInputText = it },
                    placeholder = { Text("Ask support agent...", fontSize = 13.sp, color = TextSecondary) },
                    singleLine = true,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSlate,
                        focusedLabelColor = AccentCyan
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text_field"),
                    textStyle = TextStyle(fontSize = 13.sp, color = TextPrimary)
                )

                IconButton(
                    onClick = { viewModel.sendChatMessage(inputText) },
                    enabled = !isLoading && inputText.isNotBlank(),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (inputText.isNotBlank() && !isLoading) AccentCyan else SlateSurfaceVariant)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = if (inputText.isNotBlank() && !isLoading) Color.White else TextSecondary
                    )
                }
            }
        }
    }
}
