package com.example.superid.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.superid.R

val PoppinsBold = FontFamily(Font(R.font.poppinsbold))
val InterRegular = FontFamily(Font(R.font.interregular))
val InterBold = FontFamily(Font(R.font.interbold))

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = InterRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PoppinsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PoppinsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ) ,
    labelSmall = TextStyle(
        fontFamily = InterRegular,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterBold,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PoppinsBold,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
)
