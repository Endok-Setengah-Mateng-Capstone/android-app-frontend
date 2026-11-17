package id.sch.man3merangin.ujian.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private fun style(size: Int, line: Int, weight: FontWeight) = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp
)

// Nothing in the app goes below 12sp.
val Typography = Typography(
    titleLarge = style(22, 28, FontWeight.Bold),
    titleMedium = style(16, 22, FontWeight.SemiBold),
    titleSmall = style(14, 20, FontWeight.SemiBold),
    bodyLarge = style(16, 24, FontWeight.Normal),
    bodyMedium = style(14, 20, FontWeight.Normal),
    bodySmall = style(12, 16, FontWeight.Normal),
    labelLarge = style(14, 20, FontWeight.SemiBold),
    labelMedium = style(12, 16, FontWeight.SemiBold),
    labelSmall = style(12, 16, FontWeight.Medium)
)

/** For tokens, codes and the countdown. */
val MonoStyle = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
