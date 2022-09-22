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
        backgroundBlue = CanonicalColor.Bondi50,
        backgroundBrandStatic = CanonicalColor.Ocean,
        backgroundDisabled = CanonicalColor.Snowgum50,
        backgroundFocusedInvertedStatic = CanonicalColor.Snowgum100,
        backgroundFocusedPrimary = CanonicalColor.Snowgum50,
        backgroundGreen = CanonicalColor.Eucalyptus50,
        backgroundHoveredBrandStatic = CanonicalColor.Ocean105,
        backgroundHoveredPrimaryInverted = CanonicalColor.Snowgum800,
        backgroundHoveredPrimaryInvertedStatic = CanonicalColor.Snowgum800,
        backgroundHoveredPrimary = CanonicalColor.Snowgum50,
        backgroundHoveredSecondary = CanonicalColor.Snowgum200,
        backgroundInformation = CanonicalColor.Bondi50,
        backgroundOrange = CanonicalColor.Outback50,
        backgroundPink = CanonicalColor.Coral50,
        backgroundPressedBrandStatic = CanonicalColor.Ocean105,
        backgroundPressedPrimary = CanonicalColor.Snowgum50,
        backgroundPressedPrimaryInverted = CanonicalColor.Snowgum800,
        backgroundPressedPrimaryInvertedStatic = CanonicalColor.Snowgum800,
        backgroundPressedSecondary = CanonicalColor.Snowgum200,
        backgroundPrimaryBase = CanonicalColor.White,
        backgroundPrimaryHigh = CanonicalColor.White,
        backgroundPrimaryInverted = CanonicalColor.Snowgum600,
        backgroundPrimaryInvertedStatic = CanonicalColor.Snowgum600,
        backgroundPrimary = CanonicalColor.White,
        backgroundPrimaryLow = CanonicalColor.White,
        backgroundPurple = CanonicalColor.Jacaranda50,
        backgroundSecondaryBase = CanonicalColor.Snowgum50,
        backgroundSecondaryInvertedStatic = CanonicalColor.Snowgum800,
        backgroundSecondary = CanonicalColor.Snowgum100,
        backgroundSelected = CanonicalColor.Snowgum600,
        backgroundSuccess = CanonicalColor.Eucalyptus50,
        backgroundTeal = CanonicalColor.Whitsundays50,
        backgroundWarning = CanonicalColor.Outback50,
        backgroundYellow = CanonicalColor.Sorrento50,
        borderBrandStatic = CanonicalColor.Ocean,
        borderDisabled = CanonicalColor.Snowgum100,
        borderErrored = CanonicalColor.Outback500,
        borderFocused = CanonicalColor.Snowgum600,
        borderFocusedInvertedStatic = CanonicalColor.Snowgum50,
        borderHovered = CanonicalColor.Snowgum600,
        borderHoveredInverted = CanonicalColor.Snowgum50,
        borderInverted = CanonicalColor.White,
        borderInvertedStatic = CanonicalColor.Snowgum300,
        borderPrimary = CanonicalColor.Snowgum300,
        borderPressed = CanonicalColor.Snowgum50,
        borderPressedInverted = CanonicalColor.Snowgum600,
        borderSecondary = CanonicalColor.Snowgum100,
        borderSelected = CanonicalColor.Snowgum600,
        graphicBlue = CanonicalColor.Bondi400,
        graphicGreen = CanonicalColor.Eucalyptus400,
        graphicOrange = CanonicalColor.Outback300,
        graphicPink = CanonicalColor.Coral300,
        graphicPurple = CanonicalColor.Jacaranda400,
        graphicTeal = CanonicalColor.Whitsundays300,
        graphicYellow = CanonicalColor.Sorrento200,
        linkPrimary = CanonicalColor.Bondi400,
        linkSecondary = CanonicalColor.Snowgum400,
        textBlue = CanonicalColor.Bondi500,
        textBrand = CanonicalColor.Ocean,
        textDisabled = CanonicalColor.Snowgum200,
        textGreen = CanonicalColor.Eucalyptus500,
        textInformation = CanonicalColor.Bondi500,
        textInverted = CanonicalColor.White,
        textInvertedStatic = CanonicalColor.White,
        textOrange = CanonicalColor.Outback500,
        textPink = CanonicalColor.Coral500,
        textPrimary = CanonicalColor.Snowgum600,
        textPurple = CanonicalColor.Jacaranda500,
        textSecondary = CanonicalColor.Snowgum400,
        textSuccess = CanonicalColor.Eucalyptus500,
        textTeal = CanonicalColor.Whitsundays500,
        textYellow = CanonicalColor.Sorrento500,
        textWarning = CanonicalColor.Outback500
    )

    private var DarkColorPalette = ColorPalette(
        backgroundBlue = CanonicalColor.Bondi700,
        backgroundBrandStatic = CanonicalColor.Ocean,
        backgroundDisabled = CanonicalColor.Snowgum600,
        backgroundFocusedInvertedStatic = CanonicalColor.Snowgum100,
        backgroundFocusedPrimary = CanonicalColor.Snowgum500,
        backgroundGreen = CanonicalColor.Eucalyptus700,
        backgroundHoveredBrandStatic = CanonicalColor.Ocean95,
        backgroundHoveredPrimaryInverted = CanonicalColor.Snowgum50,
        backgroundHoveredPrimaryInvertedStatic = CanonicalColor.Snowgum500,
        backgroundHoveredPrimary = CanonicalColor.Snowgum600,
        backgroundHoveredSecondary = CanonicalColor.Snowgum500,
        backgroundInformation = CanonicalColor.Bondi700,
        backgroundOrange = CanonicalColor.Outback700,
        backgroundPink = CanonicalColor.Coral700,
        backgroundPressedBrandStatic = CanonicalColor.Ocean95,
        backgroundPressedPrimary = CanonicalColor.Snowgum600,
        backgroundPressedPrimaryInverted = CanonicalColor.Snowgum50,
        backgroundPressedPrimaryInvertedStatic = CanonicalColor.Snowgum500,
        backgroundPressedSecondary = CanonicalColor.Snowgum500,
        backgroundPrimaryBase = CanonicalColor.Snowgum1000,
        backgroundPrimaryHigh = CanonicalColor.Snowgum800,
        backgroundPrimaryInverted = CanonicalColor.Snowgum100,
        backgroundPrimaryInvertedStatic = CanonicalColor.Snowgum600,
        backgroundPrimary = CanonicalColor.Snowgum700,
        backgroundPrimaryLow = CanonicalColor.Snowgum900,
        backgroundPurple = CanonicalColor.Jacaranda700,
        backgroundSecondaryBase = CanonicalColor.Snowgum1000,
        backgroundSecondaryInvertedStatic = CanonicalColor.Snowgum800,
        backgroundSecondary = CanonicalColor.Snowgum600,
        backgroundSelected = CanonicalColor.Snowgum50,
        backgroundSuccess = CanonicalColor.Eucalyptus700,
        backgroundTeal = CanonicalColor.Whitsundays700,
        backgroundWarning = CanonicalColor.Outback700,
        backgroundYellow = CanonicalColor.Sorrento700,
        borderBrandStatic = CanonicalColor.Ocean,
        borderDisabled = CanonicalColor.Snowgum500,
        borderErrored = CanonicalColor.Outback300,
        borderFocused = CanonicalColor.Snowgum50,
        borderFocusedInvertedStatic = CanonicalColor.Snowgum50,
        borderHovered = CanonicalColor.Snowgum200,
        borderHoveredInverted = CanonicalColor.Snowgum500,
        borderInverted = CanonicalColor.Snowgum600,
        borderInvertedStatic = CanonicalColor.Snowgum300,
        borderPrimary = CanonicalColor.Snowgum300,
        borderPressed = CanonicalColor.Snowgum200,
        borderPressedInverted = CanonicalColor.Snowgum500,
        borderSecondary = CanonicalColor.Snowgum500,
        borderSelected = CanonicalColor.Snowgum50,
        graphicBlue = CanonicalColor.Bondi400,
        graphicGreen = CanonicalColor.Eucalyptus400,
        graphicOrange = CanonicalColor.Outback400,
        graphicPink = CanonicalColor.Coral400,
        graphicPurple = CanonicalColor.Jacaranda400,
        graphicTeal = CanonicalColor.Whitsundays400,
        graphicYellow = CanonicalColor.Sorrento300,
        linkPrimary = CanonicalColor.Bondi300,
        linkSecondary = CanonicalColor.Snowgum300,
        textBlue = CanonicalColor.Bondi300,
        textBrand = CanonicalColor.Snowgum50,
        textDisabled = CanonicalColor.Snowgum500,
        textGreen = CanonicalColor.Eucalyptus300,
        textInformation = CanonicalColor.Bondi300,
        textInverted = CanonicalColor.Snowgum600,
        textInvertedStatic = CanonicalColor.Snowgum50,
        textOrange = CanonicalColor.Outback300,
        textPink = CanonicalColor.Coral300,
        textPrimary = CanonicalColor.Snowgum50,
        textPurple = CanonicalColor.Jacaranda300,
        textSecondary = CanonicalColor.Snowgum200,
        textSuccess = CanonicalColor.Eucalyptus300,
        textTeal = CanonicalColor.Whitsundays300,
        textYellow = CanonicalColor.Sorrento200,
        textWarning = CanonicalColor.Outback300
    )
}

@Composable
fun Theme(
    theme: Appearance.Theme = Appearance.Theme.SYSTEM,
    content: @Composable () -> Unit
) {
    SystemUiController(darkIcons = theme.darkSystemIcons())

    val targetColors = theme.getColors()
    val backgroundBlue by animateColor(targetColors.backgroundBlue)
    val backgroundBrandStatic by animateColor(targetColors.backgroundBrandStatic)
    val backgroundDisabled by animateColor(targetColors.backgroundDisabled)
    val backgroundFocusedInvertedStatic by animateColor(targetColors.backgroundFocusedInvertedStatic)
    val backgroundFocusedPrimary by animateColor(targetColors.backgroundFocusedPrimary)
    val backgroundGreen by animateColor(targetColors.backgroundGreen)
    val backgroundHoveredBrandStatic by animateColor(targetColors.backgroundHoveredBrandStatic)
    val backgroundHoveredPrimaryInverted by animateColor(targetColors.backgroundHoveredPrimaryInverted)
    val backgroundHoveredPrimaryInvertedStatic by animateColor(targetColors.backgroundHoveredPrimaryInvertedStatic)
    val backgroundHoveredPrimary by animateColor(targetColors.backgroundHoveredPrimary)
    val backgroundHoveredSecondary by animateColor(targetColors.backgroundHoveredSecondary)
    val backgroundInformation by animateColor(targetColors.backgroundInformation)
    val backgroundOrange by animateColor(targetColors.backgroundOrange)
    val backgroundPink by animateColor(targetColors.backgroundPink)
    val backgroundPressedBrandStatic by animateColor(targetColors.backgroundPressedBrandStatic)
    val backgroundPressedPrimary by animateColor(targetColors.backgroundPressedPrimary)
    val backgroundPressedPrimaryInverted by animateColor(targetColors.backgroundPressedPrimaryInverted)
    val backgroundPressedPrimaryInvertedStatic by animateColor(targetColors.backgroundPressedPrimaryInvertedStatic)
    val backgroundPressedSecondary by animateColor(targetColors.backgroundPressedSecondary)
    val backgroundPrimaryBase by animateColor(targetColors.backgroundPrimaryBase)
    val backgroundPrimaryHigh by animateColor(targetColors.backgroundPrimaryHigh)
    val backgroundPrimaryInverted by animateColor(targetColors.backgroundPrimaryInverted)
    val backgroundPrimaryInvertedStatic by animateColor(targetColors.backgroundPrimaryInvertedStatic)
    val backgroundPrimary by animateColor(targetColors.backgroundPrimary)
    val backgroundPrimaryLow by animateColor(targetColors.backgroundPrimaryLow)
    val backgroundPurple by animateColor(targetColors.backgroundPurple)
    val backgroundSecondaryBase by animateColor(targetColors.backgroundSecondaryBase)
    val backgroundSecondaryInvertedStatic by animateColor(targetColors.backgroundSecondaryInvertedStatic)
    val backgroundSecondary by animateColor(targetColors.backgroundSecondary)
    val backgroundSelected by animateColor(targetColors.backgroundSelected)
    val backgroundSuccess by animateColor(targetColors.backgroundSuccess)
    val backgroundTeal by animateColor(targetColors.backgroundTeal)
    val backgroundWarning by animateColor(targetColors.backgroundWarning)
    val backgroundYellow by animateColor(targetColors.backgroundYellow)
    val borderBrandStatic by animateColor(targetColors.borderBrandStatic)
    val borderDisabled by animateColor(targetColors.borderDisabled)
    val borderErrored by animateColor(targetColors.borderErrored)
    val borderFocused by animateColor(targetColors.borderFocused)
    val borderFocusedInvertedStatic by animateColor(targetColors.borderFocusedInvertedStatic)
    val borderHovered by animateColor(targetColors.borderHovered)
    val borderHoveredInverted by animateColor(targetColors.borderHoveredInverted)
    val borderInverted by animateColor(targetColors.borderInverted)
    val borderInvertedStatic by animateColor(targetColors.borderInvertedStatic)
    val borderPrimary by animateColor(targetColors.borderPrimary)
    val borderPressed by animateColor(targetColors.borderPressed)
    val borderPressedInverted by animateColor(targetColors.borderPressedInverted)
    val borderSecondary by animateColor(targetColors.borderSecondary)
    val borderSelected by animateColor(targetColors.borderSelected)
    val graphicBlue by animateColor(targetColors.graphicBlue)
    val graphicGreen by animateColor(targetColors.graphicGreen)
    val graphicOrange by animateColor(targetColors.graphicOrange)
    val graphicPink by animateColor(targetColors.graphicPink)
    val graphicPurple by animateColor(targetColors.graphicPurple)
    val graphicTeal by animateColor(targetColors.graphicTeal)
    val graphicYellow by animateColor(targetColors.graphicYellow)
    val linkPrimary by animateColor(targetColors.linkPrimary)
    val linkSecondary by animateColor(targetColors.linkSecondary)
    val textBlue by animateColor(targetColors.textBlue)
    val textBrand by animateColor(targetColors.textBrand)
    val textDisabled by animateColor(targetColors.textDisabled)
    val textGreen by animateColor(targetColors.textGreen)
    val textInformation by animateColor(targetColors.textInformation)
    val textInverted by animateColor(targetColors.textInverted)
    val textInvertedStatic by animateColor(targetColors.textInvertedStatic)
    val textOrange by animateColor(targetColors.textOrange)
    val textPink by animateColor(targetColors.textPink)
    val textPrimary by animateColor(targetColors.textPrimary)
    val textPurple by animateColor(targetColors.textPurple)
    val textSecondary by animateColor(targetColors.textSecondary)
    val textSuccess by animateColor(targetColors.textSuccess)
    val textTeal by animateColor(targetColors.textTeal)
    val textYellow by animateColor(targetColors.textYellow)
    val textWarning by animateColor(targetColors.textWarning)

    val colors = ColorPalette(
        backgroundBlue = backgroundBlue,
        backgroundBrandStatic = backgroundBrandStatic,
        backgroundDisabled = backgroundDisabled,
        backgroundFocusedInvertedStatic = backgroundFocusedInvertedStatic,
        backgroundFocusedPrimary = backgroundFocusedPrimary,
        backgroundGreen = backgroundGreen,
        backgroundHoveredBrandStatic = backgroundHoveredBrandStatic,
        backgroundHoveredPrimaryInverted = backgroundHoveredPrimaryInverted,
        backgroundHoveredPrimaryInvertedStatic = backgroundHoveredPrimaryInvertedStatic,
        backgroundHoveredPrimary = backgroundHoveredPrimary,
        backgroundHoveredSecondary = backgroundHoveredSecondary,
        backgroundInformation = backgroundInformation,
        backgroundOrange = backgroundOrange,
        backgroundPink = backgroundPink,
        backgroundPressedBrandStatic = backgroundPressedBrandStatic,
        backgroundPressedPrimary = backgroundPressedPrimary,
        backgroundPressedPrimaryInverted = backgroundPressedPrimaryInverted,
        backgroundPressedPrimaryInvertedStatic = backgroundPressedPrimaryInvertedStatic,
        backgroundPressedSecondary = backgroundPressedSecondary,
        backgroundPrimaryBase = backgroundPrimaryBase,
        backgroundPrimaryHigh = backgroundPrimaryHigh,
        backgroundPrimaryInverted = backgroundPrimaryInverted,
        backgroundPrimaryInvertedStatic = backgroundPrimaryInvertedStatic,
        backgroundPrimary = backgroundPrimary,
        backgroundPrimaryLow = backgroundPrimaryLow,
        backgroundPurple = backgroundPurple,
        backgroundSecondaryBase = backgroundSecondaryBase,
        backgroundSecondaryInvertedStatic = backgroundSecondaryInvertedStatic,
        backgroundSecondary = backgroundSecondary,
        backgroundSelected = backgroundSelected,
        backgroundSuccess = backgroundSuccess,
        backgroundTeal = backgroundTeal,
        backgroundWarning = backgroundWarning,
        backgroundYellow = backgroundYellow,
        borderBrandStatic = borderBrandStatic,
        borderDisabled = borderDisabled,
        borderErrored = borderErrored,
        borderFocused = borderFocused,
        borderFocusedInvertedStatic = borderFocusedInvertedStatic,
        borderHovered = borderHovered,
        borderHoveredInverted = borderHoveredInverted,
        borderInverted = borderInverted,
        borderInvertedStatic = borderInvertedStatic,
        borderPrimary = borderPrimary,
        borderPressed = borderPressed,
        borderPressedInverted = borderPressedInverted,
        borderSecondary = borderSecondary,
        borderSelected = borderSelected,
        graphicBlue = graphicBlue,
        graphicGreen = graphicGreen,
        graphicOrange = graphicOrange,
        graphicPink = graphicPink,
        graphicPurple = graphicPurple,
        graphicTeal = graphicTeal,
        graphicYellow = graphicYellow,
        linkPrimary = linkPrimary,
        linkSecondary = linkSecondary,
        textBlue = textBlue,
        textBrand = textBrand,
        textDisabled = textDisabled,
        textGreen = textGreen,
        textInformation = textInformation,
        textInverted = textInverted,
        textInvertedStatic = textInvertedStatic,
        textOrange = textOrange,
        textPink = textPink,
        textPrimary = textPrimary,
        textPurple = textPurple,
        textSecondary = textSecondary,
        textSuccess = textSuccess,
        textTeal = textTeal,
        textYellow = textYellow,
        textWarning = textWarning
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
        primary = backgroundBrandStatic,
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

data class ColorPalette(
    val backgroundBlue: Color,
    val backgroundBrandStatic: Color,
    val backgroundDisabled: Color,
    val backgroundFocusedInvertedStatic: Color,
    val backgroundFocusedPrimary: Color,
    val backgroundGreen: Color,
    val backgroundHoveredBrandStatic: Color,
    val backgroundHoveredPrimaryInverted: Color,
    val backgroundHoveredPrimaryInvertedStatic: Color,
    val backgroundHoveredPrimary: Color,
    val backgroundHoveredSecondary: Color,
    val backgroundInformation: Color,
    val backgroundOrange: Color,
    val backgroundPink: Color,
    val backgroundPressedBrandStatic: Color,
    val backgroundPressedPrimary: Color,
    val backgroundPressedPrimaryInverted: Color,
    val backgroundPressedPrimaryInvertedStatic: Color,
    val backgroundPressedSecondary: Color,
    val backgroundPrimaryBase: Color,
    val backgroundPrimaryHigh: Color,
    val backgroundPrimaryInverted: Color,
    val backgroundPrimaryInvertedStatic: Color,
    val backgroundPrimary: Color,
    val backgroundPrimaryLow: Color,
    val backgroundPurple: Color,
    val backgroundSecondaryBase: Color,
    val backgroundSecondaryInvertedStatic: Color,
    val backgroundSecondary: Color,
    val backgroundSelected: Color,
    val backgroundSuccess: Color,
    val backgroundTeal: Color,
    val backgroundWarning: Color,
    val backgroundYellow: Color,
    val borderBrandStatic: Color,
    val borderDisabled: Color,
    val borderErrored: Color,
    val borderFocused: Color,
    val borderFocusedInvertedStatic: Color,
    val borderHovered: Color,
    val borderHoveredInverted: Color,
    val borderInverted: Color,
    val borderInvertedStatic: Color,
    val borderPrimary: Color,
    val borderPressed: Color,
    val borderPressedInverted: Color,
    val borderSecondary: Color,
    val borderSelected: Color,
    val graphicBlue: Color,
    val graphicGreen: Color,
    val graphicOrange: Color,
    val graphicPink: Color,
    val graphicPurple: Color,
    val graphicTeal: Color,
    val graphicYellow: Color,
    val linkPrimary: Color,
    val linkSecondary: Color,
    val textBlue: Color,
    val textBrand: Color,
    val textDisabled: Color,
    val textGreen: Color,
    val textInformation: Color,
    val textInverted: Color,
    val textInvertedStatic: Color,
    val textOrange: Color,
    val textPink: Color,
    val textPrimary: Color,
    val textPurple: Color,
    val textSecondary: Color,
    val textSuccess: Color,
    val textTeal: Color,
    val textYellow: Color,
    val textWarning: Color
)
