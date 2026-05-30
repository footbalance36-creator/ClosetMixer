package com.closetmixer.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary               = Primary,
    onPrimary             = OnPrimary,
    primaryContainer      = PrimaryContainer,
    onPrimaryContainer    = OnPrimaryContainer,
    secondary             = Secondary,
    onSecondary           = OnSecondary,
    secondaryContainer    = SecondaryContainer,
    onSecondaryContainer  = OnSecondaryContainer,
    tertiary              = Tertiary,
    onTertiary            = OnTertiary,
    tertiaryContainer     = TertiaryContainer,
    onTertiaryContainer   = OnTertiaryContainer,
    error                 = Error,
    onError               = OnError,
    errorContainer        = ErrorContainer,
    onErrorContainer      = OnErrorContainer,
    background            = Background,
    onBackground          = OnBackground,
    surface               = Surface,
    onSurface             = OnSurface,
    onSurfaceVariant      = OnSurfaceVariant,
    surfaceVariant        = SurfaceVariant,
    outline               = Outline,
    outlineVariant        = OutlineVariant,
    inverseSurface        = InverseSurface,
    inverseOnSurface      = InverseOnSurface,
    inversePrimary        = InversePrimary,
    surfaceTint           = SurfaceTint,
)

private val DarkColorScheme = darkColorScheme(
    primary               = PrimaryDark,
    onPrimary             = OnPrimaryDark,
    primaryContainer      = PrimaryContainerDark,
    onPrimaryContainer    = OnPrimaryContainerDark,
    secondary             = SecondaryDark,
    onSecondary           = OnSecondaryDark,
    secondaryContainer    = SecondaryContainerDark,
    onSecondaryContainer  = OnSecondaryContainerDark,
    tertiary              = Tertiary,
    onTertiary            = OnTertiary,
    tertiaryContainer     = TertiaryContainer,
    onTertiaryContainer   = OnTertiaryContainer,
    error                 = Error,
    onError               = OnError,
    errorContainer        = ErrorContainer,
    onErrorContainer      = OnErrorContainer,
    background            = DarkBackground,
    onBackground          = DarkOnBackground,
    surface               = DarkSurface,
    onSurface             = DarkOnSurface,
    onSurfaceVariant      = DarkOnSurfaceVariant,
    surfaceVariant        = DarkSurfaceVariant,
    outline               = DarkOutline,
    outlineVariant        = DarkOutlineVariant,
    inverseSurface        = DarkInverseSurface,
    inverseOnSurface      = DarkInverseOnSurface,
    inversePrimary        = Primary,
)

@Composable
fun ClosetMixerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
