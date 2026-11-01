package com.yourname.savor.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import recipeapp.shared.generated.resources.*


@Composable
fun plusJakartaSans(): FontFamily = FontFamily.Default

@Composable
fun appTypography(): Typography {
    val f = plusJakartaSans()
    return Typography(
        // display-lg (40) та display-lg-mobile (32)
        displayLarge = TextStyle(
            fontFamily = f, fontWeight = FontWeight.ExtraBold,
            fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = (-0.03).em
        ),
        displayMedium = TextStyle(
            fontFamily = f, fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp, lineHeight = 38.sp, letterSpacing = (-0.025).em
        ),
        // headline-lg / md / sm
        headlineLarge = TextStyle(
            fontFamily = f, fontWeight = FontWeight.Bold,
            fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.02).em
        ),
        headlineMedium = TextStyle(
            fontFamily = f, fontWeight = FontWeight.Bold,
            fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.015).em
        ),
        headlineSmall = TextStyle(
            fontFamily = f, fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = (-0.01).em
        ),
        titleLarge = TextStyle(
            fontFamily = f, fontWeight = FontWeight.Bold,
            fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.015).em
        ),
        titleMedium = TextStyle(
            fontFamily = f, fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp, lineHeight = 24.sp, letterSpacing = (-0.01).em
        ),
        titleSmall = TextStyle(
            fontFamily = f, fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = 0.01.em
        ),

        bodyLarge = TextStyle(
            fontFamily = f, fontWeight = FontWeight.Normal,
            fontSize = 17.sp, lineHeight = 26.sp, letterSpacing = (-0.005).em
        ),
        bodyMedium = TextStyle(
            fontFamily = f, fontWeight = FontWeight.Normal,
            fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.em
        ),
        bodySmall = TextStyle(
            fontFamily = f, fontWeight = FontWeight.Normal,
            fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.em
        ),
        // label-lg / md / sm
        labelLarge = TextStyle(
            fontFamily = f, fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp, lineHeight = 20.sp, letterSpacing = (-0.01).em
        ),
        labelMedium = TextStyle(
            fontFamily = f, fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = 0.01.em
        ),
        labelSmall = TextStyle(
            fontFamily = f, fontWeight = FontWeight.Bold,
            fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.03.em
        ),
    )
}
