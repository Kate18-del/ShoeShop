package com.example.shoeshop.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font

import androidx.compose.ui.unit.sp
import com.example.shoeshop.R

// Set of Material typography styles to start with

val ralewayRegular = FontFamily(
    Font(R.font.raleway, FontWeight.Normal)
)
val ralewayBold = FontFamily(
    Font(R.font.raleway_bold, FontWeight.Normal)
)
val ralewaySemiBold = FontFamily(
    Font(R.font.raleway_semibold, FontWeight.Normal)
)

val ralewayMedium = FontFamily(
    Font(R.font.raleway_medium, FontWeight.Normal)
)

val Typography = Typography(
    // Heading Regular 34
    displayLarge = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        lineHeight = 40.sp
    ),

    // Heading Regular 32
    displayMedium = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 38.sp
    ),

    // Heading Bold 30
    displaySmall = TextStyle(
        fontFamily = ralewayBold,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp
    ),

    // Heading Regular 26
    headlineLarge = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),

    // Heading Regular 20
    headlineMedium = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),

    // Body Regular 24
    headlineSmall = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 28.sp
    ),

    // Body SemiBold 18
    titleLarge = TextStyle(
        fontFamily = ralewaySemiBold,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),

    // Body Regular 20
    titleMedium = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),

    // Heading SemiBold 16
    titleSmall = TextStyle(
        fontFamily = ralewaySemiBold,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),

    // Body Medium 16 / Body Regular 16 / Subtitle Regular 16
    bodyLarge = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),

    // Body Medium 14
    bodyMedium = TextStyle(
        fontFamily = ralewayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),

    // Body Regular 14
    bodySmall = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),

    // Body Regular 12
    labelMedium = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

// Дополнительные кастомные стили для более точного соответствия
object AppTypography {
    // Heading стили
    val headingRegular34 = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        lineHeight = 40.sp
    )

    val headingRegular32 = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 38.sp
    )

    val headingBold30 = TextStyle(
        fontFamily = ralewayBold,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp
    )

    val headingRegular26 = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 32.sp
    )

    val headingRegular20 = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp
    )

    val headingSemiBold16 = TextStyle(
        fontFamily = ralewaySemiBold,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )

    // Subtitle
    val subtitleRegular16 = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )

    // Body стили
    val bodyRegular24 = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 28.sp
    )

    val bodyRegular20 = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp
    )

    val bodySemiBold18 = TextStyle(
        fontFamily = ralewaySemiBold,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )

    val bodyMedium16 = TextStyle(
        fontFamily = ralewayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )

    val bodyRegular16 = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )

    val bodyMedium14 = TextStyle(
        fontFamily = ralewayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )

    val bodyRegular14 = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )

    val bodyRegular12 = TextStyle(
        fontFamily = ralewayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
}