package cn.ommiao.library.example.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.ommiao.library.theme.Appearance
import cn.ommiao.library.theme.contrastingTextColor
import cn.ommiao.library.theme.foundation.Spacing
import kotlin.reflect.full.memberProperties

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThemeScreen(theme: Appearance.Theme, onThemeChanged: (Appearance.Theme) -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Appearance.colors.backgroundPrimary)
    ) {
        val colors = Appearance.colors::class.memberProperties
        LazyColumn {
            items(colors.toList()) {
                val background = it.getter.call(Appearance.colors) as Color
                Box(
                    modifier = Modifier.fillMaxWidth().height(56.dp).background(background),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = it.name, color = background.contrastingTextColor())
                }
            }
        }
        LazyRow(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Medium.dp())
        ) {
            items(Appearance.Theme.values()) {
                val borderColor =
                    if (it == theme) Appearance.colors.borderSelected else Appearance.colors.borderPrimary
                FilterChip(
                    selected = it == theme,
                    onClick = { onThemeChanged(it) },
                    colors = ChipDefaults.filterChipColors(
                        backgroundColor = Appearance.colors.backgroundPrimary,
                        contentColor = Appearance.colors.textPrimary,
                        selectedBackgroundColor = Appearance.colors.backgroundSelected,
                        selectedContentColor = Appearance.colors.textInverted
                    ),
                    border = BorderStroke(width = 1.5.dp, color = borderColor)
                ) {
                    Text(
                        text = it.name,
                        modifier = Modifier.padding(Spacing.ExtraSmall.dp())
                    )
                }
            }
        }
    }
}
