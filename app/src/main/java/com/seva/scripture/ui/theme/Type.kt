package com.seva.scripture.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun scriptureTypography(fontScale: Float): Typography {
    return Typography(
        headlineLarge = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = (30f * fontScale).sp,
            lineHeight = (42f * fontScale).sp
        ),
        titleLarge = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = (24f * fontScale).sp,
            lineHeight = (36f * fontScale).sp
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontSize = (18f * fontScale).sp,
            lineHeight = (30f * fontScale).sp
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontSize = (16f * fontScale).sp,
            lineHeight = (26f * fontScale).sp
        ),
        labelLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = (14f * fontScale).sp
        )
    )
}
