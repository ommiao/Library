package cn.ommiao.library.theme.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.ommiao.library.theme.foundation.Spacing
import cn.ommiao.library.theme.Appearance

private val TopBarElevation = 8.dp

@Composable
fun TopBar(
    backgroundColor: Color = Appearance.colors.backgroundPrimaryLow,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        color = backgroundColor,
        elevation = TopBarElevation,
        content = {
            Column {
                Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.Medium.dp()),
                    content = content
                )
            }
        }
    )
}
