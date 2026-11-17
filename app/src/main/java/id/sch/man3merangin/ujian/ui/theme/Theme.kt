package id.sch.man3merangin.ujian.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/** Spacing scale. Use these instead of ad-hoc dp values. */
object Space {
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val l = 16.dp
    val xl = 24.dp
}

/** Minimum touch target. */
val TapTarget = 48.dp

private val ColorScheme = lightColorScheme(
    primary = Blue,
    onPrimary = PaperRaised,
    primaryContainer = BlueTint,
    onPrimaryContainer = Navy,
    secondary = Navy,
    onSecondary = PaperRaised,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperMid,
    onSurfaceVariant = InkMuted,
    surfaceContainerLowest = PaperRaised,
    surfaceContainerLow = PaperLow,
    surfaceContainer = PaperMid,
    surfaceContainerHigh = PaperHigh,
    outline = Line,
    outlineVariant = LineSoft,
    error = Danger,
    onError = PaperRaised,
    errorContainer = DangerContainer,
    onErrorContainer = Danger
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun UjianAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
