# BMI PRO - Professional BMI Calculator

BMI PRO is a modern, high-performance Android application built with Jetpack Compose. It provides comprehensive body mass index analysis with a sleek, minimalist design and personalized health metrics.

## Features

- **Unit Versatility**: Support for both Metric (KG, CM) and Imperial (LBS, FT) units with real-time conversion.
- **Detailed Analysis**: Beyond standard BMI, it calculates:
  - **BMI Prime**: Ratio of actual BMI to upper limit healthy BMI.
  - **Ponderal Index**: A 3D measure of body mass.
- **Health Metrics**:
  - Displays healthy weight ranges for your specific height.
  - Calculates exactly how much weight needs to be gained or lost to reach the "Normal" category.
- **Personalized Profile**: Takes Age and Gender into account for a complete profile summary.
- **Dynamic Feedback**: Features "Dramatic Messages" that react to your BMI result with high intensity.
- **Modern UI**: A clean, "Professional Light" theme using Deep Navy Blue and White, featuring smooth animations and a responsive layout.

## Logic Explanation

The core logic of the application resides in the `calculateBMIExtended` function:

1. **Unit Conversion**:
   - The application accepts input in any combination of KG/LBS and CM/FT.
   - It internally converts all inputs to the metric system (Kilograms and Meters) to ensure mathematical consistency.
   - Conversion factors used: `1 lb = 0.453592 kg` and `1 ft = 0.3048 m`.

2. **BMI Calculation**:
   - Formula: $BMI = \frac{weight (kg)}{height (m)^2}$

3. **Auxiliary Metrics**:
   - **BMI Prime**: $BMI / 25.0$ (standardized against the upper threshold of healthy BMI).
   - **Ponderal Index**: $\frac{weight (kg)}{height (m)^3}$.

4. **Healthy Range Calculation**:
   - Uses the WHO standard healthy BMI range (18.5 to 25.0).
   - Back-calculates the weight for the user's height: $Weight = BMI \times Height^2$.
   - This provides the "Healthy weight for the height" metric shown in the app.

5. **Category & Feedback**:
   - Uses a `when` expression to categorize the BMI result.
   - Triggers a specific `dramaMessage` based on the category to provide engaging user feedback.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material Design 3 (Material3)
- **Concurrency**: Kotlin Coroutines for simulated processing states.

## How to use

1. Select your **Gender**.
2. Enter your **Age**.
3. Toggle between **KG/LBS** and enter your weight.
4. Toggle between **CM/FT** and enter your height (use decimal for feet, e.g., 5.8).
5. Click **ANALYZE** to see your results!
