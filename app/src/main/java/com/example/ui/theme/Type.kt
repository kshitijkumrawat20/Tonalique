package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Custom Google Fonts installed locally in res/font
val DMSansFamily = FontFamily(
    Font(R.font.dm_sans, FontWeight.Normal),
    Font(R.font.dm_sans, FontWeight.Medium),
    Font(R.font.dm_sans, FontWeight.SemiBold),
    Font(R.font.dm_sans, FontWeight.Bold)
)

val ManropeFamily = FontFamily(
    Font(R.font.manrope, FontWeight.Normal),
    Font(R.font.manrope, FontWeight.Medium),
    Font(R.font.manrope, FontWeight.SemiBold),
    Font(R.font.manrope, FontWeight.Bold)
)

// Material 3 Typography aligned with Warm Editorial Beauty Studio design system
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = DMSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    ),
    displayMedium = TextStyle(
        fontFamily = DMSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = DMSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.015).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = DMSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.01).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = DMSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.01.sp
    ),
    bodySmall = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.01.sp
    ),
    labelLarge = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.02.sp
    ),
    labelMedium = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.03.sp
    ),
    labelSmall = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.04.sp
    )
)

// Extra style for Price Display specified in design guidelines
val PriceDisplayStyle = TextStyle(
    fontFamily = ManropeFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    letterSpacing = (-0.01).sp
)
