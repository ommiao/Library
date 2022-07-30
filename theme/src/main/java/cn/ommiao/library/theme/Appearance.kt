package cn.ommiao.library.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import cn.ommiao.library.theme.foundation.CanonicalColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController

data class ColorPalette(
    val primary: Color,
    val backgroundPrimary: Color,
    val backgroundPrimaryBase: Color,
    val backgroundPrimaryLow: Color,
    val backgroundPrimaryHigh: Color,
    val backgroundSelected: Color,
    val textPrimary: Color,
    val textInverted: Color,
    val textSecondary: Color,
    val borderPrimary: Color,
    val borderSelected: Color
)

private val LocalColorPalette = compositionLocalOf<ColorPalette> {
    error("not provide, please use Theme {...}")
}

private val LocalTheme = compositionLocalOf<Appearance.Theme> {
    error("not provide, please use Theme {...}")
}

object Appearance {
    val colors: ColorPalette
        @Composable
        get() = LocalColorPalette.current

    val theme: Theme
        @Composable
        get() = LocalTheme.current

    enum class Theme {
        LIGHT,
        DARK,
        SYSTEM;

        @Composable
        fun getColors(): ColorPalette {
            return when (this) {
                LIGHT -> LightColorPalette
                DARK -> DarkColorPalette
                SYSTEM -> if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette
            }
        }

        @Composable
        fun darkSystemIcons(): Boolean {
            return when (this) {
                LIGHT -> true
                DARK -> false
                SYSTEM -> isSystemInDarkTheme().not()
            }
        }
    }

    fun lightColorPalette(palette: ColorPalette = LightColorPalette): ColorPalette {
        LightColorPalette = palette
        return LightColorPalette
    }

    fun darkColorPalette(palette: ColorPalette = DarkColorPalette): ColorPalette {
        DarkColorPalette = palette
        return DarkColorPalette
    }

    private var LightColorPalette = ColorPalette(
        primary = CanonicalColor.Ocean,
        backgroundPrimary = CanonicalColor.White,
        backgroundPrimaryBase = CanonicalColor.White,
        backgroundPrimaryLow = CanonicalColor.White,
        backgroundPrimaryHigh = CanonicalColor.White,
        backgroundSelected = CanonicalColor.Snowgum_600,
        textPrimary = CanonicalColor.Snowgum_600,
        textSecondary = CanonicalColor.Snowgum_400,
        textInverted = CanonicalColor.White,
        borderPrimary = CanonicalColor.Snowgum_300,
        borderSelected = CanonicalColor.Snowgum_600
    )

    private var DarkColorPalette = ColorPalette(
        primary = CanonicalColor.Ocean,
        backgroundPrimary = CanonicalColor.Snowgum_700,
        backgroundPrimaryBase = CanonicalColor.Snowgum_1000,
        backgroundPrimaryLow = CanonicalColor.Snowgum_900,
        backgroundPrimaryHigh = CanonicalColor.Snowgum_800,
        backgroundSelected = CanonicalColor.Snowgum_50,
        textPrimary = CanonicalColor.Snowgum_50,
        textSecondary = CanonicalColor.Snowgum_200,
        textInverted = CanonicalColor.Snowgum_600,
        borderPrimary = CanonicalColor.Snowgum_300,
        borderSelected = CanonicalColor.Snowgum_50
    )
}

@Composable
fun Theme(
    theme: Appearance.Theme = Appearance.Theme.SYSTEM,
    content: @Composable () -> Unit
) {
    SystemUiController(darkIcons = theme.darkSystemIcons())

    val targetColors = theme.getColors()
    val primary by animateColor(targetColors.primary)
    val backgroundPrimary by animateColor(targetColors.backgroundPrimary)
    val backgroundPrimaryBase by animateColor(targetColors.backgroundPrimaryBase)
    val backgroundPrimaryLow by animateColor(targetColors.backgroundPrimaryLow)
    val backgroundPrimaryHigh by animateColor(targetColors.backgroundPrimaryHigh)
    val backgroundSelected by animateColor(targetColors.backgroundSelected)
    val textPrimary by animateColor(targetColors.textPrimary)
    val textSecondary by animateColor(targetColors.textSecondary)
    val textInverted by animateColor(targetColors.textInverted)
    val borderPrimary by animateColor(targetColors.borderPrimary)
    val borderSelected by animateColor(targetColors.borderSelected)

    val colors = ColorPalette(
        primary = primary,
        backgroundPrimary = backgroundPrimary,
        backgroundPrimaryBase = backgroundPrimaryBase,
        backgroundPrimaryLow = backgroundPrimaryLow,
        backgroundPrimaryHigh = backgroundPrimaryHigh,
        backgroundSelected = backgroundSelected,
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        textInverted = textInverted,
        borderSelected = borderSelected,
        borderPrimary = borderPrimary
    )
    CompositionLocalProvider(
        LocalTheme provides theme,
        LocalColorPalette provides colors
    ) {
        MaterialTheme(
            colors = colors.toMaterialColors(),
            content = content
        )
    }
}

@Composable
fun ColorPalette.toMaterialColors() =
    MaterialTheme.colors.copy(
        primary = primary,
        background = backgroundPrimary,
        surface = backgroundPrimary
    )

@Composable
fun SystemUiController(darkIcons: Boolean) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(color = Color.Transparent, darkIcons = darkIcons)
    }
}

@Composable
fun animateColor(targetValue: Color) = animateColorAsState(
    targetValue = targetValue,
    animationSpec = TweenSpec(DURATION_THEME_COLOR_FADING)
)

const val DURATION_THEME_COLOR_FADING = 800
