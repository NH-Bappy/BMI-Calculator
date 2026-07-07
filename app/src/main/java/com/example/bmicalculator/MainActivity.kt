package com.example.bmicalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import java.util.Locale

// Constants for Theme Colors - Light Mode
val AppBackground = Color(0xFFFFFFFF)
val AppPrimary = Color(0xFF1A237E) // Deep Navy Blue
val AppSecondary = Color(0xFFF5F5F5) // Very Light Grey

class MainActivity : ComponentActivity() {
    /**
     * Entry point of the application.
     * Sets up the Material Theme and initializes the BMIApp composable.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = AppPrimary,
                    background = AppBackground,
                    surface = AppBackground,
                    onPrimary = Color.White,
                    onBackground = Color.Black
                )
            ) {
                BMIApp()
            }
        }
    }
}

/**
 * Main UI Composable for the BMI Calculator.
 * Manages state for user inputs (weight, height, age, units, gender) and displays the analysis.
 */
@Composable
fun BMIApp() {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var isMale by remember { mutableStateOf(true) }
    var isWeightKg by remember { mutableStateOf(true) }
    var isHeightCm by remember { mutableStateOf(true) }
    
    var result by remember { mutableStateOf<BMIResult?>(null) }
    var isCalculating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "BMI PRO",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = AppPrimary
                ),
                modifier = Modifier.padding(top = 24.dp)
            )

            // Input Section: Collects user data using custom input fields and toggles
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                UnitToggleRow(
                    label = "Gender",
                    options = listOf("MALE", "FEMALE"),
                    selectedIndex = if (isMale) 0 else 1,
                    onOptionSelected = { isMale = it == 0 }
                )

                SimpleInputField(
                    value = age,
                    onValueChange = { age = it },
                    label = "Age",
                    icon = Icons.Default.Cake
                )

                UnitToggleRow(
                    label = "Weight Unit",
                    options = listOf("KG", "LBS"),
                    selectedIndex = if (isWeightKg) 0 else 1,
                    onOptionSelected = { isWeightKg = it == 0 }
                )

                SimpleInputField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = if (isWeightKg) "Weight in KG" else "Weight in LBS",
                    icon = Icons.Default.Scale
                )

                UnitToggleRow(
                    label = "Height Unit",
                    options = listOf("CM", "FT"),
                    selectedIndex = if (isHeightCm) 0 else 1,
                    onOptionSelected = { isHeightCm = it == 0 }
                )

                SimpleInputField(
                    value = height,
                    onValueChange = { height = it },
                    label = if (isHeightCm) "Height in CM" else "Height in FT (e.g. 5.8)",
                    icon = Icons.Default.Height
                )
            }

            // Calculate Button: Triggers the BMI calculation logic with a simulated loading state
            Button(
                onClick = {
                    scope.launch {
                        isCalculating = true
                        delay(600) // Simulation of processing time
                        result = calculateBMIExtended(weight, height, isWeightKg, isHeightCm, age, isMale)
                        isCalculating = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
                shape = RoundedCornerShape(8.dp),
                enabled = !isCalculating
            ) {
                if (isCalculating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("ANALYZE", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // Result Display: Conditionally shows the BMI analysis card
            result?.let {
                ResultDisplay(it)
            }
        }
    }
}

/**
 * A custom row for switching between units or options (e.g., KG/LBS, MALE/FEMALE).
 */
@Composable
fun UnitToggleRow(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Black.copy(alpha = 0.6f), fontSize = 14.sp)
        Row(
            modifier = Modifier
                .background(AppSecondary, RoundedCornerShape(4.dp))
                .padding(2.dp)
        ) {
            options.forEachIndexed { index, text ->
                Box(
                    modifier = Modifier
                        .clickable { onOptionSelected(index) }
                        .background(
                            if (selectedIndex == index) AppPrimary else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = if (selectedIndex == index) Color.White else AppPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/**
 * A styled text input field restricted to decimal numbers.
 */
@Composable
fun SimpleInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) onValueChange(it) },
        label = { Text(label, color = Color.Black.copy(alpha = 0.4f)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        leadingIcon = { Icon(icon, contentDescription = null, tint = AppPrimary) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppPrimary,
            unfocusedBorderColor = Color.Black.copy(alpha = 0.1f),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = AppPrimary,
            focusedContainerColor = AppSecondary.copy(alpha = 0.3f),
            unfocusedContainerColor = AppSecondary.copy(alpha = 0.3f)
        ),
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
        )
    )
}

/**
 * Displays the complete analysis of the BMI result including metrics and dramatic messages.
 */
@Composable
fun ResultDisplay(result: BMIResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppSecondary, RoundedCornerShape(16.dp))
            .border(1.dp, AppPrimary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "ANALYSIS COMPLETE",
            color = AppPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        Text(
            text = result.profileSummary.uppercase(),
            color = Color.Black.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
        )

        Text(
            text = String.format(Locale.US, "BMI = %.2f", result.bmi),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        )

        Text(
            text = result.dramaMessage.uppercase(),
            color = if (result.category == "Normal") Color(0xFF2E7D32) else Color.Red,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Surface(
            color = AppPrimary,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = result.category.uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        HorizontalDivider(color = AppPrimary.copy(alpha = 0.1f), thickness = 1.dp)

        InfoRow("BMI Prime", String.format(Locale.US, "%.2f", result.bmiPrime))
        InfoRow("Ponderal Index", String.format(Locale.US, "%.2f kg/m³", result.ponderalIndex))
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("HEALTH METRICS", color = AppPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            MetricItem("Healthy BMI range", "18.5 kg/m² - 25 kg/m²")
            MetricItem("Healthy weight range", result.healthyWeightRange)
            MetricItem("Adjustment required", result.weightChangeMessage)
        }
    }
}

/**
 * A helper composable to show a label-value pair in a row.
 */
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Black.copy(alpha = 0.6f))
        Text(value, color = Color.Black, fontWeight = FontWeight.Bold)
    }
}

/**
 * A styled card to display a specific health metric.
 */
@Composable
fun MetricItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(label, color = Color.Black.copy(alpha = 0.5f), fontSize = 11.sp)
        Text(value, color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Data class representing the comprehensive results of a BMI calculation.
 */
data class BMIResult(
    val bmi: Double,
    val category: String,
    val bmiPrime: Double,
    val ponderalIndex: Double,
    val healthyWeightRange: String,
    val weightChangeMessage: String,
    val profileSummary: String,
    val dramaMessage: String
)

/**
 * Core Logic for BMI Calculation.
 * 1. Converts input weight (KG/LBS) and height (CM/FT) to metric units (KG and Meters).
 * 2. Calculates BMI using formula: weight(kg) / height(m)^2.
 * 3. Calculates auxiliary metrics: BMI Prime and Ponderal Index.
 * 4. Determines the health category and generates a dramatic feedback message.
 * 5. Calculates the ideal healthy weight range and necessary adjustment.
 */
fun calculateBMIExtended(
    weightStr: String,
    heightStr: String,
    isKg: Boolean,
    isCm: Boolean,
    age: String = "",
    isMale: Boolean = true
): BMIResult {
    val wInput = weightStr.toDoubleOrNull() ?: 0.0
    val hInput = heightStr.toDoubleOrNull() ?: 0.0

    // Unit Conversion Logic: Handles both Imperial and Metric systems
    val weightKg = if (isKg) wInput else wInput * 0.453592
    val heightM = if (isCm) hInput / 100.0 else hInput * 0.3048

    if (heightM <= 0 || weightKg <= 0) {
        return BMIResult(0.0, "Invalid", 0.0, 0.0, "-", "-", "", "INVALID INPUT!")
    }

    // Mathematical calculations
    val bmi = weightKg / (heightM * heightM)
    val bmiPrime = bmi / 25.0
    val ponderalIndex = weightKg / (heightM * heightM * heightM)

    // Ideal Range Calculation (WHO Standards: BMI 18.5 to 25.0)
    val minWeightKg = 18.5 * (heightM * heightM)
    val maxWeightKg = 25.0 * (heightM * heightM)

    val minWeightLbs = minWeightKg / 0.453592
    val maxWeightLbs = maxWeightKg / 0.453592

    val healthyRangeStr = String.format(Locale.US, "%.1f kg - %.1f kg (%.1f lbs - %.1f lbs)", 
        minWeightKg, maxWeightKg, minWeightLbs, maxWeightLbs)

    // Goal-oriented feedback
    val weightChangeMessage = when {
        bmi < 18.5 -> String.format(Locale.US, "Gain %.1f lbs to reach BMI 18.5", (minWeightKg - weightKg) / 0.453592)
        bmi > 25.0 -> String.format(Locale.US, "Lose %.1f lbs to reach BMI 25", (weightKg - maxWeightKg) / 0.453592)
        else -> "You are in a healthy weight range"
    }

    // Category Determination
    val category = when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "Normal"
        bmi < 30.0 -> "Overweight"
        else -> "Obese"
    }

    // Dramatic reaction messages based on BMI result
    val dramaMessage = when {
        bmi < 18.5 -> "OH MY GOD! YOU'RE PRACTICALLY INVISIBLE! EAT SOMETHING IMMEDIATELY!"
        bmi < 25.0 -> "YESSS! YOU'RE ABSOLUTELY PERFECT! DON'T CHANGE A THING!"
        bmi < 30.0 -> "OH MY GOD! YOU ARE SO OVERWEIGHT! TIME TO DROP THE SNACKS!"
        else -> "HOLY COW! THIS IS A CRISIS! YOU ARE MASSIVE! REBOOT YOUR LIFE!"
    }

    val profileSummary = if (age.isNotEmpty()) {
        "$age years old ${if (isMale) "Male" else "Female"}"
    } else {
        if (isMale) "Male" else "Female"
    }

    return BMIResult(
        bmi = bmi,
        category = category,
        bmiPrime = bmiPrime,
        ponderalIndex = ponderalIndex,
        healthyWeightRange = healthyRangeStr,
        weightChangeMessage = weightChangeMessage,
        profileSummary = profileSummary,
        dramaMessage = dramaMessage
    )
}
